package com.xianglin.act.common.dal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "act_step_detail")
public class ActStepDetail {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 用户 partyId
     */
    private Long partyId;

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
     * 状态 1.已兑换 0.未兑换
     */
    private String status;

    /**
     * 金币奖励数量
     */
    private Integer goldReward;

    /**
     * 统一删除标记，默认 'N'
     */
    private String isDeleted="N";

    /**
     * 兑换时间
     */
    private Date rewardTime;

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
