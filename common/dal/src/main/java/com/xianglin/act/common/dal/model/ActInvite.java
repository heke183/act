package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_invite")
public class ActInvite {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 用户partyId
     */
    private Long partyId;

    /**
     * 用户类型：区分普通用户，站长，员工
     */
    private String userType;

    /**
     * 姓名
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
     * 村
     */
    private String village;

    /**
     * 微信信息
     */
    private String wxInfo;

    /**
     * 认识站长手机号
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
     * 期望邀请数
     */
    private Integer expectNum;

    /**
     * 邀请数
     */
    private Integer inviteNum;

    /**
     * 注册数
     */
    private Integer registerNum;

    /**
     * 邀请活跃用户数
     */
    private Integer activeNum;

    /**
     * 昨日活跃用户
     */
    private Integer pastActiveUser;

    /**
     * 昨日活跃设备
     */
    private Integer pastActiveDevice;

    /**
     * 审核时间
     */
    private Date audditTime;

    /**
     * 审核状态：初始，已通过，已拒绝
     */
    private String status;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;

}