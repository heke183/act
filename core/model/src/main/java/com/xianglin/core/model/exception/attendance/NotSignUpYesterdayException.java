package com.xianglin.core.model.exception.attendance;

import com.xianglin.act.common.util.ActPreconditions;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/20 19:57.
 */

public class NotSignUpYesterdayException extends AttendanceBaseException {

    public NotSignUpYesterdayException() {

    }

    public NotSignUpYesterdayException(String message) {

        super(message);
    }

    public NotSignUpYesterdayException(ActPreconditions.ResponseEnum responseEnum) {

        super(responseEnum);
    }

    public NotSignUpYesterdayException(ActPreconditions.ResponseEnum responseEnum, Object result) {

        super(responseEnum, result);
    }
}
