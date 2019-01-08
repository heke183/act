package com.xianglin.act.common.dal.model;

import lombok.*;
import tk.mybatis.mapper.annotation.Version;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Describe :
 * Created by xingyali on 2018/12/19 10:30.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_group_info")
public class ActGroupInfo {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * partyid
     */
    private Long partyId;

    @Version
    private Integer version;

    /**
     *团样式 
     */
    private String style;

    /**
     *红包总金额
     */
    private BigDecimal totalBalance;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 团状态
     */
    private String status;
    

    /**
     * 参与人员partyId拼接
     */
    private String partner;
    
    private String isDeleted;

    private Date createTime;
    
    private Date updateTime;
    
    private String comments;
}
