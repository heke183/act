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
 * @date 2018/8/3  10:50
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("奖品")
public class ActPlantPrizeVo {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("奖品code")
    private String code;

    @ApiModelProperty("奖品名")
    private String name;

    @ApiModelProperty("奖品图片")
    private String image;

    @ApiModelProperty("奖品爱心值")
    private Integer lv;

    @ApiModelProperty("类型，区分兑换奖品和奖励")
    private String type;

    @ApiModelProperty("类型，区分金币")
    private String rewardType;

    @ApiModelProperty("金币数")
    private Integer gold;

    @ApiModelProperty("是否删除")
    private String isDeleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("备注")
    private String comments;
}
