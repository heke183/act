package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Describe :
 * Created by xingyali on 2018/12/19 10:32.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_group_balance_tran")
public class ActGroupBalanceTran {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 用户partyId
     */
    private Long partyId;

    /**
     *交易号 
     */
    private String tranId;

    /**
     *变动金额
     */
    private BigDecimal changeValue;

    /**
     *类型
     */
    private String type;

    /**
     * 子类型
     */
    private String subType;

    /**
     *备注信息
     */
    private String remark;

    /**
     *状态
     */
    private String status;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;
}
