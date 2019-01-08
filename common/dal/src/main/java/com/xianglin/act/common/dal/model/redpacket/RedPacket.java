package com.xianglin.act.common.dal.model.redpacket;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author yefei
 * @date 2018-03-30 16:17
 */
@ApiModel
public class RedPacket {

    @ApiModelProperty("红包序列,唯一标识")
    private String packetId;

    private long partyId;

    private String isComplete;

    private Date startDate;

    private Date expireDate;

    private Date completeDate;

    @ApiModelProperty("红包信息")
    private String packetInfo;

    private String isChecked;

    private String sharerImage;

    private String memcCode;

    public String getMemcCode() {
        return memcCode;
    }

    public void setMemcCode(String memcCode) {
        this.memcCode = memcCode;
    }

    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }

    public long getPartyId() {
        return partyId;
    }

    public void setPartyId(long partyId) {
        this.partyId = partyId;
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

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    public String getPacketInfo() {
        return packetInfo;
    }

    public void setPacketInfo(String packetInfo) {
        this.packetInfo = PacketInfoList.getInstance().get();
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }

    public String getSharerImage() {
        return sharerImage;
    }

    public void setSharerImage(String sharerImage) {
        this.sharerImage = sharerImage;
    }

    public String getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(String isComplete) {
        this.isComplete = isComplete;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RedPacket{");
        sb.append("packetId='").append(packetId).append('\'');
        sb.append(", partyId=").append(partyId);
        sb.append(", isComplete='").append(isComplete).append('\'');
        sb.append(", startDate=").append(startDate);
        sb.append(", expireDate=").append(expireDate);
        sb.append(", completeDate=").append(completeDate);
        sb.append(", packetInfo='").append(packetInfo).append('\'');
        sb.append(", isChecked='").append(isChecked).append('\'');
        sb.append(", sharerImage='").append(sharerImage).append('\'');
        sb.append(", memcCode='").append(memcCode).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
