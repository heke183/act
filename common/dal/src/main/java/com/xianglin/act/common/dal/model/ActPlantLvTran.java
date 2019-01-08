package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 植树活动——爱心交易明细
 * @author ex-jiangyongtao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_plant_lv_tran")
public class ActPlantLvTran {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 用户
     */
    private Long partyId;

    /**
     * 关联爱心id
     */
    private Long lvId;

    /**
     * 爱心值
     */
    private Integer lv;

    /**
     * 类型
     */
    private String type;

    /**
     * 奖品code
     */
    private String prizeCode;

    /**
     * 奖品名
     */
    private String name;

    /**
     * 收货用户名
     */
    private String userName;

    /**
     * 收货手机号
     */
    private String mobile;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 状态
     */
    private String status;

    /**
     * 客服备注
     */
    private String remark;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;

    @Transient
    private String isOrder;

}
