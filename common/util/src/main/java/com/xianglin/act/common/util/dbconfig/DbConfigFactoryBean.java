package com.xianglin.act.common.util.dbconfig;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/19 16:12.
 */
public class DbConfigFactoryBean<T> implements FactoryBean<T>, InitializingBean {

    private Class<?> beanClass;

    private String beanClassName;


    @Autowired
    DbConfigBeanInvokeHandler invokeHandler;

    @Override
    public T getObject() throws Exception {

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(beanClass);
        enhancer.setCallback(invokeHandler);
        return (T) enhancer.create();
    }

    @Override
    public Class<?> getObjectType() {
        return beanClass;
    }

    @Override
    public boolean isSingleton() {

        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(beanClassName));
        beanClass = Class.forName(beanClassName);
    }

    public Class<?> getBeanClass() {

        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {

        this.beanClass = beanClass;
    }

    public String getBeanClassName() {

        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {

        this.beanClassName = beanClassName;
    }

    public DbConfigBeanInvokeHandler getInvokeHandler() {

        return invokeHandler;
    }

    public void setInvokeHandler(DbConfigBeanInvokeHandler invokeHandler) {

        this.invokeHandler = invokeHandler;
    }
}
