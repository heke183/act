package com.xianglin.core.model.vo;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.xianglin.act.common.dal.model.ActInvite;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author jiang yong tao
 * @date 2018/8/23  15:59
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("好友争霸主页信息")
public class ActInviteHomePageVo {

    @ApiModelProperty(value = "报名时间")
    public String applyTime;

    @ApiModelProperty(value = "活动时间")
    public String activityTime;

    @ApiModelProperty(value = "排行榜/已报名名单")
    public List<ActInviteVo> actInviteVos;

    @ApiModelProperty(value = "报名倒计时")
    public Long recentApplyTime;

    @ApiModelProperty(value = "显示状态")
    public String status;

    @ApiModelProperty(value = "是否报名")
    public Boolean isApply;

    @ApiModelProperty(value = "登录状态")
    public Boolean isLogin;

    /****************(二期)****************************/

    @ApiModelProperty(value = "活动剩余天数")
    public Long recentDays;
}
