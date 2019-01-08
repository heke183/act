package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/18 10:29.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("我的战绩")
public class UserAttendanceDetailVO {

    /**
     * 成功打卡天数
     */
    @ApiModelProperty("累计成功打卡天数")
    private String successDays;

    /**
     * 累计获取金币
     */
    @ApiModelProperty("累计金币奖励总数")
    private String successAmout;

    /**
     * 最近打卡记录
     */
    @ApiModelProperty("最近打卡记录")
    List<AttendanceRecordVO> recordList;

    /**
     * 分享标题
     */
    @ApiModelProperty("分享标题")
    private String shareTitle;

    /**
     * 分享跳转url
     */
    @ApiModelProperty("分享跳转url")
    private String shareUrl;

    /**
     * 分享跳转img
     */
    @ApiModelProperty("分享跳转img")
    private String shareImg;

    /**
     * 分享跳转描述
     */
    @ApiModelProperty("分享跳转描述")
    private String shareDesc;
}
