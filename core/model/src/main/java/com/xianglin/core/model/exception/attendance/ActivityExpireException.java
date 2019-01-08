package com.xianglin.core.model.exception.attendance;

import com.xianglin.act.common.util.ActPreconditions;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/18 17:43.
 */

public class ActivityExpireException extends AttendanceBaseException {

    public ActivityExpireException() {

    }

    public ActivityExpireException(String message) {

        super(message);
    }

    public ActivityExpireException(ActPreconditions.ResponseEnum responseEnum) {

        super(responseEnum);
    }

    public ActivityExpireException(ActPreconditions.ResponseEnum responseEnum, Object result) {

        super(responseEnum, result);
    }
}
