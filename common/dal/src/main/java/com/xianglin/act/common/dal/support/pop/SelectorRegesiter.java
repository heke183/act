package com.xianglin.act.common.dal.support.pop;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Lists;
import com.xianglin.act.common.dal.annotation.PopTipSelector;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yungyu
 */
@Component
public class SelectorRegesiter implements ApplicationListener<ContextRefreshedEvent> {

    //暴露标志
    public static volatile boolean exportFlag = false;

    private static final Logger logger = LoggerFactory.getLogger(SelectorRegesiter.class);

    // 单线程初始化,但是可能会在初始化还没完成时提前暴露出去
    private List<SelectorInvokable> pipTipSelectors = Lists.newArrayList();

    public void addTipSelector(Class<?> mapperInterface, Object mapperObject) {

        logger.info("===========缓存sellector接口，接口名：[[ {} ]]===========", mapperInterface.getName());
        MethodAccess methodAccess = MethodAccess.get(mapperObject.getClass());
        Method[] methods = mapperInterface.getDeclaredMethods();
        List<SelectorInvokable> invokableList = Lists.newArrayList(methods)
                .stream()
                .filter(input -> input.isAnnotationPresent(PopTipSelector.class))
                .map((input) -> {
                    PopTipSelector popTipSelector = input.getAnnotation(PopTipSelector.class);
                    int popTipType = popTipSelector.popTipType();
                    int returnType = popTipSelector.returnType();
                    if (returnType == 0) {
                        throw new IllegalArgumentException("returnType 不能为0");
                    }
                    String methodName = input.getName();
                    int index = methodAccess.getIndex(methodName);
                    SelectorInvokable selectorInvokable = new SelectorInvokable();
                    selectorInvokable.setMethodAccess(methodAccess);
                    selectorInvokable.setMethondIndex(index);
                    selectorInvokable.setTarget(mapperObject);
                    selectorInvokable.setTargetMethod(input);
                    selectorInvokable.setPopTipType(popTipType);
                    checkReturnType(returnType);
                    selectorInvokable.setReturnType(returnType);
                    //方法参数名
                    String[] paramNames = new String[input.getParameterCount()];
                    Annotation[][] parameterAnnotations = input.getParameterAnnotations();
                    if (parameterAnnotations == null) {
                        parameterAnnotations = new Annotation[0][0];
                    }
                    for (int i = 0; i < parameterAnnotations.length; i++) {
                        if (parameterAnnotations[i] == null) {
                            parameterAnnotations[i] = new Annotation[0];
                        }
                        String paramName = null;
                        for (Annotation annotation : parameterAnnotations[i]) {
                            if (annotation instanceof Param) {
                                paramName = ((Param) annotation).value();
                            }
                        }
                        paramNames[i] = paramName;
                    }
                    selectorInvokable.setParamNames(paramNames);
                    logger.info("===========缓存sellector语句 接口名：{}，方法名：{}成功===========", mapperInterface.getName(), methodName);
                    return selectorInvokable;
                }).collect(Collectors.toList());
        pipTipSelectors.addAll(invokableList);
    }
    // 该校验无效
    private void checkReturnType(int returnType) {

        if (pipTipSelectors
                .stream()
                .filter(input -> input.getReturnType() == returnType)
                .count() > 0) {
            throw new IllegalArgumentException("returnType 不唯一");
        }
    }

    public List<SelectorInvokable> getPipTipSelectors() {
        //不提前暴露不完整的服务
        if (!exportFlag) {
            throw new IllegalStateException("应用正在初始化，请稍后重试");
        }
        return pipTipSelectors;
    }

    public void setPipTipSelectors(List<SelectorInvokable> pipTipSelectors) {

        this.pipTipSelectors = pipTipSelectors;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //容器初始化完成，这说明pipTipSelectors 已经收集完成，可以暴露
        exportFlag = true;
    }
}
