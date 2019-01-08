package com.xianglin.core.model.exception.attendance;

import com.xianglin.act.common.util.ActPreconditions;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/20 19:56.
 */

public class HasSignInTodayException extends AttendanceBaseException {

    public HasSignInTodayException() {

    }

    public HasSignInTodayException(String message) {

        super(message);
    }

    public HasSignInTodayException(ActPreconditions.ResponseEnum responseEnum) {

        super(responseEnum);
    }

    public HasSignInTodayException(ActPreconditions.ResponseEnum responseEnum, Object result) {

        super(responseEnum, result);
    }
}
