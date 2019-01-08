package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Describe :
 * Created by xingyali on 2018/8/6 17:06.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "任务明细记录表")
public class ActPlantTaskDetailVo {
    
    @ApiModelProperty("id")
    private Long id;

    /**
     * 用户
     */
    @ApiModelProperty("用户的partyID")
    private Long partyId;

    /**
     * 任务code
     */
    @ApiModelProperty("活动code")
    private String code;

    /**
     *  日期 YYYYMMDD
     */
    @ApiModelProperty("日期")
    private String day;

    /**
     * 类型
     */
    @ApiModelProperty("类型")
    private String type;

    /**
     * 关联数据id
     */
    @ApiModelProperty("关联数据id")
    private String refId;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("删除状态")
    private String isDeleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("备注")
    private String comments;
}
