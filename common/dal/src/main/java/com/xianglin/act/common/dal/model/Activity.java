package com.xianglin.act.common.dal.model;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @author yefei
 * @date 2018-01-23 12:39
 */
@Table(name = "act_activity")
@Data
public class Activity {

    @Transient
    private int type;

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 活动标签
     */
    private String activityCode;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动开始时间
     */
    private Date startDate;

    /**
     * 活动结束日期
     */
    private Date expireDate;

    /**
     * 活动主图
     */
    private String activityMainImg;

    /**
     * 主图跳转页面
     */
    private String activityMainImgDest;

    /**
     * 活动说明/规则
     */
    private String activityRule;

    /**
     * 统一删除标记
     */
    private String isDeleted;

    private String creator;

    private String updater;

    private Date createDate;

    private Date updateDate;

    private String comments;

}
