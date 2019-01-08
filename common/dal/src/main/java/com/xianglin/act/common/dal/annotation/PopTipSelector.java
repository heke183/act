package com.xianglin.act.common.dal.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 10:15.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PopTipSelector {


    /**
     * 弹窗类型
     *
     * @return
     */
    int popTipType() default Integer.MAX_VALUE;

    /**
     * 返回值类型
     *
     * @return
     */
    int returnType() default 0;
}
