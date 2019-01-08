package com.xianglin.act.common.util;

/**
 * @author yefei
 * @date 2018-01-18 14:37
 */
public class BizException extends RuntimeException {

    private ActPreconditions.ResponseEnum responseEnum;

    private Object result;

    public BizException() {
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(ActPreconditions.ResponseEnum responseEnum) {
        super(responseEnum.tips);
        this.responseEnum = responseEnum;
    }

    public BizException(ActPreconditions.ResponseEnum responseEnum, Object result) {
        super(responseEnum.tips);
        this.responseEnum = responseEnum;
        this.result = result;
    }

    public ActPreconditions.ResponseEnum getResponseEnum() {
        return responseEnum;
    }

    public Object getResult() {
        return result;
    }
}
