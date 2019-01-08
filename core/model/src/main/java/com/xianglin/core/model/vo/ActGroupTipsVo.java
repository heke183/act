package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Describe :
 * Created by xingyali on 2018/12/19 15:26.
 * Update reason :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("提示消息")
public class ActGroupTipsVo {
    
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户partyId")
    private Long partyId;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "信息")
    private String tips;

    @ApiModelProperty(value = "变动金额")
    private String changeValue;

    @ApiModelProperty(value = "是否是收入")
    private Boolean isIncome;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;
}
