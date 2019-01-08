package com.xianglin.core.model.vo;

import com.xianglin.core.model.enums.AttendanceH5StatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/17 20:24.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("活动详情")
public class AttendanceActivityDetailVO {

    /**
     * 参与者总数
     */
    @ApiModelProperty("当前用户partyId")
    private Long partyId;

    /**
     * 参与者总数
     */
    @ApiModelProperty("参与者总数")
    private String partakeCount;

    /**
     * 奖金池总额
     */
    @ApiModelProperty("奖金池总额")
    private String bonusAmout;

    /**
     * 打卡成功人数
     */
    @ApiModelProperty("打卡成功人数")
    private String successNum;

    /**
     * 打卡失败人数
     */
    @ApiModelProperty("打卡失败人数")
    private String failNum;

    /**
     * 开奖倒计时时间戳
     */
    @ApiModelProperty("开奖倒计时")
    private Long timestamps;

    /**
     * 打卡状态
     */
    @ApiModelProperty("打卡状态")
    private AttendanceH5StatusEnum attendanceStatus;

    private String message;

    private List<AttendanceStarVO> attendanceStars;

    /**
     * 分享标题
     */
    @ApiModelProperty("活动分享标题")
    private String shareTitle;

    /**
     * 分享跳转url
     */
    @ApiModelProperty("活动分享跳转url")
    private String shareUrl;

    /**
     * 分享跳转img
     */
    @ApiModelProperty("活动分享跳转img")
    private String shareImg;

    /**
     * 分享跳转描述
     */
    @ApiModelProperty("活动分享跳转描述")
    private String shareDesc;

    /**
     * 分享标题
     */
    @ApiModelProperty("战绩分享标题")
    private String shareTitlez;

    /**
     * 分享跳转url
     */
    @ApiModelProperty("战绩分享跳转url")
    private String shareUrlz;

    /**
     * 分享跳转img
     */
    @ApiModelProperty("战绩分享跳转img")
    private String shareImgz;

    /**
     * 分享跳转描述
     */
    @ApiModelProperty("战绩分享跳转描述")
    private String shareDescz;

    /**
     * 打卡时间段
     */
    @ApiModelProperty("打卡时间段")
    private String signInTimeZone;
}
