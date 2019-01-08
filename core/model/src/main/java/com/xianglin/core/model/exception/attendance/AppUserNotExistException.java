package com.xianglin.core.model.exception.attendance;

import com.xianglin.act.common.util.ActPreconditions;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/8 15:18.
 */

public class AppUserNotExistException extends AttendanceBaseException {

    public AppUserNotExistException() {

        super();
    }

    public AppUserNotExistException(String message) {

        super(message);
    }

    public AppUserNotExistException(ActPreconditions.ResponseEnum responseEnum) {

        super(responseEnum);
    }

    public AppUserNotExistException(ActPreconditions.ResponseEnum responseEnum, Object result) {

        super(responseEnum, result);
    }
}
