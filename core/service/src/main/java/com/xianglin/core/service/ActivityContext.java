package com.xianglin.core.service;

import com.xianglin.act.common.dal.model.Activity;

/**
 * @author yefei
 * @date 2018-07-06 8:40
 */
public class ActivityContext {

    private static final ThreadLocal<Activity> CURRENT_ACTIVITY = ThreadLocal.withInitial(() -> null);

    public static Activity get() {
        return CURRENT_ACTIVITY.get();
    }

    public static void set(Activity activity) {
        CURRENT_ACTIVITY.set(activity);
    }

    public static String getActivityCode() {
        final Activity activity = get();
        if (activity != null) {
            return activity.getActivityCode();
        }
        return null;
    }

    public static String getActivityName() {
        final Activity activity = get();
        if (activity != null) {
            return activity.getActivityName();
        }
        return null;
    }

    public static void remove() {
        CURRENT_ACTIVITY.remove();
    }
}
