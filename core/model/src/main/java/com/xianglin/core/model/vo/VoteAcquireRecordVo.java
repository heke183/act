package com.xianglin.core.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Describe :
 * Created by xingyali on 2018/11/20 15:19.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteAcquireRecordVo {
    
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
     * 物流单号
     */
    private String memcCode;
}
