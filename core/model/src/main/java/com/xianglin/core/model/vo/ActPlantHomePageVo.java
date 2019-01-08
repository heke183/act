package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jiang yong tao
 * @date 2018/8/15  9:08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户主页信息")
public class ActPlantHomePageVo {

    @ApiModelProperty(value = "海报信息")
    private String userPoster;

    @ApiModelProperty(value = "是否弹窗")
    private Boolean actOfTips;

    @ApiModelProperty(value = "该用户所有的爱心值列表")
    private List<ActPlantLvVo> actPlantLvVos;

    @ApiModelProperty(value = "用户爱心值")
    private Integer userLv;

    @ApiModelProperty(value = "用户是否参与了活动")
    private Boolean userJoin;

    @ApiModelProperty(value = "用户头像")
    private String userHeadImg;

    @ApiModelProperty(value = "活动的code")
    private String actCode;

    @ApiModelProperty("轮播消息列表")
    private List<ActPlantNoticeVo> actPlantNoticeVoList;

    @ApiModelProperty("是否显示大转盘入口")
    private String isDisplay;

}
