package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/4 16:31.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("app用户")
public class AppUserVO {

    /**
     * partyId
     */
    @ApiModelProperty("partyId")
    private Long partyId;

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String userName;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String headImg;

    /**
     * 是否实名认证
     */
    @ApiModelProperty("是否实名认证")
    private Boolean isAuth;

    /**
     * 简介
     */
    @ApiModelProperty("简介")
    private String introduce;

    /**
     * 地址
     */
    @ApiModelProperty("地址")
    private String district;
}
