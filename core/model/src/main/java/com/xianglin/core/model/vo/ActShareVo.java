package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jiang yong tao
 * @date 2018/8/24  9:06
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("分享链接的信息")
public class ActShareVo implements Serializable{

    @ApiModelProperty(value = "分享信息内容")
    private String content;

    @ApiModelProperty(value = "分享信息内容标题")
    private String title;

    @ApiModelProperty(value = "分享信息图片")
    private String image;

    @ApiModelProperty(value = "分享信息链接")
    private String shareUrl;

    @ApiModelProperty(value = "分享信息外链")
    private String sharOutUrl;

}
