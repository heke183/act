package com.xianglin.act.common.dal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "act_vote_rel")
public class ActVoteRel implements Serializable {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 关联act_vote_target中的记录
     */
    private String activityCode;

    /**
     * partyId 投票人
     */
    private Long partyId;

    /**
     * 给toPartyId投票
     */
    private Long toPartyId;

    /**
     * 用户类型（新用户还是老用户）
     */
    private String userType;

    /**
     * 统一删除标记
     */
    private String isDeleted;

    private String creator;

    private String updater;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String comments;

    private static final long serialVersionUID = 1L;

    /**
     * 金额（金币）
     */
    private BigDecimal amount;

    /**
     * 获得的金币
     */
    private BigDecimal awardAmount;

    /**
     * 状态
     */
    private String status;

    /**
     * 比例
     */
    @Transient
    private BigDecimal ratio;

    @Transient
    private String name;

}