package com.xianglin.act.common.dal.model;

import java.math.BigDecimal;

/**
 * @author yefei
 * @date 2018-01-24 9:34
 */
public class PrizeProbabilityConfig {

    private String customerType;

    private BigDecimal initialProbability;

    private BigDecimal addProbability;

    private BigDecimal increment;

    public BigDecimal getIncrement() {
        return increment;
    }

    public void setIncrement(BigDecimal increment) {
        this.increment = increment;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public BigDecimal getInitialProbability() {
        return initialProbability;
    }

    public void setInitialProbability(BigDecimal initialProbability) {
        this.initialProbability = initialProbability;
    }

    public BigDecimal getAddProbability() {
        return addProbability;
    }

    public void setAddProbability(BigDecimal addProbability) {
        this.addProbability = addProbability;
    }
}
