package com.xianglin.core.model.enums;

/**
 * 之后都使用 Thread local 处理
 *
 * @author yefei
 * @date 2018 -01-22 10:44
 */
@Deprecated
public enum ActivityEnum {

    /**
     * Lucky wheel activity enum.
     */
    LUCKY_WHEEL("幸运大转盘"),
    RED_PACKET("现金红包"),
    RED_PACKET_V2("感恩母亲节红包"),
    ATTENDANCE_AWARD("打卡赢金币"),
    ACT_STEP("步步生金"),
    GAME_PLANE("飞机大战"),
    ACT_PLANT("种树活动");

    ActivityEnum(String remark) {

        this.remark = remark;
    }

    private String remark;

    public String getRemark() {

        return remark;
    }

    public void setRemark(String remark) {

        this.remark = remark;
    }
}
