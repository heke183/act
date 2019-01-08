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
 * Created by xingyali on 2018/8/3 10:35.
 * Update reason :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("消息")
public class ActPlantTipVo {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("用户partyId")
    private Long partyId;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("提示信息")
    private String tip;

    @ApiModelProperty("展示时间")
    private String dateTime;

    @ApiModelProperty("删除状态")
    private String isDeleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("备注")
    private String comments;
}
