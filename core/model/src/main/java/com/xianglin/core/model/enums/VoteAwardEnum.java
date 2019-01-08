package com.xianglin.core.model.enums;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/4 15:52.
 */
public enum VoteAwardEnum {
    //去拉票
    VOTING,
    //领取奖品
    GRANT,
    //发放中
    GRANTING,
    //晒单
    SHARED,
    //已结束
    ENDED;

    public static VoteAwardEnum parse(String status) {

        for (VoteAwardEnum voteAwardEnum : VoteAwardEnum.values()) {
            if (voteAwardEnum.name().equals(status)) {
                return voteAwardEnum;
            }
        }
        return null;
    }
}
