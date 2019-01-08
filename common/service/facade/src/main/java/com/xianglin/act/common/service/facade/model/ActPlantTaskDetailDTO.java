package com.xianglin.act.common.service.facade.model;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Describe :
 * Created by xingyali on 2018/8/6 17:16.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActPlantTaskDetailDTO implements Serializable {
    
    private Long id;

    /**
     * 用户
     */
    private Long partyId;

    /**
     * 任务code
     */
    private String code;

    /**
     *  日期 YYYYMMDD
     */
    private String day;

    /**
     * 类型
     */
    private String type;

    /**
     * 关联数据id
     */
    private String refId;

    /**
     * 状态
     */
    private String status;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;
}
