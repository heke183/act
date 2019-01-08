package com.xianglin.core.service.filter.regular;

import com.xianglin.act.common.dal.enums.lucky.wheel.RegularCustomPrize;
import com.xianglin.act.common.dal.mappers.CustomerAcquireRecordMapper;
import com.xianglin.act.common.dal.mappers.PrizeMapper;
import com.xianglin.act.common.dal.model.Prize;
import com.xianglin.core.model.Player;
import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;
import com.xianglin.core.model.enums.CustomerTypeEnum;
import com.xianglin.core.service.filter.Filter;
import com.xianglin.core.service.filter.FilterChain;

import javax.annotation.Resource;

import static com.xianglin.core.service.ActivityContext.getActivityCode;

/**
 * 老用户一天抽奖超过 50 次之后都给阳光普照奖
 */
public class TimeLimitFilter implements Filter {

    @Resource
    private CustomerAcquireRecordMapper customerAcquireRecordMapper;

    @Resource
    private PrizeMapper prizeMapper;

    @Override
    public void doFilter(ActivityRequest<?> request, ActivityResponse response, FilterChain filterChain) {
        final Player player = (Player) request.getRequest();
        if (player.getCustomerType() == CustomerTypeEnum.REGULAR_CUSTOMER) {
            long count = customerAcquireRecordMapper.selectRegularCustomerRecordCount(
                    player.getPartyId(),
                    getActivityCode()
            );
            if (count > 50) {
                Prize prize = prizeMapper.selectActivityPrize(getActivityCode(), RegularCustomPrize.EVERY_PRIZE.name());
                response.setPrize(prize);
            }
        }
        filterChain.doFilter(request, response);
    }
}
