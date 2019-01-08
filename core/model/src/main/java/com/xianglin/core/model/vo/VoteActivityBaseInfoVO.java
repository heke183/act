package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.zookeeper.server.quorum.Vote;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/1 10:53.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("投票活动基本信息")
public class VoteActivityBaseInfoVO {

    /**
     * itemId
     */
    @ApiModelProperty("itemId")
    private Long id;

    /**
     * 页面标题
     */
    @ApiModelProperty("页面标题")
    private String title;

    /**
     * 轮播图
     */
    @ApiModelProperty("活动图片")
    private String carouselImgs;

    /**
     * 活动是否已经结束
     */
    @ApiModelProperty("活动是否已经结束")
    private boolean hasExpire;

    /**
     * 当前用户是否已经参加活动
     */
    @ApiModelProperty("当前用户是否已经参加活动")
    private boolean hasPartakeIn;

    /**
     * 参与人数
     */
    @ApiModelProperty("参与人数")
    private Integer partakePeoples;

    /**
     * 倒计时
     */
    @ApiModelProperty("倒计时")
    private Long timestamp;

    /**
     * 奖池
     */
    @ApiModelProperty("奖池")
    private BigDecimal amount;

    /**
     * 参与列表
     */
    @ApiModelProperty("参与列表")
    private List<VoteItemVO> items;

    @ApiModelProperty("是否开启报名")
    private String pushOnRegister;

    @ApiModelProperty("报名是否结束")
    private Boolean isStopRegister;
}
