package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;


/**
 * @author wanglei
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("种树活动关注用户信息")
public class ActPlantFollow implements Serializable{

    @ApiModelProperty("排名")
    private Integer rank;

    @ApiModelProperty("关注用户partyId")
    private Long partyId;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("头像")
    private String headImage;

    @ApiModelProperty("最近兑换多少笔")
    private Integer exchangeNum;

    @ApiModelProperty("当前爱心值")
    private Integer lv;

    @ApiModelProperty("是否开通种树，true：是")
    private Boolean hasPlant;

    @ApiModelProperty("最近能量可收时间（秒），0：表示有能量可收，小于0或空表示没有可收能量")
    private Long recentLv;

    @ApiModelProperty("是否邀请过,true:已邀请")
    private Boolean hasInvite;
}
