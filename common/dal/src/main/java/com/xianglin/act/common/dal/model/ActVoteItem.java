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
import java.time.LocalDateTime;

/**
 * @author
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "act_vote_item")
public class ActVoteItem implements Serializable {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 序号
     */
    private Integer orderNumber;

    /**
     * 投票活动code
     */
    private String activityCode;

    /**
     * partyId
     */
    private Long partyId;

    /**
     * 图片，保证容量冗余
     */
    private String images;

    /**
     * 描述
     */
    private String description;

    /**
     * 基础投票数，用于票数显示假数据
     */
    private Integer baseVoteNum;

    /**
     * 真实投票数，冗余字段
     */
    private Integer realVoteNum;

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
     * 排名
     */
    @Transient
    private int ranking;

    /**
     * 状态
     */
    private String status;

    /**
     * 是否投过票
     */
    @Transient
    private boolean voted;

}