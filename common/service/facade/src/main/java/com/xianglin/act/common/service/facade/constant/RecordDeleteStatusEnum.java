package com.xianglin.act.common.service.facade.constant;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/10 11:03.
 */
public enum RecordDeleteStatusEnum implements EnumReadable {
    YES("是", 1),
    NO("否", 0);

    private String desc;

    private int code;

    RecordDeleteStatusEnum() {

    }

    RecordDeleteStatusEnum(String desc, int code) {

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

    public static RecordDeleteStatusEnum vauleOfCode(int code) {

        for (RecordDeleteStatusEnum popTipTypeEnum : RecordDeleteStatusEnum.values()) {
            if (popTipTypeEnum.getCode() == code) {
                return popTipTypeEnum;
            }
        }
        return null;
    }
}
