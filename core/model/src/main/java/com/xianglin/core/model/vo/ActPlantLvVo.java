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
 * @date 2018/8/7  10:22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("植树活动爱心值表")
public class ActPlantLvVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long partyId;

    @ApiModelProperty(value = "爱心值",required = true)
    private Integer lv;

    @ApiModelProperty(value = "爱心值类型")
    private String type;

    @ApiModelProperty(value = "爱心值状态",required = true)
    private String status;

    @ApiModelProperty(value = "开始显示时间")
    private Date shouTime;

    @ApiModelProperty(value = "可领取时间")
    private Date matureTime;

    @ApiModelProperty(value = "过期时间")
    private Date expireTime;

    @ApiModelProperty(value = "关联任务code")
    private Long taskId;

    @ApiModelProperty("是否删除")
    private String isDeleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("备注")
    private String comments;

    @ApiModelProperty("还剩下多少时间可收取（以秒为单位）")
    private Long rencentTime;

    @ApiModelProperty("当前用户是否可偷取")
    private Boolean canCollect;

}
