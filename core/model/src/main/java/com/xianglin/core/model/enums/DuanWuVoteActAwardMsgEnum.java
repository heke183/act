package com.xianglin.core.model.enums;

import com.xianglin.act.common.service.facade.constant.ActivityConfig;
import com.xianglin.core.model.base.IVoteActAwardMsg;
import com.xianglin.core.model.base.VoteActAwardMsg;

/**
 * 端午节投票奖励
 *  完善提示语
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/4 17:21.
 */
@VoteActAwardMsg(activityCode = "HD001")
public enum DuanWuVoteActAwardMsgEnum implements IVoteActAwardMsg {
    SPECIAL_PRIZE("恭喜你，获得乡邻投票活动特等奖iphoneX一部，请去投票活动-我的中点领取奖励，速来领取，过期作废！",
            "恭喜你，获得乡邻投票活动特等奖iphoneX一部，请去投票活动-我的中点领取奖励，速来领取，过期作废！"),
    FIRST_PRIZE("恭喜你，获得乡邻投票活动一等奖{0}元，请去乡邻投票活动-我的中点领取奖励，速来领取，过期作废！",
            "恭喜你，获得乡邻投票活动一等奖#{XXX}元，请去乡邻投票活动-我的中点领取奖励，速来领取，过期作废！"),
    SECOND_PRIZE("恭喜你，获得乡邻投票活动二等奖{0}元话费券，请去乡邻投票活动-我的中点领取奖励，速来领取，过期作废！",
            "恭喜你，获得乡邻投票活动二等奖#{XXX}元话费券，请去乡邻投票活动-我的中点领取奖励，速来领取，过期作废！"),
    THIRD_PRIZE("恭喜你，获得乡邻投票活动三等奖{0}元购物券，请去乡邻投票活动-我的中点领取奖励，速来领取，过期作废！",
            "恭喜你，获得乡邻投票活动三等奖#{XXX}元购物券，请去乡邻投票活动-我的中点领取奖励，速来领取，过期作废！"),
    FOURTH_PRIZE("恭喜你，获得乡邻投票活动四等奖保温杯，请去投票活动-我的中点领取奖励，速来领取，过期作废！",
            "恭喜你，获得乡邻投票活动四等奖保温杯，请去投票活动-我的中点领取奖励，速来领取，过期作废！"),
    LUCKY_PRIZE("恭喜你，获得乡邻投票活动幸运奖2元话费券，请去乡邻投票活动-我的中点领取奖励，速来领取，过期作废！",
            "恭喜你，获得乡邻投票活动幸运奖2元话费券，请去乡邻投票活动-我的中点领取奖励，速来领取，过期作废！"),
    EC_PHONE_COUPON("恭喜你，获得乡邻投票活动阳光普照奖话费券一张，请去乡邻投票活动-我的中点领取奖励，速来领取，过期作废！",
            "恭喜你，获得乡邻投票活动阳光普照奖话费券一张，请去乡邻投票活动-我的中点领取奖励，速来领取，过期作废！");

    private String appMsg;

    private String smsMsg;

    DuanWuVoteActAwardMsgEnum(String appMsg, String smsMsg) {

        this.appMsg = appMsg;
        this.smsMsg = smsMsg;
    }

    @Override
    public String getSmsMessageTemp() {

        return smsMsg;
    }

    @Override
    public String getAppMessageTemp() {

        return appMsg;
    }
}
