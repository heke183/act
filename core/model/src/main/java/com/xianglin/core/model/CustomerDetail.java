package com.xianglin.core.model;

/**
 * @author yefei
 * @date 2018-01-22 15:23
 */
public class CustomerDetail {

    private Long partyId;

    private String mobilePhone;

    private Integer gold;

    private int availableCount;

    private long customerCount;

    public Long getPartyId() {
        return partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public int getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }

    public long getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(long customerCount) {
        this.customerCount = customerCount;
    }
}
