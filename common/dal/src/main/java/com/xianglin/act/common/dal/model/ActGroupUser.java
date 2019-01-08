package com.xianglin.act.common.dal.model;

import lombok.*;
import tk.mybatis.mapper.annotation.Version;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Describe :
 * Created by xingyali on 2018/12/19 10:30.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_group_user")
public class ActGroupUser {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 用户partyId
     */
    private Long partyId;

    /**
     * 用户手机号
     */
    private String mobilePhone;

    @Version
    private Integer version;

    /**
     *姓名 
     */
    private String name;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 当前余额
     */ 
    private BigDecimal balance;

    /**
     * 小程序openid
     */
    private String openId;

    /**
     * 小程序unionid
     */
    private String unionId;

    /**
     *二维码地址
     */
    private String qrCode;

    /**
     *分享海报地址
     */
    private String posterUrl;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;
}
