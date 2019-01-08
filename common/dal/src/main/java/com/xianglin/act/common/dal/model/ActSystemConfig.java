package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.Table;
import java.util.Date;

/**
 * 植树活动——活动配置
 * @author ex-jiangyongtao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_system_config")
public class ActSystemConfig {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 配置code
     */
    private String configCode;

    /**
     * 描述
     */
    private String configDesc;

    /**
     * 统一删除标记
     */
    private String isDeleted;

    /** */
    private String creator;

    /** */
    private String updater;

    /** */
    private Date createDate;

    /** */
    private Date updateDate;

    /** */
    private String comments;

    /** */
    private String configValue;

}