/**
 *
 */
package com.xianglin.act.common.service.facade.model;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.xianglin.act.common.service.facade.constant.FacadeEnums;

import java.io.Serializable;

/**
 * 通用服务响应结果
 *
 * @author pengpeng 2016年2月18日下午4:04:56
 */
public class Response<T> implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5670390880829918366L;

    /**
     * 成功的结果码
     */
    private static final int SUCCESS_CODE = 1000;

    /**
     * 响应结果码
     */
    private int code;

    /**
     * 结果描述，给调用方系统看的描述信息
     */
    private String memo;

    /**
     * 返回给调用方，需要显示给用户的的友好提示信息，请谨慎定义
     */
    private String tips;

    /**
     * 服务结果
     */
    private T result = null;

    public Response() {

        this.code = FacadeEnums.OK.code;
        this.memo = FacadeEnums.OK.msg;
        this.tips = FacadeEnums.OK.tip;
    }

    public void setFacade(FacadeEnums facadeEnums) {

        this.code = facadeEnums.code;
        this.memo = facadeEnums.msg;
        this.tips = facadeEnums.tip;
    }

    /**
     * 操作是否成功
     *
     * @return
     */
    public boolean isSuccess() {

        return code == SUCCESS_CODE;
    }

    /**
     * @return the code
     */
    public int getCode() {

        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(int code) {

        this.code = code;
    }

    /**
     * @return the memo
     */
    public String getMemo() {

        return memo;
    }

    /**
     * @param memo the memo to set
     */
    public void setMemo(String memo) {

        this.memo = memo;
    }

    /**
     * @return the tips
     */
    public String getTips() {

        return tips;
    }

    /**
     * @param tips the tips to set
     */
    public void setTips(String tips) {

        this.tips = tips;
    }

    /**
     * @return the result
     */
    public T getResult() {

        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(T result) {

        this.result = result;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return "Response [code=" + code + ", memo=" + memo + ", tips=" + tips + ", result=" + result + "]";
    }

    /**
     * 检查响应，静态导入后可用于检查响应结果并返回响应结果
     *
     * @param response
     * @param <T>
     * @return
     * @throws IllegalArgumentException 响应失败
     */
    public static <T> Optional<T> checkResponse(Response<T> response) {

        if (response == null) {
            throw new IllegalStateException("rpc 调用失败，返回为空");
        }
        if (!response.isSuccess()) {
            throw new IllegalArgumentException(response.getTips());
        }
        T result = response.getResult();
        return Optional.fromNullable(result);
    }

    /**
     * 成功响应
     *
     * @param data
     * @return
     */
    public static <T> Response<T> ofSuccess(T data) {

        Response<T> response = new Response<>();
        response.setResult(data);
        return response;

    }

    /**
     * 失败响应的工厂方法(带提示)
     *
     * @param message
     * @return
     */
    public static <T> Response<T> ofFail(String message) {

        Response<T> response = new Response<>();
        response.setFacade(FacadeEnums.FAIL);
        if (!Strings.isNullOrEmpty(message)) {
            response.setTips(message);
        }
        return response;
    }

    /**
     * 失败响应的工厂方法(带提示)
     *
     * @param message
     * @return
     */
    public static <T> Response<T> ofFail(int code,String message) {

        Response<T> response = new Response<>();
        response.setCode(code);
        if (!Strings.isNullOrEmpty(message)) {
            response.setTips(message);
        }
        return response;
    }

    /**
     * 失败响应的工厂方法
     *
     * @return
     */
    public static <T> Response<T> ofFail() {

        return ofFail(null);
    }


}
