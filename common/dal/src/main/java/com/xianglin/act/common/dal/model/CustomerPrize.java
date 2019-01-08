package com.xianglin.act.common.dal.model;

import java.math.BigDecimal;

/**
 * @author yefei
 * @date 2018-01-18 16:40
 */
public class CustomerPrize {

    private String customerType;

    private String customerPrize;

    private String prizeCode;

    private int prizeLevel;
    
    private int amount;

    private int availableAmount;

    private BigDecimal probability;

    private BigDecimal initialProbability;

    private BigDecimal minValue;

    private BigDecimal maxValue;

    private int remainValue;

    private BigDecimal unitRmb;

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

    public int getPrizeLevel() {
        return prizeLevel;
    }

    public void setPrizeLevel(int prizeLevel) {
        this.prizeLevel = prizeLevel;
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public int getRemainValue() {
        return remainValue;
    }

    public void setRemainValue(int remainValue) {
        this.remainValue = remainValue;
    }

    public BigDecimal getUnitRmb() {
        return unitRmb;
    }

    public void setUnitRmb(BigDecimal unitRmb) {
        this.unitRmb = unitRmb;
    }

    public BigDecimal getInitialProbability() {
        return initialProbability;
    }

    public void setInitialProbability(BigDecimal initialProbability) {
        this.initialProbability = initialProbability;
    }
}
