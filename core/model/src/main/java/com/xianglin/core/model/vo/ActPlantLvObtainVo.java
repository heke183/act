package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiang yong tao
 * @date 2018/8/14  15:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户收取爱心值返回的信息")
public class ActPlantLvObtainVo {

    @ApiModelProperty(value = "本次收取的爱心值")
    private Integer obtainLv;


    @ApiModelProperty(value = "当前爱心值")
    private Integer currentLv;


}
