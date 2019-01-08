package com.xianglin.act.common.dal.support.pop;

import com.xianglin.act.common.dal.annotation.PopTipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.spring.mapper.MapperFactoryBean;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 11:06.
 */

public class SelectorMapperFactoryBean<T> extends MapperFactoryBean<T> {

    @Autowired
    SelectorRegesiter selectorRegesiter;

    public SelectorMapperFactoryBean(Class<T> mapperInterface) {

        super(mapperInterface);
    }

    public SelectorMapperFactoryBean() {

    }

    @Override
    public T getObject() throws Exception {

        Object object = super.getObject();
        Class mapperClazz = getObjectType();
        if (mapperClazz.isAnnotationPresent(PopTipMapper.class)) {
            selectorRegesiter.addTipSelector(getObjectType(),object);
        }
        return (T) object;
    }
}
