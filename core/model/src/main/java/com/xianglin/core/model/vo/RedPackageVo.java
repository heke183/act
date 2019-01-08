package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author jiang yong tao
 * @date 2018/12/25  9:53
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("我的红包")
public class RedPackageVo {

    @ApiModelProperty("累计金额")
    private String balanceSum;

    @ApiModelProperty("用户当前余额")
    private String balance;

    @ApiModelProperty("订单数量")
    private Integer entityCount;

    @ApiModelProperty("发放红包数量")
    private Integer groupCount;

    @ApiModelProperty("收到的红包数量")
    private Integer joinCount;

    @ApiModelProperty("失效的红包数量")
    private Integer loosedCount;

    @ApiModelProperty("优惠券数量")
    private Integer couponCount;

    @ApiModelProperty("提示消息列表")
    private List<ActGroupTipsVo> actGroupTipsVoList;
}
