package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Describe :
 * Created by xingyali on 2018/12/19 14:17.
 * Update reason :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("团员信息")
public class ActGroupInfoDetailVo {
    
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "团id")
    private Long infoId;

    @ApiModelProperty(value = "参与用户")
    private Long partyId;

    @ApiModelProperty(value = "分得金额")
    private BigDecimal balance;

    @ApiModelProperty(value = "成员头像")
    private String headImg;

    @ApiModelProperty(value = "用户类型 Manager团长 user成员")
    private String type;

    @ApiModelProperty(value = "状态 进行中：I,红包发放成功:S，失效：F")
    private String status;

    @ApiModelProperty(value = "删除状态")
    private String isDeleted;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "备注")
    private String comments;
}
