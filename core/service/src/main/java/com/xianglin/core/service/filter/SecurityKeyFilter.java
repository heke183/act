package com.xianglin.core.service.filter;

import com.google.common.base.Objects;
import com.xianglin.act.common.dal.enums.lucky.wheel.NewCustomPrize;
import com.xianglin.act.common.dal.mappers.PrizeMapper;
import com.xianglin.act.common.dal.model.Prize;
import com.xianglin.core.model.Player;
import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;
import com.xianglin.core.model.enums.CustomerTypeEnum;
import com.xianglin.core.service.ActivityContext;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 * @date 2018-02-02 14:22
 */
public class SecurityKeyFilter implements Filter {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(SecurityKeyFilter.class);

    private final static Long EXPIRE = 3600 * 24L;

    private final static String PREFIX = "ACT:ATOMIC:";

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private PrizeMapper prizeMapper;

    @Override
    public void doFilter(ActivityRequest<?> request, ActivityResponse response, FilterChain filterChain) {
        Player player = (Player) request.getRequest();
        // NEW_CUSTOMER LIMIT
        if (CustomerTypeEnum.NEW_CUSTOMER == player.getCustomerType()) {
            if (Objects.equal(request.getSecurityKey(), null)) {
                Prize prize = prizeMapper.selectActivityPrize(ActivityContext.getActivityCode(), NewCustomPrize.FOURTH_PRIZE.name());
                response.setPrize(prize);
            } else {
                RAtomicLong atomicLong = redissonClient.getAtomicLong(PREFIX + request.getSecurityKey());
                // 竞争条件不影响设置有效期
                if (atomicLong.get() == 0) {
                    atomicLong.expire(EXPIRE, TimeUnit.SECONDS);
                }
                logger.info("----------> SecurityKeyFilter :{}, count: {}", request.getIp(), atomicLong.get());
                if (atomicLong.incrementAndGet() > 5) {
                    Prize prize = prizeMapper.selectActivityPrize(ActivityContext.getActivityCode(), NewCustomPrize.FOURTH_PRIZE.name());
                    response.setPrize(prize);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
