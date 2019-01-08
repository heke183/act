package com.xianglin.core.model.exception.attendance;

import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.BizException;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/22 20:04.
 */

public class AttendanceBaseException extends BizException {

    public AttendanceBaseException() {

        super();
    }

    public AttendanceBaseException(String message) {

        super(message);
    }

    public AttendanceBaseException(ActPreconditions.ResponseEnum responseEnum) {

        super(responseEnum);
    }

    public AttendanceBaseException(ActPreconditions.ResponseEnum responseEnum, Object result) {

        super(responseEnum, result);
    }
}
