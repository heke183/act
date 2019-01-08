package com.xianglin.core.model.enums;

/**
 * 弹窗绑定的mq topic和tag
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/21 14:59.
 */
public interface PopTipEvent {

    String getDesc();

    String getTopic();

    String getTag();

    String getKey();

}
