package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author jiang yong tao
 * @date 2018/8/7  15:00
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("爱心值交易明细")
public class ActPlantLvTranVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户")
    private Long partyId;

    @ApiModelProperty(value = "关联爱心id")
    private Long lvId;

    @ApiModelProperty(value = "爱心值",required = true)
    private Integer lv;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "奖品id")
    private String prizeCode;

    @ApiModelProperty(value = "奖品名")
    private String name;

    @ApiModelProperty(value = "收货用户名",required = true)
    private String userName;

    @ApiModelProperty(value = "收货手机号",required = true)
    private String mobile;

    @ApiModelProperty(value = "收货地址",required = true)
    private String address;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "客服备注")
    private String remark;

    @ApiModelProperty("是否删除")
    private String isDeleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("备注")
    private String comments;
}
