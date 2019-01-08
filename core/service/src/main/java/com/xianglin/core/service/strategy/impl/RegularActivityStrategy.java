package com.xianglin.core.service.strategy.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Objects;
import com.xianglin.act.common.dal.enums.lucky.wheel.RegularCustomPrize;
import com.xianglin.act.common.dal.mappers.ActPlantLvTranMapper;
import com.xianglin.act.common.dal.mappers.ActPlantMapper;
import com.xianglin.act.common.dal.mappers.PrizeMapper;
import com.xianglin.act.common.dal.mappers.SequenceMapper;
import com.xianglin.act.common.dal.model.ActPlant;
import com.xianglin.act.common.dal.model.ActPlantLvTran;
import com.xianglin.act.common.dal.model.CustomerAcquire;
import com.xianglin.act.common.dal.model.CustomerPrize;
import com.xianglin.act.common.service.integration.GoldcoinServiceClient;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.BizException;
import com.xianglin.act.common.util.GoldSequenceUtil;
import com.xianglin.cif.common.service.facade.model.Response;
import com.xianglin.cif.common.service.facade.vo.GoldcoinAccountVo;
import com.xianglin.cif.common.service.facade.vo.GoldcoinRecordVo;
import com.xianglin.core.model.Player;
import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;
import com.xianglin.core.model.enums.ActPlantEnum;
import com.xianglin.core.model.enums.CustomerTypeEnum;
import com.xianglin.core.model.enums.UserType;
import com.xianglin.core.service.strategy.AbstractActivityStrategy;
import com.xianglin.core.service.strategy.ActivityStrategy;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.xianglin.core.service.ActivityContext.getActivityCode;
import static com.xianglin.core.service.ActivityContext.getActivityName;

/**
 * The type Regular activity strategy.
 *
 * @author yefei
 * @date 2018 -01-18 16:12
 */
public class RegularActivityStrategy extends AbstractActivityStrategy implements ActivityStrategy {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(RegularActivityStrategy.class);

    private final static String LOCK_GOLD = "ACT:LOCK:GOLD:";

    private final static String LOCK_LV = "ACT:LOCK:LV:";
    /**
     * 老用户每次抽奖消耗金币
     */
    private final static Integer GOLD_USE = -50;

    /**
     * 老用户每次抽奖消耗爱心值
     */

    private final static Integer LV_USE = -20;

    private final RedissonClient redissonClient;

    @Resource
    private PrizeMapper prizeMapper;

    @Resource
    private GoldcoinServiceClient goldcoinServiceClient;

    @Resource
    private SequenceMapper sequenceMapper;

    @Resource
    private ActPlantMapper actPlantMapper;

    @Resource
    private ActPlantLvTranMapper actPlantLvTranMapper;

