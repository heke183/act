package com.xianglin.act.common.service.facade.constant;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/10 14:49.
 */
public enum RecordStatusEnum implements EnumReadable {
    VALID("有效", 0),
    IN_VALID("失效", 1);

    private String desc;

    private int code;

    RecordStatusEnum() {

    }

    RecordStatusEnum(String desc, int code) {

        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {

        return desc;
    }

    public void setDesc(String desc) {

        this.desc = desc;
    }

    @Override
    public String getName() {

        return desc;
    }

    public int getCode() {

        return code;
    }

    public void setCode(int code) {

        this.code = code;
    }

    public static RecordStatusEnum vauleOfCode(int code) {

        for (RecordStatusEnum popTipTypeEnum : RecordStatusEnum.values()) {
            if (popTipTypeEnum.getCode() == code) {
                return popTipTypeEnum;
            }
        }
        return null;
    }
}
