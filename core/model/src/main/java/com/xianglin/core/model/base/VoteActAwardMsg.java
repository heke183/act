package com.xianglin.core.model.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记不同活动的不同提示消息
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/4 17:24.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VoteActAwardMsg {

    String activityCode();
}
