package com.xianglin.core.service.filter;

import com.xianglin.core.model.Player;
import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;
import com.xianglin.core.service.strategy.ActivityStrategy;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author yefei
 * @date 2018-01-25 11:13
 */
@Component("originalFilterChain")
public class OriginalFilterChain implements FilterChain, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void doFilter(ActivityRequest<?> request, ActivityResponse response) {
        Player player = (Player) request.getRequest();
        ActivityStrategy activityStrategy = (ActivityStrategy) applicationContext.getBean(player.getCustomerType().strategy);
        activityStrategy.handle((ActivityRequest<Player>) request, response);
    }
}
