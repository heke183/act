package com.xianglin.core.service.strategy;

import com.xianglin.act.common.dal.mappers.*;
import com.xianglin.act.common.dal.model.*;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.core.model.Player;
import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;
import com.xianglin.core.service.PrizeAwardUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.xianglin.act.common.util.ActPreconditions.ResponseEnum.PRIZE_ERROR;
import static com.xianglin.core.service.ActivityContext.getActivityCode;

/**
 * @author yefei
 * @date 2018-01-24 11:17
 */
public abstract class AbstractActivityStrategy implements ActivityStrategy {

    protected final static long GOLD_SYS_ACCOUNT = 10000L;
    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(AbstractActivityStrategy.class);
    private final static String LOCK = "ACT:LOCK:GRAND_PRIZE";

    @Resource
    protected CustomerPrizeMapper customerPrizeMapper;

    @Resource
    protected PrizeProbabilityConfigMapper prizeProbabilityConfigMapper;

    @Resource
    protected PrizeConfigMapper prizeConfigMapper;

    @Resource
    protected CustomerAcquireRecordMapper customerAcquireRecordMapper;

    @Resource
    protected RedissonClient redissonClient;

    @Resource
    protected PrizeMapper prizeMapper;

    @Resource
    private PrizeAwardUtils prizeAwardUtils;

    @Override
    public void handle(ActivityRequest<Player> request, ActivityResponse response) {
        if (logger.isDebugEnabled()) {
            logger.debug("抽奖处理 参数：{}", request);
        }

        beforeProcess(request, response);
        Player player = request.getRequest();

        // 奖品为空 抽大奖
        if (com.google.common.base.Objects.equal(response.getPrize(), null)) {
            //grandPrize(request, response);
        }
        // 是否中大奖
        if (com.google.common.base.Objects.equal(response.getPrize(), null)) {
            List<CustomerPrize> customerPrizes = customerPrizeMapper.selectCustomerPrize(
                    getActivityCode(),
                    player.getCustomerType().name());
            ActPreconditions.checkCondition(customerPrizes.isEmpty(), PRIZE_ERROR);
            if (customerPrizes.size() == 1) {
                final Prize prize = prizeMapper.selectActivityPrize(
                        getActivityCode(),
                        customerPrizes.get(0).getPrizeCode());
                ActPreconditions.checkCondition(prize == null, PRIZE_ERROR);
                prize.setAmount(customerPrizes.get(0).getMinValue());
                response.setPrize(prize);
            } else {
                BigDecimal random = new BigDecimal(Math.random());
                logger.info("act_handle_random:"+random);
                CustomerPrize acquiredPrize = null;
                // 加权随机算法 把概率看成权重
                for (int i = 0; i < customerPrizes.size(); i++) {
                    /*acquiredPrize = customerPrizes.get(i);
                    if (random.subtract(acquiredPrize.getProbability()).floatValue() < 0) {
                        break;
                    }*/
                    if(random.compareTo(customerPrizes.get(i).getProbability()) == -1 && random.compareTo(customerPrizes.get(i).getInitialProbability()) >= 0){
                        acquiredPrize = customerPrizes.get(i);
                        break;
                    }
                }
                if (acquiredPrize == null) {
                    acquiredPrize = customerPrizes.get(customerPrizes.size() - 1);
                }
                Prize prize = prizeMapper.selectActivityPrize(getActivityCode(), acquiredPrize.getPrizeCode());
                ActPreconditions.checkCondition(prize == null, PRIZE_ERROR);
                prize.setAmount(acquiredPrize.getMinValue());
                response.setPrize(prize);
            }
        }
        afterProcess(request, response);
    }

    public void grandPrize(ActivityRequest<Player> request, ActivityResponse response) {
        Player player = request.getRequest();
        BigDecimal random = new BigDecimal(Math.random());
        PrizeProbabilityConfig prizeProbabilityConfig = prizeProbabilityConfigMapper.selectAddProbability(player.getCustomerType().name());
        // 抽中大奖
        if (random.compareTo(prizeProbabilityConfig.getAddProbability()) < 0) {
            // 竞争条件
            RLock lock = redissonClient.getLock(LOCK);
            lock.lock();
            try {
                doGrandPrize(request, response);
            } catch (Exception e) {
                throw e;
            } finally {
                lock.unlock();
            }
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void doGrandPrize(ActivityRequest<Player> request, ActivityResponse response) {

        Player player = request.getRequest();
        String customerType = player.getCustomerType().name();
        // 抽中后不管是否符合条件都回归原始概率
        prizeProbabilityConfigMapper.resetProbability(customerType);

        List<PrizeConfig> prizeConfigs = prizeConfigMapper.selectGrandPrize(customerType);
        if (prizeConfigs.size() > 0) {
            int i = (int) (Math.random() * prizeConfigs.size());
            PrizeConfig prizeConfig = prizeConfigs.get(i);
            Prize prize = prizeMapper.selectActivityPrize(getActivityCode(), prizeConfig.getPrizeCode());
            response.setPrize(prize);
            // 校验是否符合
            //checkEmployeeAndRegularCustomer(player, response);

            if (Objects.equals(response.getPrize().getPrizeEnum().name(), prizeConfig.getPrizeCode())) {
                // 随机取一个大奖并删除配置
                prizeConfigMapper.deleteGrandPrizeConfig(prizeConfig.getId());
            }
        }
    }

    protected void insertAcquireRecord(Player player, ActivityResponse response, boolean isAward) {
        final Prize prize = response.getPrize();
        if (prize.getAmount() == null) {
            final CustomerPrize customerPrize = customerPrizeMapper.selectCustomerPrizeUnique(
                    getActivityCode(),
                    player.getCustomerType().name(),
                    prize.getPrizeCode()
            );
            ActPreconditions.checkCondition(customerPrize == null, PRIZE_ERROR);
            prize.setAmount(customerPrize.getMinValue());
        }

        if (isAward) {
            // 发放奖励
            prizeAwardUtils.award(player, prize);
        }

        CustomerAcquire customerAcquire = CustomerAcquire.builder()
                .userType(player.getCustomerType().name())
                .mobilePhone(player.getMobilePhone())
                .partyId(player.getPartyId())
                .prizeCode(prize.getPrizeCode())
                .prizeValue(prize.getAmount())
                .activityCode(getActivityCode())
                .memcCode(prize.getMemcCode()).build();

        customerAcquireRecordMapper.insertCustomerAcquireRecord(customerAcquire);
    }

    /**
     * 前置处理
     *
     * @param request
     * @param response
     */
    protected abstract void beforeProcess(ActivityRequest<Player> request, ActivityResponse response);


    /**
     * 后置处理
     *
     * @param request
     * @param response
     */
    protected abstract void afterProcess(ActivityRequest<Player> request, ActivityResponse response);

}
