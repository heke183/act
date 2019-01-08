package com.xianglin.core.model.enums;

/**
 * @author yefei
 * @date 2018-04-02 18:02
 */
public enum UserType {
    REGULAR_CUSTOMER,
    NEW_CUSTOMER,
    //拼团
    GROUP_PARTAKE,

    // 分享者
    RP_SHARER,
    // 参与者
    RP_PARTAKER,
    //打卡用户
    ATTENDANCE_USER,
    // 参与者 老用户
    RP_PARTAKER_OLD,
    // 投票者
    VOTE_VOTER,
    VOTE_PARTAKE,
    VOTER_APP_USER,
    VOTER_NEW;

    public static UserType parse(String code) {
        for (UserType userType : UserType.values()) {
            if (userType.name().equals(code)) {
                return userType;
            }
        }
        return null;
    }

}
