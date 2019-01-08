package com.xianglin.act.common.dal.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author yefei
 * @date 2018-01-22 13:30
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("获奖记录")
@Table(name = "act_customer_acquire_record")
public class CustomerAcquire {

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

    @ApiModelProperty("奖品code")
    private String prizeCode;

    private String status;

    private Date acquireDate;

    private String headImageUrl;

    private String memcCode;

    @ApiModelProperty("奖品价值")
    private BigDecimal prizeValue;

    private String userType;

    @Transient
    private String prizeType;

    @Transient
    private String oldStatus;
    
    private String isDeleted;

    @Transient
    private String prizeName;

    @Transient
    private String prizeImage;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;
}
