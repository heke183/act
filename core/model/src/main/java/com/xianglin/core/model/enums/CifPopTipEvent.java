package com.xianglin.core.model.enums;

/**
 * CIF 弹窗事件枚举
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/21 15:03.
 */

public enum CifPopTipEvent implements PopTipEvent {

    CIF_REGISTER("注册", "REGISTER"),
    CIF_UPDATE_MOBILE("修改手机号码", "UPDATE_MOBILE"),
    CIF_BINDING_ROLE("绑定角色", "BINDING_ROLE"),
    CIF_UNBINDING_ROLE("解绑角色", "UNBINDING_ROLE"),
    CIF_BINDING_BUSINESS("开通业务", "BINDING_BUSINESS"),
    CIF_UNBINDING_BUSINESS("解绑业务", "UNBINDING_BUSINESS");

    CifPopTipEvent(String desc, String topic, String tag) {

        this.desc = desc;
        this.topic = topic;
        this.tag = tag;
    }

    CifPopTipEvent(String desc, String tag) {

        this.desc = desc;
        this.tag = tag;
    }

    CifPopTipEvent() {

    }

    private String desc;

    private String topic = "CIF_TOPIC";

    private String tag;

    @Override
    public String getDesc() {

        return this.desc;
    }

    @Override
    public String getTopic() {

        return this.topic;
    }

    @Override
    public String getTag() {

        return this.tag;
    }

    @Override
    public String getKey() {

        return this.topic + "_" + this.tag;
    }
}
