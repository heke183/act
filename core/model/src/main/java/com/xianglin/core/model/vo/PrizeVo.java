package com.xianglin.core.model.vo;

import com.xianglin.act.common.dal.enums.PrizeEnum;
import com.xianglin.act.common.dal.enums.PrizeType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;

/**
 * @author jiang yong tao
 * @date 2018/12/19  10:09
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("奖品信息")
public class PrizeVo {

    private Long id;

    @ApiModelProperty("活动code")
    private String activityCode;

    @ApiModelProperty("奖品名称")
    private String prizeName;

    @ApiModelProperty("优惠券名称")
    private String couponName;

    @ApiModelProperty("优惠券名称")
    private String prizeImage;

    @ApiModelProperty("奖品code")
    private String prizeCode;

    @ApiModelProperty("奖品类型")
    private String prizeType;

    @ApiModelProperty("奖品描述")
    private String prizeDesc;

    @ApiModelProperty("奖品等级")
    private int prizeLevel;

    @ApiModelProperty("奖品价格")
    private BigDecimal unitRmb;
}
