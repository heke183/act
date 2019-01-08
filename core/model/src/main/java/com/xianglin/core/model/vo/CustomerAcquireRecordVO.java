package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author jiang yong tao
 * @date 2018/12/21  11:28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("礼品交易明细")
public class CustomerAcquireRecordVO {
    private Long id;

    private Long partyId;

    @ApiModelProperty("用户姓名")
    private String userName;

    @ApiModelProperty("活动code")
    private String activityCode;

    @ApiModelProperty("用户手机号")
    private String mobilePhone;

    @ApiModelProperty("奖品code")
    private String prizeCode;

    private String status;

    @ApiModelProperty("获奖时间")
    private Date acquireDate;

    @ApiModelProperty("头像地址")
    private String headImageUrl;

    @ApiModelProperty("物流单号")
    private String memcCode;

    @ApiModelProperty("奖品价值")
    private BigDecimal prizeValue;

    @ApiModelProperty("用户类型")
    private String userType;

    private String prizeType;

    private String oldStatus;

    private String isDeleted;

    private String prizeName;

    private String prizeImage;

    private String toTime;

    private String fromTime;

    private String couponListUrl;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;
}
