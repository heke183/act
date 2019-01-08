package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 植树活动——任务明细
 * @author ex-jiangyongtao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_plant_task_detail")
public class ActPlantTaskDetail {

    @Id
    @GeneratedValue(generator = "JDBC")
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
