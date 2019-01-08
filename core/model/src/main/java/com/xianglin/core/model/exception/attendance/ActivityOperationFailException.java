package com.xianglin.core.model.exception.attendance;

import com.xianglin.act.common.util.ActPreconditions;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/18 18:02.
 */

public class ActivityOperationFailException extends AttendanceBaseException {

    public ActivityOperationFailException() {

    }

    public ActivityOperationFailException(String message) {

        super(message);
    }

    public ActivityOperationFailException(ActPreconditions.ResponseEnum responseEnum) {

        super(responseEnum);
    }

    public ActivityOperationFailException(ActPreconditions.ResponseEnum responseEnum, Object result) {

        super(responseEnum, result);
    }
}
