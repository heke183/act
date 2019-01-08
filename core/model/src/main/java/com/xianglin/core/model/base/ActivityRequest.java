package com.xianglin.core.model.base;

import com.xianglin.core.model.enums.CustomerTypeEnum;

/**
 * @author yefei
 * @date 2018-01-25 10:36
 */
public class ActivityRequest<T> {

    private String ip;

    private String signature;

    private String securityKey;

    private T request;

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public T getRequest() {
        return request;
    }

    public void setRequest(T request) {
        this.request = request;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ActivityRequest{");
        sb.append("ip='").append(ip).append('\'');
        sb.append(", signature='").append(signature).append('\'');
        sb.append(", securityKey='").append(securityKey).append('\'');
        sb.append(", request=").append(request);
        sb.append('}');
        return sb.toString();
    }
}
