package com.xianglin.core.service.strategy;

import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;
import com.xianglin.core.service.filter.Filter;
import com.xianglin.core.service.filter.FilterChain;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yefei
 * @date 2018-01-18 16:59
 */
@Component
public class CustomerContext {

    @Resource(name = "compositeFilter")
    private Filter compositeFilter;

    @Resource(name = "originalFilterChain")
    private FilterChain filterChain;

    public void handle(ActivityRequest<?> request, ActivityResponse response) {
        compositeFilter.doFilter(request, response, filterChain);
    }
}
