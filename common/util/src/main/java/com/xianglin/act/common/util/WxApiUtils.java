package com.xianglin.act.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 微信一些接口的调用
 *
 * @author yefei
 * @date 2018-04-09 19:18
 */
public class WxApiUtils {

    private final static String OPEN_ID = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
            "appid=$0&" +
            "secret=$1&" +
            "code=$2&" +
            "grant_type=authorization_code";

    private final static String AUTH = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
            "appid=$0&" +
            "redirect_uri=$1&" +
            "response_type=code&scope=snsapi_base&" +
            "state=123#wechat_redirect";

    private String appid;

    private String secret;

    private String redirectUrl;

    /**
     * 静默登陆根据 code 获得openId
     */
    public String getOpenId(String code) {
        String requestUrl = OPEN_ID;
        requestUrl = requestUrl.replace("$0", appid)
                .replace("$1", secret)
                .replace("$2", code);

        String s = HttpUtils.executeGet(requestUrl);
        JSONObject jsonObject = JSON.parseObject(s);
        return jsonObject.getString("openid");
    }

    public String getAuthUrl(long partyId) throws UnsupportedEncodingException {
        String auth = AUTH;
        auth = auth.replace("$0", appid)
                .replace("$1", URLEncoder.encode(redirectUrl, "utf-8") + "?partyId=" + partyId);
        return auth;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
