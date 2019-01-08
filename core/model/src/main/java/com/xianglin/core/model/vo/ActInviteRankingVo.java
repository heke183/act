package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @author jiang yong tao
 * @date 2018/8/27  11:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("好友争霸排行榜信息")
public class ActInviteRankingVo {

    @ApiModelProperty(value = "partyId")
    public Long partyId;

    @ApiModelProperty(value = "排序(名次)")
    public Integer order;

    @ApiModelProperty(value = "头像")
    public String headImage;

    @ApiModelProperty(value = "姓名")
    public String name;

    @ApiModelProperty(value = "票数")
    public Integer ticketNum;
}
