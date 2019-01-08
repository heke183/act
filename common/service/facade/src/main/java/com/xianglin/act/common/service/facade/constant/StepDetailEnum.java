package com.xianglin.act.common.service.facade.constant;

/**
 * 步步生金
 */
public enum StepDetailEnum{
    

    FIRST("000000","090000"),
    SECOND("090000","170000"),
    THIRD("170000","200000"),
    FOURTH("200000","240000"),
    ALL(null,null);
    
    public static StepDetailEnum getType(String type) {
        return StepDetailEnum.valueOf(type);
    }

    /**
     * 起始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    StepDetailEnum(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
