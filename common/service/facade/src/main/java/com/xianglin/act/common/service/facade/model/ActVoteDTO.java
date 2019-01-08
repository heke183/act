package com.xianglin.act.common.service.facade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Describe :
 * Created by xingyali on 2018/11/19 11:04.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActVoteDTO implements Serializable {
    
    private Long id;

    /**
     * 活动编号
     */
    private String activityCode;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     *活动类型 
     */
    private String type;

    /**
     *创建日期 
     */
    private String createTime;

    /**
     *活动时间
     */
    private String activityTime;

    /**
     *状态
     */
    private String status;
    
}
