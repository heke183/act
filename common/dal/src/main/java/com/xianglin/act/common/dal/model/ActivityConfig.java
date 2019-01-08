package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;


/**
 * 活动配置
 */
@Table(name = "act_activity_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ActivityConfig {

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
     * 配置key CONFIG_KEY
     */
    private String configKey;

    /**
     * 配置值 CONFIG_VALUE
     */
    private String configValue;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;

}
