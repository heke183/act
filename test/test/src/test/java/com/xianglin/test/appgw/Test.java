/**
 *
 */
package com.xianglin.test.appgw;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pengpeng 2016年1月26日上午11:39:05
 */
public class Test {

    /**
     * 获取概述信息
     *
     * @return
     */
    public static List<NameValuePair> getOverviewInfo() {
        // 创建参数列表
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("operationType",
                "com.xianglin.act.common.service.facade.ActService.selectAct"));
        list.add(new BasicNameValuePair("requestData", "[]"));
        return list;
    }

    /**
     * 创建用户身份角色信息
     *
     * @return
     */
    public static List<NameValuePair> detail() {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("operationType", "com.xianglin.cif.common.service.facade.FigureService.detail"));
        list.add(new BasicNameValuePair("requestData", "[\"10002151\"]"));
        return list;
    }


    public static void main(String[] args) {


        CookieStore cookieStore = new BasicCookieStore();


        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore).build();

        try {
            //HttpPost post = new HttpPost("http://appgw.dev.xianglin.com/api.json");
            HttpPost post = new HttpPost("http://appgw.dev.xianglin.com/api.json");
            // post.setHeader("did", "ba4b1f3fad864e99850859ccbfc1dce8");
            // url格式编码
            // active
            // getUnusedFigureIds
            // regiser
            // login
            // autoLogin
            // logout
            // create
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(getOverviewInfo(), "UTF-8");
            post.setEntity(uefEntity);
            post.addHeader("did", "4b6032c5ac9b4355962d15d06e18e74d");
            System.out.println("POST 请求...." + post.getURI());
            // 执行请求
            BasicClientCookie cookie = new BasicClientCookie("XLSESSIONID","39be8c76-3af2-4b8f-93da-78acfa8fcc0e");
            cookie.setDomain("");   //设置范围
            cookie.setPath("/");
            cookieStore.addCookie(cookie);

            CloseableHttpResponse httpResponse = httpClient.execute(post);

            List<Cookie> cookies = cookieStore.getCookies();
            for (int i = 0; i < cookies.size(); i++) {
                System.out.println("Local cookie: " + cookies.get(i));
            }

            try {
                HttpEntity entity = httpResponse.getEntity();
                if (null != entity) {
                    System.out.println("-------------------------------------------------------");
                    System.out.println(EntityUtils.toString(entity));
                    System.out.println("-------------------------------------------------------");
                }
            } finally {
                httpResponse.close();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
