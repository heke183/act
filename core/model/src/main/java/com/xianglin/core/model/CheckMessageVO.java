package com.xianglin.core.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * The type Check message vo.
 *
 * @author yefei
 * @date 2018 -01-22 18:48
 */
@ApiModel
public class CheckMessageVO {

    private Long partyId;

    private Long fromPartyId;

    @ApiModelProperty(value = "手机号", required = true)
    private String mobilePhone;

    @ApiModelProperty(value = "验证码", required = true)
    private String code;

    @ApiModelProperty(value = "微信openId")
    private String openId;

    @ApiModelProperty(value = "红包id")
    private String packetId;

    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getFromPartyId() {
        return fromPartyId;
    }

    public void setFromPartyId(Long fromPartyId) {
        this.fromPartyId = fromPartyId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
