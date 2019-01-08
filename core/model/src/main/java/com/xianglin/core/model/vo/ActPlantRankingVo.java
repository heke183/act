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
 * Created by xingyali on 2018/8/3 10:13.
 * Update reason :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("种树活动排行榜")
public class ActPlantRankingVo {

    @ApiModelProperty("排行榜列表")
    private List<ActPlantVo> actPlantVoList;

    @ApiModelProperty("用户再榜内的状态 第一名：ONE;在榜内;OnRank;榜上无名:NotRank")
    private String status;

    @ApiModelProperty("跳转地址")
    private String url;
    
}
