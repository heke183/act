package com.xianglin.core.model.enums;

/**
 * @author yefei
 * @date 2018-01-18 17:13
 */
public enum CustomerTypeEnum {

    /**
     * 新用户
     */
    NEW_CUSTOMER("新用户", "newActivityStrategy"),

    /**
     * 老用户
     */
    REGULAR_CUSTOMER("老用户", "regularActivityStrategy");

    public String desc;

    public String strategy;

    CustomerTypeEnum(String desc, String strategy) {
        this.desc = desc;
        this.strategy = strategy;
    }
}
