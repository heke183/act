package com.xianglin.act.common.service.facade.model;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Describe :
 * Created by xingyali on 2018/8/23 14:54.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ActInviteDTO implements Serializable {

    private static final long serialVersionUID = 2261670098381435399L;
   
    private Long id;

    /**
     * 用户的partyID
     */
    private Long partyId;

    /**
     * 用户类型
     */
    private String userType;

    /**
     *姓名
     */
    private String name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 手机号
     */
    private String mobilePhone;
    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 县
     */
    private String county;

    /**
     * 镇
     */
    private String town;

    /**
     * 乡
     */
    private String village;

    /**
     * 微信信息
     */
    private String wxInfo;

    /**
     *认识站长手机号 
     */
    private String nodeMobile;

    /**
     * 报名时间
     */
    private Date signTime;

    /**
     * 来源
     */
    private String source;

    /**
     *期望邀请数 
     */
    private Integer expectNum;

    /**
     *邀请数 
     */
    private Integer inviteNum;

    /**
     *注册数 
     */
    private Integer registerNum;
    
    /**
     *邀请活跃用户数 
     */
    private Integer activeNum;

    /**
     * 昨日活跃用户
     */
    private Integer pastActiveUser;

    /**
     *昨日活跃设备 
     */
    private Integer pastActiveDevice;

    /**
     *审核时间 
     */
    private Date audditTime;

    /**
     * 审核状态：初始，已通过，已拒绝
     */
    private String status;

    /**
     *删除状态 
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
     * 备注
     */
    private String comments;

    /**
     * 报名时间
     */
    private String startDate;

    /**
     * 截止时间
     */
    private String endDate;

    /**
     * 开始年龄
     */
    private int startAge;

    /**
     * 结束年龄
     */
    private int endAge;

    /**
     * 用户
     */
    private String user;

    /**
     * 站长姓名
     */
    private String nodeManagerName;

    /**
     *站长地址 
     */
    private String nodeAddress;

}
