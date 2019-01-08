package com.xianglin.core.model.exception.attendance;

import com.xianglin.act.common.util.ActPreconditions;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/22 22:50.
 */

public class AccountBalanceException extends AttendanceBaseException {

    public AccountBalanceException() {

        super();
    }

    public AccountBalanceException(String message) {

        super(message);
    }

    public AccountBalanceException(ActPreconditions.ResponseEnum responseEnum) {

        super(responseEnum);
    }

    public AccountBalanceException(ActPreconditions.ResponseEnum responseEnum, Object result) {

        super(responseEnum, result);
    }
}
