package com.xianglin.core.model;

import com.xianglin.act.common.dal.model.Party;
import com.xianglin.core.model.enums.CustomerTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yefei
 * @date 2018-01-18 14:06
 */
@ApiModel(value = "玩家")
@Data
public class Player extends Party {

    @ApiModelProperty(value = "推介人party",required = false)
    private Long fromPartyId;

    @ApiModelProperty(value = "手机号", required = true)
    private String mobilePhone;

    private CustomerTypeEnum customerType;

    private String signature;

    private String securityKey;

    private boolean isEmployee;

    private boolean woman;
}
