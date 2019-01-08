package com.xianglin.core.service.filter;

import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;

/**
 * The interface Filter chain.
 *
 * @author yefei
 * @date 2018 -01-25 10:40
 */
public interface FilterChain {

    /**
     * Do filter boolean.
     *
     * @param request  the request
     * @param response the response
     */
    void doFilter(ActivityRequest<?> request, ActivityResponse response);
}
