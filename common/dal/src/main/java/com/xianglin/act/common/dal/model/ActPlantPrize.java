package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 植树活动——奖励
 * @author ex-jiangyongtao
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_plant_prize")
public class ActPlantPrize {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 奖品code
     */
    private String code;

    /** 奖品名*/
    private String name;

    /**
     * 奖品图片
     */
    private String image;

    /**
     * 奖品爱心值
     */
    private Integer lv;

    /**
     * 类型，区分兑换奖品和奖励
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

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;

}