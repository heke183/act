package com.xianglin.act.common.dal.support.pop;

import com.xianglin.act.common.dal.annotation.PopTipMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

/**
 * 弹窗来源
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/20 10:23.
 */

public class GenericPopWindowSelectorProcessor implements BeanPostProcessor, Ordered {

    @Autowired
    private SelectorRegesiter selectorRegesiter;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> beanClazz = bean.getClass();
        PopTipMapper mapper = beanClazz.getAnnotation(PopTipMapper.class);
        if (mapper != null) {
            selectorRegesiter.addTipSelector(beanClazz, bean);
        }
        return bean;
    }

    @Override
    public int getOrder() {

        return Integer.MAX_VALUE;
    }
}
