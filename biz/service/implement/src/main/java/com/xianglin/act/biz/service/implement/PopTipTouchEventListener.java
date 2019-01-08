package com.xianglin.act.biz.service.implement;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.biz.shared.PopTipLogService;
import com.xianglin.act.biz.shared.PopTipTouchEvent;
import com.xianglin.core.service.PrizeAwardUtils;
import com.xianglin.act.biz.shared.TabTipCloseEvent;
import com.xianglin.act.common.dal.enums.PrizeEnum;
import com.xianglin.act.common.dal.mappers.CustomerAcquireRecordMapper;
import com.xianglin.act.common.dal.mappers.PrizeMapper;
import com.xianglin.act.common.dal.mappers.RedPacketMapper;
import com.xianglin.act.common.dal.mappers.RedPacketPartakerMapper;
import com.xianglin.act.common.dal.model.CustomerAcquire;
import com.xianglin.act.common.dal.model.Prize;
import com.xianglin.act.common.dal.model.redpacket.PartakerInfo;
import com.xianglin.act.common.dal.model.redpacket.RedPacket;
import com.xianglin.act.common.service.facade.constant.PopTipTypeEnum;
import com.xianglin.act.common.service.facade.model.ActivityDTO;
import com.xianglin.act.common.service.integration.MessageServiceClient;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.appserv.common.service.facade.model.Request;
import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.enums.Constant;
import com.xianglin.appserv.common.service.facade.model.vo.MsgVo;
import com.xianglin.core.model.enums.ActivityEnum;
import com.xianglin.core.model.enums.Constants;
import com.xianglin.core.model.enums.UserType;
import com.xianglin.act.common.util.BizException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

import static com.xianglin.act.common.service.facade.constant.PopTipTypeEnum.POP_TIP_OF_ONE_BUTTON;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/13 0:42.
 */
@Component
public class PopTipTouchEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PopTipTouchEventListener.class);

    private final ActivityEnum current_act = ActivityEnum.RED_PACKET_V2;

    @Autowired
    private PopTipLogService popTipLogService;

    @Resource
    private PrizeAwardUtils prizeAwardUtils;

    @Resource
    private CustomerAcquireRecordMapper customerAcquireRecordMapper;

    @Resource
    private RedPacketPartakerMapper redPacketPartakerMapper;

    @Resource
    private PrizeMapper prizeMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedPacketMapper redPacketMapper;

    @Resource
    private MessageServiceClient messageServiceClient;

    /**
     * 签到弹窗关闭事件
     *
     * @param event
     */
    @EventListener
    @Async
    public void signInTipCloseListener(TabTipCloseEvent event) {

        Integer popTipTYpe = event.getPopTipTYpe();
        if (popTipTYpe == PopTipTypeEnum.POP_TIP_OF_TAB.getCode()) {
            Long id = event.getId();
            Long partyId = event.getPartyId();
            ActivityDTO activityDTO = new ActivityDTO();
            activityDTO.setId(id);
            activityDTO.setPartyId(partyId);
            activityDTO.setShowType(popTipTYpe);
            popTipLogService.log(activityDTO);
            logger.info("===========签到弹窗已记录 popTipTYpe:[[ {} ]],actId:[[ {} ]],partyId:[[ {} ]],===========", partyId, id, popTipTYpe);
        }
    }

    /**
     * 活动结果打开事件
     *
     * @param event
     */
    @EventListener
    @Async
    public void activityResultOpenListener(PopTipTouchEvent event) {

        if (event.getPopTipTYpe() == POP_TIP_OF_ONE_BUTTON.getCode()) {
            RLock lock = redissonClient.getLock("ACT:RED_PACKET:PARTAKER:" + event.getPartyId());
            if (!lock.tryLock()) {
                throw new BizException(ActPreconditions.ResponseEnum.REPEAT);
            }
            try {
                PartakerInfo partakerInfo = redPacketPartakerMapper.selectLastUnexpireRedPacket(event.getPartyId());
                if (partakerInfo == null) {
                    throw new IllegalStateException("not found partaker info partyId: " + event.getPartyId());
                }
                if (partakerInfo.isRemind()) {
                    throw new IllegalStateException("partaker already remind: " + event.getPartyId());
                }

                Prize prize = prizeMapper.selectActivityPrize(current_act.name(), partakerInfo.getPrizeCode());
                prize.setAmount(partakerInfo.getPrizeValue());
                prize.setPrizeEnum(PrizeEnum.parse(partakerInfo.getPrizeCode()));

                // 发放金币
                prizeAwardUtils.award(partakerInfo, prize);
                // 插入流水
                CustomerAcquire customerAcquire = new CustomerAcquire();
                customerAcquire.setMobilePhone(partakerInfo.getMobilePhone());
                customerAcquire.setPartyId(partakerInfo.getPartyId());
                customerAcquire.setPrizeCode(prize.getPrizeCode());
                customerAcquire.setActivityCode(current_act.name());
                customerAcquire.setMemcCode(prize.getMemcCode());
                customerAcquire.setHeadImageUrl(Constants.USER_DEFAULT_HEAD);
                customerAcquire.setPrizeValue(prize.getAmount());
                customerAcquire.setUserType(UserType.RP_PARTAKER.name());

                customerAcquireRecordMapper.insertCustomerAcquireRecord(customerAcquire);
                // 更新已弹窗
                redPacketPartakerMapper.updateRemind(
                        event.getPartyId(),
                        partakerInfo.getPacketId(),
                        prize.getMemcCode()
                );

                // 更新团是否完成
                int i = redPacketMapper.updateRedPacketCompletion(partakerInfo.getPacketId());
                if (i > 0) {
                    RedPacket redPacket = redPacketMapper.selectRedPacket(partakerInfo.getPacketId());
                    if ("Y".equals(redPacket.getIsComplete())) {
                        Request<MsgVo> param = new Request<>();
                        param.setReq(MsgVo.builder()
                                .partyId(redPacket.getPartyId())
                                .msgTitle("红包活动通知")
                                .isSave(Constant.YESNO.YES)
                                .message("你发出的红包已被好友领取，赶紧去APP领取红包吧！")
                                .msgType(Constant.MsgType.CASHBONUS_TIP.name())
                                .loginCheck(Constant.YESNO.NO.code)
                                .passCheck(Constant.YESNO.NO.code).expiryTime(0)
                                .isDeleted("N")
                                .msgSource(Constant.MsgType.CASHBONUS_TIP.name()).build());

                        Response<Boolean> booleanResponse =
                                messageServiceClient.sendMsg(param, Arrays.asList(new Long[]{redPacket.getPartyId()}));

                        com.google.common.base.Optional<Boolean> aBoolean = Response.checkResponse(booleanResponse);
                        if (!(aBoolean.get())) {
                            logger.error("给分享者发送红包成功消息失败：{}", JSON.toJSONString(booleanResponse));
                        }
                    }
                }
            } finally {
                lock.unlock();
            }
        } else {
            logger.warn("un supported event code: {} ", event.getId());
        }
    }
}
