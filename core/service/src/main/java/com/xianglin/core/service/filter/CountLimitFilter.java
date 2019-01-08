package com.xianglin.core.service.filter;


import com.xianglin.act.common.dal.mappers.CustomerAcquireRecordMapper;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.core.model.Player;
import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;
import com.xianglin.core.model.enums.CustomerTypeEnum;
import com.xianglin.act.common.util.BizException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import static com.xianglin.core.service.ActivityContext.getActivityCode;


/**
 * @author yefei
 * @date 2018-01-25 10:23
 */
public class CountLimitFilter implements Filter {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(CountLimitFilter.class);

    private final static String LOCK = "ACT:LOCK:";
    @Resource
    private CustomerAcquireRecordMapper customerAcquireRecordMapper;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public void doFilter(ActivityRequest<?> request, ActivityResponse response, FilterChain filterChain) {
        Player player = (Player) request.getRequest();
        // 新用户验证抽奖次数
        if (CustomerTypeEnum.NEW_CUSTOMER == player.getCustomerType()) {
            // 单用户竞争条件
            RLock lock = redissonClient.getLock(LOCK + player.getPartyId());
            lock.lock();
            try {
                logger.info("CountLimitFilter 验证抽奖次数， customerType: {} ", player.getCustomerType());
                long count = customerAcquireRecordMapper.selectNewCustomerRecordCount(player.getPartyId(), getActivityCode());
                if (count >= 1) {
                    throw new BizException(ActPreconditions.ResponseEnum.ALREADY_PLAY);
                }
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                throw e;
            } finally {
                lock.unlock();
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

}
