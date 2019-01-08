package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Table;

/**
 * Describe :
 * Created by xingyali on 2018/8/3 10:49.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "分享")
public class ActPlantShareVo{

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;
    
    @ApiModelProperty("urlWX")
    private String urlWX;

    @ApiModelProperty("urlWB")
    private String urlWB;

    @ApiModelProperty("urlQQ")
    private String urlQQ;

    @ApiModelProperty("openId")
    private String openId;
    
}
