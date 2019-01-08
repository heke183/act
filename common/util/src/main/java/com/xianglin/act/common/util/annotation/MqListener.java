package com.xianglin.act.common.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * mq监听方法注解
 * 最多可以有三个参数 MessageExt msgs, ConsumeConcurrentlyContext context, String body
 * 顺序不用固定
 * body字符串为消息内容字符串
 * 此注解只支持并发消费消息，顺序消费消息需要自行获取消费者注册话题和tag
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/16 15:41.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MqListener {

    /**
     * 主题
     *
     * @return
     */
    String topic();

    /**
     * 标签
     *
     * @return
     */
    String tag();

}
