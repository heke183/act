package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Describe :
 * Created by xingyali on 2018/8/3 10:16.
 * Update reason :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("植树活动爱心值表")
public class ActPlantVo {
    
    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("partyId")
    private Long partyId;

    @ApiModelProperty("用户显示名称")
    private String showName;


    @ApiModelProperty(value = "当前爱心值",required = true)
    private Integer lv;

    @ApiModelProperty(value = "总爱心值",required = true)
    private Integer totalLv;

    @ApiModelProperty("海报地址")
    private String poster;

    @ApiModelProperty("个人二维码地址")
    private String qr;

    @ApiModelProperty("微信openId")
    private String openId;

    @ApiModelProperty("删除状态")
    private String isDeleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("备注")
    private String comments;
}
