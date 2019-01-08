package com.xianglin.core.service;

import com.xianglin.act.common.dal.model.Activity;
import com.xianglin.act.common.util.BizException;

import java.util.Date;

import static com.xianglin.act.common.util.ActPreconditions.ResponseEnum.ACT_EXPIRE;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/1 13:44.
 */
public class VoteActivityContextV2 extends ActivityContext {

    /**
     * 获取当前请求所属的活动对象
     *
     * @return
     */
    public static Activity getCurrentVoteActivity() {

        return (Activity) ActivityContext.get();
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
    public static void setCurrentVoteActivity(Activity activity) {

        ActivityContext.set(activity);
    }

    public static void checkExpire() {
        if (getCurrentVoteActivity().getExpireDate().before(new Date(System.currentTimeMillis()))) {
            throw new BizException(ACT_EXPIRE);
        }
    }

}
