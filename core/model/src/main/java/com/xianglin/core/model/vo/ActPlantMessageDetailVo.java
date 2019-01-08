package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Describe :
 * Created by xingyali on 2018/8/3 10:33.
 * Update reason :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("消息明细列表")
public class ActPlantMessageDetailVo {

    @ApiModelProperty("日期")
    private String day;

    @ApiModelProperty("消息列表")
    private List<ActPlantTipVo> actPlantTipVos;
    
}
