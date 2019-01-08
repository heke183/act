package com.xianglin.act.common.dal.support.pop;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 21:18.
 */

public class PopTipDO {

    private Object dbRecords;

    private Integer popTipType;

    private Integer returnType;


    public PopTipDO() {

    }

    public PopTipDO(Object dbRecords, Integer popTipType, Integer returnType) {

        this.dbRecords = dbRecords;
        this.popTipType = popTipType;
        this.returnType = returnType;
    }

    public Object getDbRecords() {

        return dbRecords;
    }

    public void setDbRecords(Object dbRecords) {

        this.dbRecords = dbRecords;
    }

    public Integer getPopTipType() {

        return popTipType;
    }

    public void setPopTipType(Integer popTipType) {

        this.popTipType = popTipType;
    }

    public Integer getReturnType() {

        return returnType;
    }

    public void setReturnType(Integer returnType) {

        this.returnType = returnType;
    }
}
