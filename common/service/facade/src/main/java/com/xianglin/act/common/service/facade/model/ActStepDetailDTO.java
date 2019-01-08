package com.xianglin.act.common.service.facade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActStepDetailDTO implements Serializable {

    private static final long serialVersionUID = -2547839261270745010L;

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 用户 partyId
     */
    private Long partyId;

    /**
     * 显示名称
     */
    private String showName;

    /**
     * 日期
     */
    private String day;

    /**
     * 类型，区分四个时间段及全部
     */
    private String type;

    /**
     * 步数
     */
    private Integer stepNumber;

    /**
     * 状态
     */
    private String status;

    /**
     * 金币奖励数量
     */
    private Integer goldReward;

    /**
     * 兑换时间
     */
    private String rewardTime;

    /**
     * 统一删除标记，默认 'N'
     */
    private String isDeleted;

    /**
     * 开始时间，默认为 NULL
     */
    private Date createTime;

    /**
     * 更新时间，默认为 NULL
     */
    private Date updateTime;

    /**
     * 备注，默认为 NULL
     */
    private String comments;

}
