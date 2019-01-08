package com.xianglin.act.common.service.facade.constant;

/**
 * @author Yungyu
 */
public enum PopTipFrequencyEnum implements EnumReadable {
    ONECE("每天", 0),
    EVERY_DAY("一次", 1);

    private String desc;

    private int code;

    PopTipFrequencyEnum() {

    }

    PopTipFrequencyEnum(String desc, int code) {

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


    public static PopTipFrequencyEnum vauleOfCode(int code) {

        for (PopTipFrequencyEnum popTipTypeEnum : PopTipFrequencyEnum.values()) {
            if (popTipTypeEnum.getCode() == code) {
                return popTipTypeEnum;
            }
        }
        return null;
    }
}
