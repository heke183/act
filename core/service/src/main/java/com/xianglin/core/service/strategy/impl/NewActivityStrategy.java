package com.xianglin.core.service.strategy.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Objects;
import com.xianglin.act.common.dal.enums.lucky.wheel.NewCustomPrize;
import com.xianglin.act.common.dal.mappers.PrizeMapper;
import com.xianglin.act.common.dal.model.CustomerPrize;
import com.xianglin.act.common.dal.model.Prize;
import com.xianglin.act.common.service.integration.ActivityInviteServiceClient;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.BizException;
import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.vo.ActivityInviteDetailVo;
import com.xianglin.core.model.Player;
import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;
import com.xianglin.core.model.enums.CustomerTypeEnum;
import com.xianglin.core.service.strategy.AbstractActivityStrategy;
import com.xianglin.core.service.strategy.ActivityStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import static com.xianglin.act.common.util.ActPreconditions.ResponseEnum.PRIZE_ERROR;
import static com.xianglin.core.service.ActivityContext.getActivityCode;

/**
 * The type New activity strategy.
 *
 * @author yefei
 * @date 2018 -01-18 16:14
 */
public class NewActivityStrategy extends AbstractActivityStrategy implements ActivityStrategy {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(NewActivityStrategy.class);

    @Resource
    private ActivityInviteServiceClient activityInviteServiceClient;

    @Resource
    private PrizeMapper prizeMapper;

    @Override
    protected void beforeProcess(ActivityRequest<Player> request, ActivityResponse response) {
        beforeProcess(request.getRequest());
    }

    @Override
    protected void afterProcess(ActivityRequest<Player> request, ActivityResponse response) {
        checkCount(response);

        // 新用户获取 五等奖1000金币， 需要48小时内注册，才给发放
        if (NewCustomPrize.FIFTH_PRIZE_NEW.name().equals(response.getPrize().getPrizeCode())) {
            insertAcquireRecord(request.getRequest(), response, false);
        } else {
            insertAcquireRecord(request.getRequest(), response, true);
        }
    }

    /**
     * 新用户前置处理 建立推荐关系
     *
     * @param player
     */
    private void beforeProcess(Player player) {
        if (Objects.equal(player.getFromPartyId(), null)) {
            BizException bizException = new BizException(ActPreconditions.ResponseEnum.PARTY_IS_NULL);
            logger.error("from party id is null！", bizException);
            throw bizException;
        }
        // 分享添加金币
        ActivityInviteDetailVo vo = new ActivityInviteDetailVo();
        vo.setSource("大转盘");
        vo.setLoginName(player.getMobilePhone());
        vo.setRecPartyId(player.getFromPartyId());
        vo.setActivityCode("109");
        com.xianglin.appserv.common.service.facade.model.Response<Boolean> invite = activityInviteServiceClient.invite(vo);
        com.google.common.base.Optional<Boolean> aBoolean = Response.checkResponse(invite);
        logger.info("添加分享记录： {}", JSON.toJSONString(invite));
        if (Objects.equal(invite, null) || !aBoolean.get()) {
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }

    }

    /**
     * 后置处理：验证可用奖品个数
     */
    private void checkCount(ActivityResponse response) {
        if (NewCustomPrize.FOURTH_PRIZE.name().equals(response.getPrize().getPrizeCode())) {
            return;
        }
        CustomerPrize customerPrize = new CustomerPrize();
        customerPrize.setCustomerType(CustomerTypeEnum.NEW_CUSTOMER.name());
        customerPrize.setPrizeCode(response.getPrize().getPrizeCode());
        // 奖品个数
        int availableAmount = customerPrizeMapper.updateCustomerPrizeAvailableAmount(customerPrize);
        // 奖品个数不足给四等奖
        if ((availableAmount == 0)) {
            Prize prize = prizeMapper.selectActivityPrize(getActivityCode(), NewCustomPrize.FOURTH_PRIZE.name());
            ActPreconditions.checkCondition(prize == null, PRIZE_ERROR);
            response.setPrize(prize);
        }
    }
}
