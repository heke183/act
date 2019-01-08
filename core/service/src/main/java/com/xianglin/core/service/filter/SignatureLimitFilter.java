package com.xianglin.core.service.filter;

import com.google.common.base.Objects;
import com.xianglin.act.common.dal.enums.lucky.wheel.NewCustomPrize;
import com.xianglin.act.common.dal.mappers.PrizeMapper;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.BizException;
import com.xianglin.act.common.util.DateUtils;
import com.xianglin.act.common.util.MD5;
import com.xianglin.core.model.Player;
import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;
import com.xianglin.core.model.enums.CustomerTypeEnum;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.xianglin.core.service.ActivityContext.getActivityCode;

/**
 * 用户token 拦截
 *
 * @author yefei
 * @date 2018-01-26 11:05
 */
public class SignatureLimitFilter implements Filter {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(SignatureLimitFilter.class);

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
            String formatDate = DateUtils.formatDate(DateUtils.getNow(), DateUtils.DATE_TPT_TWO);
            try {
                // partyId 和 当天日期 yyyyMMdd md5验证
                if (!Objects.equal(request.getSignature(), MD5.encode(player.getMobilePhone() + formatDate))) {
                    response.setPrize(prizeMapper.selectActivityPrize(getActivityCode(), NewCustomPrize.FOURTH_PRIZE.name()));
                } else {
                    RAtomicLong atomicLong = redissonClient.getAtomicLong(PREFIX + request.getSignature());
                    // 竞争条件不影响设置有效期
                    if (atomicLong.get() == 0) {
                        atomicLong.expire(EXPIRE, TimeUnit.SECONDS);
                    }
                    logger.info("----------> SignatureLimitFilter :{}, count: {}", request.getIp(), atomicLong.get());
                    if (atomicLong.incrementAndGet() > 5) {
                        response.setPrize(prizeMapper.selectActivityPrize(getActivityCode(), NewCustomPrize.FOURTH_PRIZE.name()));
                    }
                }
            } catch (Exception e) {
                logger.error("md5 error!", e);
                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
            }
        }
        filterChain.doFilter(request, response);
    }

}
