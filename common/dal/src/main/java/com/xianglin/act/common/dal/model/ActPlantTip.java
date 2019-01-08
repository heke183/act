package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


/**
 * 植树活动——消息提示
 * @author ex-jiangyongtao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_plant_tip")
public class ActPlantTip {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 用户partyId
     */
    private Long partyId;

    /**
     * 类型
     */
    private String type;

    /**
     * 提示信息
     */
    private String tip;

    private String status;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;
}