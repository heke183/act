package com.xianglin.act.common.util.dbconfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/19 16:05.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbConfigBean {

    String activityCode() default "";
}
