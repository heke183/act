package com.xianglin.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.xianglin.act.common.dal.enums.lucky.wheel.NewCustomPrize;
import com.xianglin.act.common.dal.mappers.*;
import com.xianglin.act.common.dal.model.*;
import com.xianglin.act.common.service.integration.CustomersInfoServiceClient;
import com.xianglin.act.common.util.DateUtils;
import com.xianglin.act.common.util.annotation.MqListener;
import com.xianglin.cif.common.service.facade.model.CustomersDTO;
import com.xianglin.core.model.enums.ActPlantEnum;
import com.xianglin.core.model.enums.Constants;
import com.xianglin.core.model.enums.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class RegisterAward {

    private static final Logger logger = LoggerFactory.getLogger(RegisterAward.class);

    private static final String KEY_PARTY_ID = "partyId";

    private static final String KEY_ROLE_CODE = "roleCode";

    private static final String ROLE_APP_USER = Constants.APP_USER;

    @Resource
    private CustomerAcquireRecordMapper customerAcquireRecordMapper;

    @Resource
    private PrizeAwardUtils prizeAwardUtils;

    @Resource
    private PrizeMapper prizeMapper;

    @Autowired
    private ActPlantTaskDetailMapper actPlantTaskDetailMapper;

    @Autowired
    private ActPlantTipMapper actPlantTipMapper;

    @Resource
    private CustomersInfoServiceClient customersInfoServiceClient;

    @Autowired
    private ActPlantMapper actPlantMapper;

    @Autowired
    private ActPlantLvMapper actPlantLvMapper;

    @Autowired
    private ActPlantLvTranMapper actPlantLvTranMapper;

    @Autowired
    private ActInviteDetailMapper actInviteDetailMapper;

    @Autowired
    private ConfigMapper configMapper;


    @MqListener(topic = "CIF_TOPIC", tag = "BINDING_ROLE")
    public boolean resolveRegisterAward(MessageExt msgs, ConsumeConcurrentlyContext context, String body) {

        JSONObject jsonObject = JSON.parseObject(body);
        Long partyId = jsonObject.getLong(KEY_PARTY_ID);
        String roleCode = jsonObject.getString(KEY_ROLE_CODE);
        if (ROLE_APP_USER.equals(roleCode)) {
            List<CustomerAcquire> acquires = customerAcquireRecordMapper.selectCustomerAcquired(
                    "LUCKY_WHEEL_V2",
                    partyId,
                    NewCustomPrize.FIFTH_PRIZE_NEW.name(),
                    UserType.NEW_CUSTOMER.name()
            );
            if (acquires.size() == 1) {
                CustomerAcquire customerAcquire = acquires.get(0);
                // 48 小时内
                if ((customerAcquire.getAcquireDate().getTime() + 48 * 3600 * 1000) > System.currentTimeMillis()) {
                    Prize prize = prizeMapper.selectActivityPrize("LUCKY_WHEEL_V2", NewCustomPrize.FIFTH_PRIZE_NEW.name());
                    prize.setAmount(customerAcquire.getPrizeValue());
                    prizeAwardUtils.award(Party.crateParty(partyId), prize);

                    // update
                    customerAcquire.setMemcCode(prize.getMemcCode());
                    customerAcquireRecordMapper.updateCustomerPrizeMemo(customerAcquire);
                }
            }
            //TODO wanglei
            logger.info(" user Login {},{}", partyId, roleCode);
            actPlantTaskDetailMapper.select(ActPlantTaskDetail.builder().refId(partyId + "").code(ActPlantEnum.ActPlantTaskCodeEnum.INVITE.desc).build())
                    .stream().findFirst().ifPresent(v -> {
                //将邀请记录修改成邀请成功的状态
                actPlantTaskDetailMapper.updateByPrimaryKeySelective(ActPlantTaskDetail.builder().updateTime(new Date()).id(v.getId()).status("S").build());
                //给邀请人账户添加100
                ActPlant byPartyId = actPlantMapper.findByPartyId(v.getPartyId());
                byPartyId.setLv(byPartyId.getLv() + 100);
                byPartyId.setTotalLv(byPartyId.getTotalLv() + 100);
                byPartyId.setUpdateTime(new Date());
                actPlantMapper.updateByPrimaryKey(byPartyId);
                //给邀请人添加100爱心值
                ActPlantLv actPlantLv = ActPlantLv.builder().partyId(byPartyId.getPartyId()).lv(0).totalLv(100).status("S").matureTime(new Date()).expireTime(DateUtils.skipDateTime(new Date(), 365)).type(ActPlantEnum.ActPlantTaskTypeEnum.USERTASK.name()).shouTime(new Date()).taskId(v.getId()).build();
                actPlantLvMapper.insertSelective(actPlantLv);

                //添加邀请人100入账明细plantlvtran
                actPlantLvTranMapper.insertSelective(ActPlantLvTran.builder().partyId(byPartyId.getPartyId()).lvId(actPlantLv.getId()).lv(100).type(ActPlantEnum.TranType.COLLECT.name()).status("S").build());
                actPlantTipMapper.insertSelective(ActPlantTip.builder().partyId(v.getPartyId()).type(ActPlantEnum.TipType.TIP.name())
                        .tip("已邀请用户<b>" + customersInfoServiceClient.selectByPartyId(partyId).getResult().getMobilePhone() + "</b>").build());
            });

            synInviteDetail(partyId);
        }
        return true;

    }

    /**
     * 同步邀请信息
     *
     * @param partyId
     */
    private void synInviteDetail(Long partyId) {
        try {
            logger.info(" user synInviteDetail {}", partyId);
            String str = configMapper.selectConfig("ACT_INVITE_TIME");
            Map result = JSON.parseObject(str);
            Date stop = DateUtils.formatStr(result.get("stopTime").toString(), "yyyy-MM-dd HH:mm:ss");
            if (new Date().before(stop)) {
                Optional<ActInviteDetail> detail = actInviteDetailMapper.select(ActInviteDetail.builder().partyId(partyId).build()).stream().sorted((v1, v2) -> v2.getId().intValue() - v1.getId().intValue()).findFirst();
                if (detail.isPresent()) {
                    actInviteDetailMapper.updateByPrimaryKeySelective(ActInviteDetail.builder().id(detail.get().getId()).status(ActPlantEnum.StatusType.S.name()).updateTime(new Date()).build());
                    CustomersDTO customersDTO = new CustomersDTO();
                    customersDTO.setPartyId(partyId);
                    customersDTO.setInvitationPartyId(detail.get().getRecPartyId());
                    customersInfoServiceClient.syncInvitationCustomer(null);
                } else {
                    Optional.ofNullable(customersInfoServiceClient.selectByPartyId(partyId).getResult()).ifPresent(v -> {
                        if (v.getInvitationPartyId() != null && v.getInvitationPartyId() != 0) {
                            actInviteDetailMapper.insertSelective(ActInviteDetail.builder().partyId(partyId).recPartyId(v.getInvitationPartyId()).status(ActPlantEnum.StatusType.S.name()).build());
                        }
                    });
                }
            }
            actInviteDetailMapper.updateInviteDetail();
        } catch (Exception e) {
            logger.warn("synInviteDetail ", e);
        }
    }
}
