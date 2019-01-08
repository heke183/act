package com.xianglin.act.common.dal.model;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/20 19:25.
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author yefei
 * @date 2018-01-22 13:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "act_customer_acquire_record")
public class ActivityPartake {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    private Long partyId;

    private String userName;

    private String activityCode;

    private String mobilePhone;

    private String prizeCode;

    private LocalDateTime acquireDate;

    private LocalDateTime signInDate;

    private String headImageUrl;

    private String memcCode;

    private BigDecimal prizeValue;

    private String userType;

    private String status;

    private BigDecimal activityFee;

    /**
     * 统一删除标记
     */
    private Boolean isDeleted;

    private String creator;

    private String updater;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String comments;

}
