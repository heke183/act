package com.xianglin.act.common.dal.model.redpacket;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yefei
 * @date 2018-03-30 10:51
 */
@ApiModel
public class Partaker extends User {

    private long sharerPartyId;

    private String packetId;

    private String excludePacketId;

    @ApiModelProperty("头像")
    private String headImg;

    private boolean isRemind;

    private Date openDate;

    private String memcCode;

    private String deviceId;

    private String prizeCode;

    private BigDecimal prizeValue;

    private String userType;

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getMemcCode() {
        return memcCode;
    }

    public void setMemcCode(String memcCode) {
        this.memcCode = memcCode;
    }

    public String getPrizeCode() {
        return prizeCode;
    }

    public void setPrizeCode(String prizeCode) {
        this.prizeCode = prizeCode;
    }

    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }

    public boolean isRemind() {
        return isRemind;
    }

    public void setRemind(boolean remind) {
        isRemind = remind;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getExcludePacketId() {
        return excludePacketId;
    }

    public void setExcludePacketId(String excludePacketId) {
        this.excludePacketId = excludePacketId;
    }

    public BigDecimal getPrizeValue() {
        return prizeValue;
    }

    public void setPrizeValue(BigDecimal prizeValue) {
        this.prizeValue = prizeValue;
    }

    public long getSharerPartyId() {
        return sharerPartyId;
    }

    public void setSharerPartyId(long sharerPartyId) {
        this.sharerPartyId = sharerPartyId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public boolean isSharer() {
        return sharer = false;
    }
}
