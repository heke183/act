package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 植树活动主表
 * @author ex-jiangyongtao
 * @date
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_plant")
public class ActPlant {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 用户
     */
    private Long partyId;

    /**
     * 当前爱心值
     */
    private Integer lv;

    /**
     * 总爱心值
     */
    private Integer totalLv;

    /**
     * 海报地址
     */
    private String poster;

    /**
     *个人二维码地址 
     */
    private String qr;

    /**
     * 微信openId
     */
    private String openId;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;

}
