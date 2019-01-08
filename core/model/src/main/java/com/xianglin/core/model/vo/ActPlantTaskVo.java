package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Table;
import java.util.Date;

/**
 * Describe :
 * Created by xingyali on 2018/8/3 10:40.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "用户的任务表")
public class ActPlantTaskVo {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("任务code")
    private String code;

    @ApiModelProperty("任务名")
    private String name;

    @ApiModelProperty("任务图片")
    private String image;

    @ApiModelProperty("描述")
    private String detail;

    @ApiModelProperty("每日上限")
    private Integer dayLimit;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("删除状态")
    private String isDeleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("备注")
    private String comments;

    @ApiModelProperty("可领克数")
    private int gramNumber;

    @ApiModelProperty("任务状态 S：明天再来 I去完成")
    private String status;

    @ApiModelProperty("完成数")
    private int completeCount;

    @ApiModelProperty("倒计时")
    private Long countDown;

    @ApiModelProperty("跳转地址")
    private String url;

}
