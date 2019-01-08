package com.xianglin.core.model.vo;

import com.xianglin.appserv.common.service.facade.model.vo.AreaVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * @author jiang yong tao
 * @date 2018/8/23  15:41
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("好友争霸报名信息")
public class ActInviteVo {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户partyId")
    private Long partyId;

   @ApiModelProperty(value = "用户类型：区分普通用户，站长，员工")
    private String userType;

    @ApiModelProperty(value = "姓名",required = true)
    private String name;

    @ApiModelProperty(value = "性别",required = true)
    private String gender;

    @ApiModelProperty(value = "年龄",required = true)
    private Integer age;

    @ApiModelProperty(value = "手机号",required = true)
    private String mobilePhone;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "县")
    private String county;

    @ApiModelProperty(value = "镇")
    private String town;

    @ApiModelProperty(value = "村")
    private String village;

    @ApiModelProperty(value = "家乡地址")
    private String homeAddress;

    @ApiModelProperty(value = "微信信息")
    private String wxInfo;

    @ApiModelProperty(value = "认识站长手机号")
    private String nodeMobile;

    @ApiModelProperty(value = "报名时间")
    private Date signTime;

    @ApiModelProperty(value = "来源")
    private String source;

    @ApiModelProperty(value = "期望邀请数")
    private Integer expectNum;

    @ApiModelProperty(value = "邀请数")
    private Integer inviteNum;

    @ApiModelProperty(value = "注册数")
    private Integer registerNum;

    @ApiModelProperty(value = "邀请活跃用户数")
    private Integer activeNum;

    @ApiModelProperty(value = "昨日活跃用户")
    private Integer pastActiveUser;

    @ApiModelProperty(value = "昨日活跃设备")
    private Integer pastActiveDevice;

    @ApiModelProperty(value = "审核时间")
    private Date audditTime;

    @ApiModelProperty(value = "审核状态：初始，已通过，已拒绝")
    private String status;

    @ApiModelProperty(value = "报名时间(去掉秒)")
    private String applyTime;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;

    @ApiModelProperty(value = "站长姓名")
    private String nodeManagerName;

    @ApiModelProperty(value = "站点位置")
    private String nodeAddress;

    @ApiModelProperty(value = "名次")
    private Integer rank;

    @ApiModelProperty(value = "头像")
    private String headImg;

}
