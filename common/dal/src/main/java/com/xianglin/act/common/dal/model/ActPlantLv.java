package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 植树活动——爱心表
 * @author ex-jiangyongtao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_plant_lv")
public class ActPlantLv {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 用户
     */
    private Long partyId;

    /**
     * 爱心值
     */
    private Integer lv;

    /**
     * 总爱心值
     */
    private Integer totalLv;
    /**
     * 类型
     */
    private String type;

    /**
     * 状态
     * N 表示已过期(不可用)，Y表示未使用(可用)
     */
    private String status;

    /**
     * 开始显示时间
     */
    private Date shouTime;

    /**
     * 可领取时间
     */
    private Date matureTime;

    /**
     *
     * 过期时间
     */
    private Date expireTime;

    /**
     * 关联的任务id
     */
    private Long taskId;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;

}
