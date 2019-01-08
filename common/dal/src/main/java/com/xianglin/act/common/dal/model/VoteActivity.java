package com.xianglin.act.common.dal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/31 16:46.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteActivity extends Activity {

    /**
     * 轮播图图片
     */
    private String carouselImgs;

    /**
     * 活动结束后的展示天数
     */
    private LocalDateTime displayDate;

    /**
     * 投票形式 0:每日均可投票，1:一次性投票
     */
    private Integer voteMode;

    /**
     * 每人每天可投票数 VOTE_MODE 为0时可用
     */
    private Integer evetyDayVotes;

    /**
     * 每人可投票数 VOTE_MODE 为1时可用
     */
    private Integer allVotes;

    /**
     * 分享图表icon
     */
    private String shareIcon;

    /**
     * 分享标题
     */
    private String shareTitle;

    /**
     * 分享子标题
     */
    private String shareSubTitle;

    /**
     * 分享跳转地址
     */
    private String shareUrl;

    /**
     * 活动奖励
     */
    private String awardDesc;

    /**
     * 活动介绍
     */
    private String actDesc;

    /**
     * 投票须知
     */
    private String voteDesc;

    /**
     * 活动说明页脚
     */
    private String descFooter;
}
