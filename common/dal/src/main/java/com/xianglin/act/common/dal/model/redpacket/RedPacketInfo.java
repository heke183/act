package com.xianglin.act.common.dal.model.redpacket;

import com.alibaba.fastjson.annotation.JSONField;
import com.xianglin.act.common.dal.model.CustomerAcquire;
import com.xianglin.act.common.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author yefei
 * @date 2018-04-02 17:21
 */
@ApiModel
public class RedPacketInfo extends RedPacket {

    @ApiModelProperty("红包剩余秒")
    private long timeLeft;

    @ApiModelProperty("剩余人数")
    private int needUserCount;

    /**
     * 滚动数据
     */
    private Set<CustomerAcquire> customerAcquires;

    private List<PartakerInfo> partakers;

    /**
     * 是否超过24小时
     */
    @JSONField(name = "isOutOfHour")
    private boolean outOfHour;

    private boolean complete;

    private Sharer sharer;

    @JSONField(name = "isToday")
    private boolean isToday;

    public boolean isToday() {
        if (getStartDate() == null) {
            return true;
        } else {
            final Date startDate = getStartDate();
            Calendar instance = Calendar.getInstance();
            instance.setTime(startDate);
            instance.clear(Calendar.MILLISECOND);
            instance.set(Calendar.HOUR_OF_DAY, 23);
            instance.set(Calendar.MINUTE, 59);
            instance.set(Calendar.SECOND, 59);

            return isToday = DateUtils.getNow().getTime() < instance.getTime().getTime();
        }
    }

    /**
     * 红包还剩多少人
     *
     * @return
     */
    public int getNeedUserCount() {
        if (partakers != null) {
            return needUserCount = 2 - partakers.size();
        }
        return needUserCount = 2;
    }

    /**
     * 红包剩余秒数
     *
     * @return
     */
    public long getTimeLeft() {
        if (getExpireDate() != null && getStartDate() != null) {
            return timeLeft = getExpireDate().getTime() - DateUtils.getNow().getTime();
        }
        return timeLeft;
    }

    public List<PartakerInfo> getPartakers() {
        return partakers;
    }

    public void setPartakers(List<PartakerInfo> partakers) {
        this.partakers = partakers;
    }

    public Set<CustomerAcquire> getCustomerAcquires() {
        return customerAcquires;
    }

    public void setCustomerAcquires(Set<CustomerAcquire> customerAcquires) {
        this.customerAcquires = customerAcquires;
    }

    public Sharer getSharer() {
        return sharer;
    }

    public void setSharer(Sharer sharer) {
        this.sharer = sharer;
    }

    public boolean isOutOfHour() {
        if (getStartDate() == null) {
            return outOfHour = false;
        }
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR_OF_DAY, -24);
        if (instance.getTime().after(getStartDate())) {
            return outOfHour = true;
        }
        return outOfHour = false;
    }

    public boolean isComplete() {
        if ("Y".equals(getIsComplete())) {
            return complete = true;
        }
        return complete = false;
    }

}
