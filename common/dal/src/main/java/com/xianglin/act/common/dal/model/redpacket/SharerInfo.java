package com.xianglin.act.common.dal.model.redpacket;

import com.alibaba.fastjson.annotation.JSONField;
import com.xianglin.act.common.dal.model.CustomerAcquire;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yefei
 * @date 2018-03-30 11:30
 */
public class SharerInfo extends Sharer {

    private List mockFootList;

    private List mockTitleList;

    private RedPacket redPacket;

    private boolean checked;

    private CustomerAcquire customerAcquire;

    /**
     * 不满足条件是否超过两次
     */
    @JSONField(name = "isOutOf")
    private boolean outOf;

    public RedPacket getRedPacket() {
        return redPacket;
    }

    public void setRedPacket(RedPacket redPacket) {
        this.redPacket = redPacket;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public CustomerAcquire getCustomerAcquire() {
        return customerAcquire;
    }

    public void setCustomerAcquire(CustomerAcquire customerAcquire) {
        this.customerAcquire = customerAcquire;
    }

    public List getMockFootList() {
        return mockFootList;
    }

    public void setMockFootList(List mockFootList) {
        this.mockFootList = new ArrayList<>(mockFootList);
    }

    public List getMockTitleList() {
        return mockTitleList;
    }

    public void setMockTitleList(List mockTitleList) {
        this.mockTitleList = new ArrayList<>(mockTitleList);
    }

    public boolean isOutOf() {
        return outOf;
    }

    public void setOutOf(boolean outOf) {
        this.outOf = outOf;
    }
}
