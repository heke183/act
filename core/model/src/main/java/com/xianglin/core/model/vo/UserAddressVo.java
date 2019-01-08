package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiang yong tao
 * @date 2018/8/17  16:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("返回用户信息")
public class UserAddressVo {

    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "是否实名认证")
    private Boolean isAuth;

}
