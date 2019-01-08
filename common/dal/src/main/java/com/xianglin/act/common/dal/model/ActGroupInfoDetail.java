package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Describe :
 * Created by xingyali on 2018/12/19 10:31.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_group_info_detail")
public class ActGroupInfoDetail {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;
    
    private Long infoId;

    /**
     * 参与用户
     */
    private Long partyId;
    
    /**
     * 分得金额
     */
    private BigDecimal balance;

    /**
     *状态
     */
    private String status;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;
}
