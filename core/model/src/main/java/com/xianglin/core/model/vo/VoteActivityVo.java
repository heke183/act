package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Describe :
 * Created by xingyali on 2018/11/19 11:20.
 * Update reason :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteActivityVo {

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
     *未开始    NOTBEGIN
     *未发布   UNPUBLISH
     * 进行中  ONGOING
     * 已结束  FINISH
     */
    private String status; 
}
