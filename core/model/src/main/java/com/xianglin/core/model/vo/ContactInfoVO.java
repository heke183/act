package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @author yefei
 * @date 2018-06-05 14:06
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("联系信息")
public class ContactInfoVO {

    private long partyId;

    @ApiModelProperty(name = "手机号",required = true)
    private String mobilePhone;

    @ApiModelProperty(name = "姓名",required = true)
    private String name;

    @ApiModelProperty(name = "地址信息",required = true)
    private String address;

    private String comments;

    private String activityCode;

    @ApiModelProperty(name = "礼品code",required = true)
    private String prizeCode;

}
