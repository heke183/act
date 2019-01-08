package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * Describe :
 * Created by xingyali on 2018/8/14 13:59.
 * Update reason :
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("用户注册")
public class PlantRegisterVo {

    @ApiModelProperty(value = "手机号", required = true)
    private String mobilePhone;

    @ApiModelProperty(value = "验证码", required = true)
    private String code;
    
    @ApiModelProperty(value = "邀请人的partyID", required = true)
    private Long fromPartyId;

    @ApiModelProperty(value = "微信openId", required = true)
    private String openId;
}
