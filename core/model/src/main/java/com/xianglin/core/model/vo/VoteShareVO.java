package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/4 16:09.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("分享信息")
public class VoteShareVO {

    /**
     * 图标
     */
    @ApiModelProperty("图标")
    private String icon;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;

    /**
     * 子标题
     */
    @ApiModelProperty("子标题")
    private String subTitle;

    /**
     * 链接url
     */
    @ApiModelProperty("链接url")
    private String shareUrl;

    /**
     * 预览链接url
     */
    @ApiModelProperty("预览链接url")
    private String previewShareUrl;

    @ApiModelProperty("是否显示二维码/logo")
    private String LogoTyepe;

    @ApiModelProperty("二维码/Logo")
    private String Logo;

    @ApiModelProperty("分享路径")
    private String shareType;

    @ApiModelProperty("是否需要非注册用户注册")
    private String needUserRegister;

    @ApiModelProperty("是否需要非注册用户注册")
    private String voteImg;

    @ApiModelProperty("是否需要非注册用户注册")
    private String joinImg;

}
