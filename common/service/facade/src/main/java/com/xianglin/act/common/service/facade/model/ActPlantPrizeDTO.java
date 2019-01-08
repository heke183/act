package com.xianglin.act.common.service.facade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Describe :
 * Created by xingyali on 2018/8/13 10:49.
 * Update reason :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActPlantPrizeDTO {

    private static final long serialVersionUID = 2261670098381435399L;

    /**
     * id
     */
    private Long id;
    /**
     *奖品code
     */
    private String code;
    /**
     *
     */
    private String name;

    /**
     奖品图片
     */
    private String image;

    /**
     *奖品爱心值
     */
    private Integer lv;

    /**
     *类型，区分兑换奖品和奖励
     */
    private String type;

    /**
     * 类型，区分金币
     */
    private String rewardType;

    /**
     * 金币数
     */
    private Integer gold;

    /**
     *是否删除
     */
    private String isDeleted;

    /**
     *创建时间
     */
    private Date createTime;

    /**
     *更新时间
     */
    private Date updateTime;

    /**
     *备注
     */
    private String comments;
}