    public RegularActivityStrategy(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    protected void beforeProcess(ActivityRequest<Player> request, ActivityResponse response) {
        Player player = request.getRequest();
        //useGold(player);
        useLv(player);
    }

    @Override
    protected void afterProcess(ActivityRequest<Player> request, ActivityResponse response) {
        Player player = request.getRequest();
        //checkPrizeLevel(player, response);
        checkCount(response);
        insertAcquireRecord(player, response, true);
    }

    /**
     * 获得2等奖的人之后不可获得3等奖
     * 获得3等奖的人不可再获得2、3等奖
     *
     * @param player
     * @param response
     */
    private void checkPrizeLevel(Player player, ActivityResponse response) {
        String prizeCode = response.getPrize().getPrizeCode();
        // 抽中二等奖 验证是否抽过三等奖
        if (RegularCustomPrize.SECOND_PRIZE.name().equals(prizeCode)) {
            List<CustomerAcquire> prize = customerAcquireRecordMapper.selectCustomerAcquired(
                    getActivityCode(),
                    player.getPartyId(),
                    RegularCustomPrize.THIRD_PRIZE.name(),
                    UserType.REGULAR_CUSTOMER.name()
            );
            if (prize.size() > 0) {
                randomPrize(response);
            }
        }
        // 抽中三等奖 验证是否抽过 二等奖 或者 三等奖
        if (RegularCustomPrize.THIRD_PRIZE.name().equals(prizeCode)) {
            List<CustomerAcquire> prize = customerAcquireRecordMapper.selectCustomerPrizeGreaterThan(
                    getActivityCode(),
                    player.getPartyId(),
                    RegularCustomPrize.THIRD_PRIZE.name(),
                    UserType.REGULAR_CUSTOMER.name()
            );
            if (prize.size() > 0) {
                randomPrize(response);
            }
        }
    }

    /**
     * 使用金币
     *
     * @param player
     */
    private void useGold(Player player) {

        // 竞争条件
        RLock lock = redissonClient.getLock(LOCK_GOLD + player.getPartyId());
        lock.lock();
        try {
            Response<GoldcoinAccountVo> goldcoinAccountVoResponse = goldcoinServiceClient.queryAccount(player.getPartyId());
            logger.debug("查询金币， result: {}", goldcoinAccountVoResponse.getTips());

            if (!Objects.equal(goldcoinAccountVoResponse.getCode(), 1000)
                    || Objects.equal(goldcoinAccountVoResponse.getResult(), null)) {
                logger.error("查询用户金币失败： {}", JSON.toJSONString(goldcoinAccountVoResponse));
                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
            }

            if (goldcoinAccountVoResponse.getResult().getAmount() < -GOLD_USE) {
                throw new BizException(ActPreconditions.ResponseEnum.GOLD_NOT_ENOUGH);
            }
            GoldcoinRecordVo goldcoinRecordVo = new GoldcoinRecordVo();
            goldcoinRecordVo.setSystem("act");
            goldcoinRecordVo.setAmount(GOLD_USE);
            goldcoinRecordVo.setFronPartyId(GOLD_SYS_ACCOUNT);
            goldcoinRecordVo.setType(getActivityCode());
            goldcoinRecordVo.setRemark(getActivityName() + "抽奖");
            goldcoinRecordVo.setToPartyId(player.getPartyId());
            goldcoinRecordVo.setRequestId(GoldSequenceUtil.getSequence(player.getPartyId(), sequenceMapper.getSequence()));

            Response<GoldcoinRecordVo> goldcoinRecordVoResponse = goldcoinServiceClient.doRecord(goldcoinRecordVo);
            logger.debug("金币交易， result: {}", goldcoinRecordVoResponse.getTips());
            if (!Objects.equal(goldcoinAccountVoResponse.getCode(), 1000)) {
                logger.error("用户金币添加失败，{}", JSON.toJSONString(goldcoinRecordVoResponse));
                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }
    }

    private void useLv(Player player) {
        // 竞争条件
        RLock lock = redissonClient.getLock(LOCK_LV + player.getPartyId());
        lock.lock();
        try {
            ActPlant actPlant = actPlantMapper.findByPartyId(player.getPartyId());
            if(actPlant == null){
                logger.error("查询爱心值失败：{}");
                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
            }

            if (actPlant.getLv() < -LV_USE) {
                throw new BizException(ActPreconditions.ResponseEnum.LV_NOT_ENOUGH);
            }
            //减当前用户的爱心值
            actPlant.setLv(actPlant.getLv() + LV_USE);
            actPlant.setUpdateTime(new Date());
            actPlantMapper.updateByPrimaryKeySelective(actPlant);

            //同时保存爱心交易明细一条记录
            actPlantLvTranMapper.insertSelective(ActPlantLvTran.builder().partyId(player.getPartyId())
                    .lv(+LV_USE)
                    .lvId(null)
                    .isDeleted("N").status(ActPlantEnum.StatusType.S.name()).type(ActPlantEnum.TranType.LUCKDRAW.name()).build());
        } catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 后置处理：验证可用奖品个数
     *
     * @return
     */
    private void checkCount(ActivityResponse response) {
        final String prizeCode = response.getPrize().getPrizeCode();
        if (RegularCustomPrize.LUCKY_PRIZE.name().equals(prizeCode)
                || RegularCustomPrize.EVERY_PRIZE.name().equals(prizeCode)) {
            return;
        }
        //查询奖品是否有上限
        CustomerPrize customerPrize1 = customerPrizeMapper.selectCustomerPrizeUnique(response.getPrize().getActivityCode(), CustomerTypeEnum.REGULAR_CUSTOMER.name(), response.getPrize().getPrizeCode());
        if(customerPrize1.getAmount() == -1){ //没有上限
            return;
        }
        CustomerPrize customerPrize = new CustomerPrize();
        customerPrize.setCustomerType(CustomerTypeEnum.REGULAR_CUSTOMER.name());
        customerPrize.setPrizeCode(prizeCode);
        // 奖品个数
        int availableAmount = customerPrizeMapper.updateCustomerPrizeAvailableAmount(customerPrize);
        // 礼品如被全部抽完，默认二等奖、六等奖随机发
        if ((availableAmount == 0)) {
            randomPrize(response);
        }
    }

    private void randomPrize(ActivityResponse response) {
        final RegularCustomPrize luckyPrize;
        if (ThreadLocalRandom.current().nextInt() % 2 == 0) {
            luckyPrize = RegularCustomPrize.SECOND_PRIZE;
        } else {
            luckyPrize = RegularCustomPrize.SIXTH_PRIZE;
        }
        response.setPrize(prizeMapper.selectActivityPrize(getActivityCode(), luckyPrize.name()));
    }

}
