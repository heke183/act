package com.xianglin.act.common.service.facade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Describe :
 * Created by xingyali on 2018/11/20 14:47.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteAcquireRecordDTO implements Serializable {
    
    private Long id;

    /**
     * 活动编号
     */
    private String activityCode;

    /**
     *活动名称 
     */
    private String activityName;

    /**
     *活动类型 
     */
    private String activityType;

    /**
     * 是否领取
     */
    private String isReceive;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 真实姓名
     */
    private String trueName;

    /**
     * 领取时间
     */
    private String receiveTime;

    /**
     * 奖品
     */
    private String prize;

    /**
     * 收件人
     */
    private String addressee;

    /**
     * 物流单号
     */
    private String memcCode;

    /**
     * 是否需要填物流单号 Y需要 N不需要
     */
    private String isMemecCode;
    
}
