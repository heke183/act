package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 植树活动——任务
 * @author ex-jiangyongtao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_plant_task")
public class ActPlantTask {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 任务code
     */
    private String code;

    /**
     * 任务名
     */
    private String name;

    /**
     * 任务图片
     */
    private String image;

    /**
     * 描述
     */
    private String detail;

    /**
     * 每日上限
     */
    private Integer dayLimit;

    /**
     * 类型
     */
    private String type;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;

}