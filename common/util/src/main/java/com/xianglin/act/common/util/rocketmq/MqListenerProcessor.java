package com.xianglin.act.common.util.rocketmq;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Lists;
import com.xianglin.act.common.util.annotation.MqListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Mq 注解监听处理
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/17 9:29.
 */
@Component
public class MqListenerProcessor implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MqListenerProcessor.class);

    public static final List<Class<?>> parameterTypes = Lists.newArrayList(ConsumeConcurrentlyContext.class, MessageExt.class, String.class);

    @Autowired
    private MqEventBus eventBus;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> beanClazz = bean.getClass();
        ArrayList<Method> listenerMethods = Lists.newArrayList();
        ReflectionUtils.doWithMethods(beanClazz, method -> listenerMethods.add(method), method -> {
            int modifiers = method.getModifiers();
            boolean isPrivate = Modifier.isPrivate(modifiers);
            boolean annotationPresent = method.isAnnotationPresent(MqListener.class);
            return !isPrivate && annotationPresent;
        });

        if (listenerMethods.isEmpty()) {
            return bean;
        }
        MethodAccess methodAccess = MethodAccess.get(beanClazz);
        listenerMethods.stream()
                .filter(method -> {
                    if (method.getParameterCount() > 3) {
                        logger.warn("===========方法有多于3个参数：[[ {} ]][[ {} ]]===========", beanClazz.getName(), method.getName());
                        return false;
                    }
                    return true;
                })
                .filter(method -> {
                    boolean flag = Arrays.stream(method.getParameterTypes()).allMatch(aClass -> parameterTypes.contains(aClass));
                    if (!flag) {
                        logger.warn("===========方法参数不合法：[[ {} ]][[ {} ]]===========", beanClazz.getName(), method.getName());
                    }
                    return flag;
                })

                .forEach(method -> {
                    MqListener mqListener = method.getAnnotation(MqListener.class);

                    MqEventBus.MqListenerContext mqListenerContext = new MqEventBus.MqListenerContext();
                    mqListenerContext.setTopic(mqListener.topic());
                    mqListenerContext.setTag(mqListener.tag());
                    int index = methodAccess.getIndex(method.getName());
                    mqListenerContext.setMethodIndex(index);
                    mqListenerContext.setParameterTypes(method.getParameterTypes());
                    mqListenerContext.setMethod(method);
                    mqListenerContext.setMethodAccess(methodAccess);
                    mqListenerContext.setTarget(bean);
                    eventBus.addMqListener(mqListenerContext);
                });
        return bean;
    }
}
