package com.xianglin.act.common.util;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtils {

    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    public static String executeGet(String url) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(url);
            CloseableHttpResponse resp = httpclient.execute(httpget);
            return EntityUtils.toString(resp.getEntity());
        } catch (Exception e) {
            logger.error("Failed to connect to url ", e);
        }
        return "";
    }

    public static String executePost(String url, Map<String, ? extends Object> param) {
        String result = "";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (Entry<String, ? extends Object> entry : param.entrySet()) {
                if (entry.getValue() != null) {
                    params.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry
                            .getValue())));
                }
            }
            HttpEntity formEntity = new UrlEncodedFormEntity(params, "UTF-8");
            post.setEntity(formEntity);
            HttpResponse response = client.execute(post);
            InputStream is = response.getEntity().getContent();
            result = inStream2String(is);
        } catch (Exception e) {
            logger.error("Failed to connect to url ,{}", e, url);
        }
        return result;
    }

    public static String executePost(String url, Map<String, ? extends Object> param, SSLContext sslContext) {
        String result = "";
        try (CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }).setSslcontext(sslContext).build()) {
            HttpPost post = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (Entry<String, ? extends Object> entry : param.entrySet()) {
                if (entry.getValue() != null) {
                    params.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry
                            .getValue())));
                }
            }
            HttpEntity formEntity = new UrlEncodedFormEntity(params, "UTF-8");
            post.setEntity(formEntity);
            HttpResponse response = httpClient.execute(post);
            InputStream is = response.getEntity().getContent();
            result = inStream2String(is);
        } catch (Exception e) {
            logger.error("Failed to connect to url ,{}", e, url);
        }
        return result;
    }


    public static String executePostXml(String url, String param, SSLContext sslContext) {
        String result = "";
        try (CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }).setSslcontext(sslContext).build()) {
            HttpPost post = new HttpPost(url);
            post.addHeader("Content-Type","text/xml;charset=UTF-8");
            StringEntity stringEntity = new StringEntity(param,"UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            post.setEntity(stringEntity);
            HttpResponse response = httpClient.execute(post);
            InputStream is = response.getEntity().getContent();
            result = inStream2String(is);
        } catch (Exception e) {
            logger.error("Failed to connect to url ,{}", e, url);
        }
        return result;
    }

    // 将输入流转换成字符串
    private static String inStream2String(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;
        while ((len = is.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return new String(baos.toByteArray());
    }

    public static void main(String[] args) {
        System.out.println(executeGet("http://www.baidu.com"));
    }
}
