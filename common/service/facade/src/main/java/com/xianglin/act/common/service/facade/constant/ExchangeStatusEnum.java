package com.xianglin.act.common.service.facade.constant;

/**
 * Describe :
 * Created by xingyali on 2018/7/20 9:48.
 * Update reason :
 */
public enum ExchangeStatusEnum {
    I("未兑换"),
    F("兑换失败"),
    S("成功");
    public String desc;

    ExchangeStatusEnum(String desc) {
        this.desc = desc;
    }
}
