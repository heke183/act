package com.xianglin.core.model.exception.attendance;

import com.xianglin.act.common.util.ActPreconditions;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/18 13:54.
 */

public class ActivityNotExistException extends AttendanceBaseException {

    public ActivityNotExistException() {

    }

    public ActivityNotExistException(String message) {

        super(message);
    }

    public ActivityNotExistException(ActPreconditions.ResponseEnum responseEnum) {

        super(responseEnum);
    }

    public ActivityNotExistException(ActPreconditions.ResponseEnum responseEnum, Object result) {

        super(responseEnum, result);
    }
}
