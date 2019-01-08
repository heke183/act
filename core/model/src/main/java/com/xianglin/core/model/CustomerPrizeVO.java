package com.xianglin.core.model;


import com.xianglin.act.common.dal.enums.PrizeEnum;

import java.math.BigDecimal;

/**
 * @author yefei
 * @date 2018-01-18 16:40
 */
public class CustomerPrizeVO {

    private String customerType;

    private String customerPrize;

    private String prizeCode;

    private int amount;

    private int availableAmount;

    private BigDecimal probability;

    private PrizeEnum prizeEnum;

    public CustomerPrizeVO() {
    }

    public CustomerPrizeVO(PrizeEnum prizeEnum) {
        this.prizeEnum = prizeEnum;
    }

    public PrizeEnum getPrizeEnum() {
        return prizeEnum;
    }

    public void setPrizeEnum(PrizeEnum prizeEnum) {
        this.prizeEnum = prizeEnum;
    }

    public String getCustomerPrize() {
        return customerPrize;
    }

    public void setCustomerPrize(String customerPrize) {
        this.customerPrize = customerPrize;
    }

    public String getPrizeCode() {
        return prizeCode;
    }

    public void setPrizeCode(String prizeCode) {
        this.prizeCode = prizeCode;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(int availableAmount) {
        this.availableAmount = availableAmount;
    }

    public BigDecimal getProbability() {
        return probability;
    }

    public void setProbability(BigDecimal probability) {
        this.probability = probability;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
}
