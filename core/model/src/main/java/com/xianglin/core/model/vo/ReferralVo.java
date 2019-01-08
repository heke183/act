package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @author jiang yong tao
 * @date 2018/8/28  16:04
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("为他拉票")
public class ReferralVo {

    @ApiModelProperty(value = "推荐码")
    private String code;

    @ApiModelProperty(value = "名字")
    private String name;

    @ApiModelProperty(value = "头像")
    private String headImg;

}
