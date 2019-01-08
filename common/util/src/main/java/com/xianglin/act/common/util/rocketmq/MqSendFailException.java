package com.xianglin.act.common.util.rocketmq;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/16 16:00.
 */

public class MqSendFailException extends RuntimeException {

    public MqSendFailException() {

    }

    public MqSendFailException(String message) {

        super(message);
    }

    public MqSendFailException(String message, Throwable cause) {

        super(message, cause);
    }

    public MqSendFailException(Throwable cause) {

        super(cause);
    }

    public MqSendFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }
}
