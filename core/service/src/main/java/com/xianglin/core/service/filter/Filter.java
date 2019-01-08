package com.xianglin.core.service.filter;

import com.xianglin.act.common.dal.model.Prize;
import com.xianglin.core.model.Player;
import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;

/**
 * The interface Security filter.
 *
 * @author yefei
 * @date 2018 -01-25 9:54
 */
public interface Filter {

    /**
     * Do filter boolean.
     *
     * @param request     the request
     * @param response    the response
     * @param filterChain the filter chain
     */
    void doFilter(ActivityRequest<?> request, ActivityResponse response, FilterChain filterChain);
}
