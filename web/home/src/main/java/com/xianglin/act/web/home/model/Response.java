package com.xianglin.act.web.home.model;

import com.xianglin.act.common.util.ActPreconditions;

/**
 * @author yefei
 * @date 2018-01-18 14:31
 */
public class Response<T> {

    private String code;

    private String message;

    private String tips;

    private T result;

    public Response() {

        this.code = ActPreconditions.ResponseEnum.SUCCESS.code;
        this.message = ActPreconditions.ResponseEnum.SUCCESS.message;
        this.tips = ActPreconditions.ResponseEnum.SUCCESS.tips;
    }

    public void setResponseEnum(ActPreconditions.ResponseEnum responseEnum) {

        this.code = responseEnum.code;
        this.message = responseEnum.message;
        this.tips = responseEnum.tips;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public String getCode() {

        return code;
    }

    public void setCode(String code) {

        this.code = code;
    }

    public String getTips() {

        return tips;
    }

    public void setTips(String tips) {

        this.tips = tips;
    }

    public T getResult() {

        return result;
    }

    public void setResult(T result) {

        this.result = result;
    }

    /**
     * 工厂方法
     * created by Yungyu
     *
     * @return
     */
    public static <T> Response<T> ofSuccess() {

        return new Response<>();
    }

    /**
     * 工厂方法
     * created by Yungyu
     *
     * @param result
     * @return
     */
    public static <T> Response<T> ofSuccess(T result) {

        Response<T> Response = new Response<>();
        Response.setResult(result);
        return Response;
    }

    /**
     * 工厂方法
     * created by Yungyu
     *
     * @param message
     * @return
     */
    public static <T> Response<T> ofFail(String message) {

        Response<T> response = new Response<>();
        response.setResponseEnum(ActPreconditions.ResponseEnum.FAIL);
        response.setTips(message);
        response.setMessage(message);

        return response;
    }

}
