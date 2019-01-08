package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 入口主页信息
 *@author wanglei
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("种树活动主页")
public class ActPlantPortal {

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("头像")
    private String headImage;

    @ApiModelProperty("当前爱心值")
    private Integer lv;

    @ApiModelProperty("海报地址")
    private String poster;

    @ApiModelProperty("显示的能量值")
    private List<ActPlantLvVo> lvs;

}
