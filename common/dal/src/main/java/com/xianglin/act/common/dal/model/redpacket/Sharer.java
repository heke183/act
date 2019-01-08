package com.xianglin.act.common.dal.model.redpacket;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author yefei
 * @date 2018-03-30 10:51
 */
@ApiModel
public class Sharer extends User {

    private String wxId;

    @ApiModelProperty("微信头像")
    private String wxHeadImg;

    @ApiModelProperty("微信昵称")
    private String wxNickName;

    @ApiModelProperty("红包序列,唯一标识")
    private String packetId;

    @ApiModelProperty("分享者设备Id")
    private String deviceId;

    private String sharerQrCode;

    public String getSharerQrCode() {
        return sharerQrCode;
    }

    public void setSharerQrCode(String sharerQrCode) {
        this.sharerQrCode = sharerQrCode;
    }

    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }

    public String getWxId() {
        return wxId;
    }

    public void setWxId(String wxId) {
        this.wxId = wxId;
    }

    public String getWxHeadImg() {
        return wxHeadImg;
    }

    public void setWxHeadImg(String wxHeadImg) {
        this.wxHeadImg = wxHeadImg;
    }

    public String getWxNickName() {
        return wxNickName;
    }

    public void setWxNickName(String wxNickName) {
        this.wxNickName = wxNickName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public boolean isSharer() {
        return sharer = true;
    }
}
