package com.xianglin.core.service;

import com.xianglin.act.common.dal.model.VoteActivity;
import com.xianglin.act.common.util.BizException;

import java.time.LocalDateTime;

import static com.xianglin.act.common.util.ActPreconditions.ResponseEnum.ACT_EXPIRE;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/1 13:44.
 */
public class VoteActivityContext extends ActivityContext {

    /**
     * 获取当前请求所属的活动对象
     *
     * @return
     */
    public static VoteActivity getCurrentVoteActivity() {

        return (VoteActivity) ActivityContext.get();
    }

    /**
     * 获取当前请求所属的活动CODE
     *
     * @return
     */
    public static String getVoteActivityCode() {

        return getCurrentVoteActivity().getActivityCode();
    }

    /**
     * 获取当前请求所属的活动名字
     *
     * @return
     */
    public static String getVoteActivityName() {

        return getCurrentVoteActivity().getActivityName();
    }

    /**
     * 获取当前请求所属的活动对象
     *
     * @return
     */
    public static void setCurrentVoteActivity(VoteActivity voteActivity) {

        ActivityContext.set(voteActivity);
    }

    public static void checkExpire() {
        if (getCurrentVoteActivity().getDisplayDate().isBefore(LocalDateTime.now())) {
            throw new BizException(ACT_EXPIRE);
        }
    }

}
