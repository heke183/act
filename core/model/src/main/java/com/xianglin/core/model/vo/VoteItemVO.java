package com.xianglin.core.model.vo;

import com.xianglin.core.model.enums.VoteAwardEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/1 17:13.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("发布信息")
public class VoteItemVO {

    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * partyId
     */
    @ApiModelProperty("partyId")
    private Long partyId;

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String userName;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String headImg;

    /**
     * 序列号
     */
    @ApiModelProperty("序列号")
    private String serialNum;

    /**
     * 图片url
     */
    @ApiModelProperty("图片url")
    private String imageUrl;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 票数
     */
    @ApiModelProperty("票数")
    private int voteNum;

    /**
     * 是否显示投票按钮
     */
    @ApiModelProperty("是否显示投票按钮")
    private boolean showVoteButton;

    /**
     * 当前排名
     */
    @ApiModelProperty("当前排名")
    private int ranking;

    /**
     * 状态
     */
    @ApiModelProperty("当前排名，去拉票/领取奖品/奖品发放中/点我晒单/已结束")
    private VoteAwardEnum awardEnum;

    @ApiModelProperty("是否淘汰")
    private boolean isKnockOut;
}
