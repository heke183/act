package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * Describe :
 * Created by xingyali on 2018/12/20 9:55.
 * Update reason :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("团员的分享信息")
public class ActGroupShareVo {
    
    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "头像")
    private String headImg;

    @ApiModelProperty(value = "小程序的图片")
    private String programImg;

    @ApiModelProperty(value = "二维码")
    private String qrCode;

    @ApiModelProperty(value = "微信内容")
    private String wxContent;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "海报url")
    private String posterUrl;
}
