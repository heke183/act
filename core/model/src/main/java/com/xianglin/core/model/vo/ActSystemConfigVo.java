package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("活动配置表")
public class ActSystemConfigVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "配置code")
    private String configCode;

    @ApiModelProperty(value = "描述")
    private String configDesc;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "更新人")
    private String updater;

    @ApiModelProperty("活动配置的值")
    private String configValue;

    @ApiModelProperty("删除状态")
    private String isDeleted;

    @ApiModelProperty("创建时间")
    private Date createDate;

    @ApiModelProperty("修改时间")
    private Date updateDate;

    @ApiModelProperty("备注")
    private String comments;

}