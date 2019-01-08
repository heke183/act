package com.xianglin.act.common.service.facade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Describe :
 * Created by xingyali on 2018/8/13 10:21.
 * Update reason :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActPlantLvTranDTO implements Serializable {

    private static final long serialVersionUID = 2261670098381435399L;

    /**
     * id
     */
    private Long id;

    /**
     *用户
     */
    private Long partyId;

    /**
     *关联爱心id
     */
    private Long lvId;

    /**
     *爱心值
     */
    private Integer lv;

    /**
     * 类型
     */
    private String type;

    /**
     *奖品id
     */
    private String prizeCode;

    /**
     * 奖品名
     */
    private String name;

    /**
     *收货用户名
     */
    private String userName;

    /**
     *收货手机号
     */
    private String mobile;

    /**
     *收货地址
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

    /**
     *  是否删除
     */
    private String isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *更新时间
     */
    private Date updateTime;

    /**
     *备注
     */
    private String comments;

    /**
     * 是否填写单号
     */
    private String isOrder;
}
