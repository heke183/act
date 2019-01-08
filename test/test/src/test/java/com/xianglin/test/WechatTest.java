package com.xianglin.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xianglin.act.common.util.HttpUtils;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 * @date 2018-03-29 9:12
 */
public class WechatTest {

    /**
     * 静默登陆 根据code 获取openId
     */
    public void code() {
        String s = HttpUtils.executeGet("https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=wxbddc65182f051200&" +
                "secret=e98ad5c2cf8366d11964acd79a9ce141&" +
                "code=CODE&" +
                "grant_type=authorization_code");

        /**
         * 响应
         *
         * { "access_token":"ACCESS_TOKEN",
         "expires_in":7200,
         "refresh_token":"REFRESH_TOKEN",
         "openid":"OPENID",
         "scope":"SCOPE" }
         */
        System.out.println(s);
    }


    /**
     * 获取access_token
     */
    @Test
    public void accessToken() {
        String s = HttpUtils.executeGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx0c1a1664441c4dd7&secret=41d507bda264f6c9da9a25eb8d84c0ce");
        System.out.println(s);
    }

    @Test
    public void userList() {
        String s = HttpUtils.executeGet("https://api.weixin.qq.com/cgi-bin/user/get?access_token=8_YC_g0ty8Y9Gt1THHJKLj6cmHbtBnvYn80xxYvb9Joc6k-Mwr9YQbsGll0BJHKl7wvTROAzjA-Yq4llSR3vEm6EBs3hWNSTjN1JkRFIjRAqDppJcft2VsJAfxF1YHtf4yBGppaPL6hH7P2TLVAYEjAAATRX&next_openid=");

        JSONObject jsonObject = JSON.parseObject(s);
        System.out.println(jsonObject.getString("data"));
    }

    /**
     * 查找指定用户昵称的 openid
     */
    @Test
    public void userInfo() throws Exception {
        String s = HttpUtils.executeGet("https://api.weixin.qq.com/cgi-bin/user/get?access_token=8_GsmZl3bZ6cKgihZu0IouXrteYctEPVbPIptfPCO50AVRCL2_L5esr7qOtRBGZZi6VNfHJ_0LU_EA5KgOMlPhYPWaki3In-huvGCGmCmJYT1EeocNnSLDz6CyRjgdWb3Fxmm4Uj6lj4Ny_QFWHYIcAIALCM&next_openid=");

        JSONObject jsonObject = JSON.parseObject(s);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("openid");
        for (Object o : jsonArray) {
            TimeUnit.MILLISECONDS.sleep(200);
            String userInfo = HttpUtils.executeGet("https://api.weixin.qq.com/cgi-bin/user/info?access_token=8_GsmZl3bZ6cKgihZu0IouXrteYctEPVbPIptfPCO50AVRCL2_L5esr7qOtRBGZZi6VNfHJ_0LU_EA5KgOMlPhYPWaki3In-huvGCGmCmJYT1EeocNnSLDz6CyRjgdWb3Fxmm4Uj6lj4Ny_QFWHYIcAIALCM&openid=" + o.toString());

            JSONObject userInfoJsonObject = JSON.parseObject(userInfo);
            String nickname = userInfoJsonObject.getString("nickname");
            nickname = new String(nickname.getBytes("ISO-8859-1"), "UTF-8");
            System.out.println(nickname);
            if ("叶飞".equals(nickname)) {

                System.out.println(userInfoJsonObject.toJSONString());
                System.out.println("--------------------------------");
                return;
            }
        }

    }
}
