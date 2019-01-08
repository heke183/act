package com.xianglin.act.common.dal.model;

import java.util.Date;

/**
 * @author yefei
 * @date 2018-01-23 19:14
 */
public class PrizeConfig {

    private Long id;

    private String prizeCode;

    private int prizeLevel;

    private String customerType;

    private Date startDate;

    private Date expireDate;

    public int getPrizeLevel() {
        return prizeLevel;
    }

    public void setPrizeLevel(int prizeLevel) {
        this.prizeLevel = prizeLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrizeCode() {
        return prizeCode;
    }

    public void setPrizeCode(String prizeCode) {
        this.prizeCode = prizeCode;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }
}
