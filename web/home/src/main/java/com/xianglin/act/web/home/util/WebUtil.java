/**
 *
 */
package com.xianglin.act.web.home.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * web工具类
 *
 * @author pengpeng 2015年9月17日上午11:34:13
 */
public class WebUtil {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(WebUtils.class);

    /**
     * JSON_MIME_TYPE
     */
    private static final String MIME_TYPE_JSON = "application/json;charset=UTF-8";

    /**
     * HEADER_CLIENT_IP
     */
    private static final String HEADER_CLIENT_IP = "";

    private static final Map<String, String> MIME_TYPE_MAP = new HashMap<String, String>();

    static {
        MIME_TYPE_MAP.put("jpg", "image/jpeg");
        MIME_TYPE_MAP.put("jpeg", "image/jpeg");
        MIME_TYPE_MAP.put("gif", "image/gif");
        MIME_TYPE_MAP.put("png", "image/png");
    }

    /**
     * 根据后缀名取得对应的MimeType
     *
     * @param suffix
     * @return
     */
    public static String getMimeType(String suffix) {

        suffix = StringUtils.trimToEmpty(suffix).toLowerCase();
        return MIME_TYPE_MAP.get(suffix);
    }

    /**
     * 将json字符串写入http响应中
     *
     * @param response
     * @param message
     */
    public static void writeJsonToResponse(HttpServletResponse response, String message) {

        writeToResponse(response, message, MIME_TYPE_JSON);
    }

    /**
     * 将字符串写入http响应中
     *
     * @param response
     * @param message
     * @param mimeType
     */
    public static void writeToResponse(HttpServletResponse response, String message, String mimeType) {

        response.setContentType(mimeType);
//		 response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            logger.error("writeJsonToResponse error!", e);
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    /**
     * 将data写入http响应中
     *
     * @param response
     * @param data
     * @param mimeType
     */
    public static void writeToResponse(HttpServletResponse response, byte[] data, String mimeType) {

        response.setContentType(mimeType);
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            logger.error("writeJsonToResponse error!", e);
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * 从HttpServletRequest中取指定名称的header
     *
     * @param request
     * @param header
     * @param defaultValue
     * @return
     */
    public static String getHeader(HttpServletRequest request, String header, String defaultValue) {

        String result = StringUtils.trimToNull(request.getHeader(header));
        if (StringUtils.isEmpty(result)) {
            result = defaultValue;
        }
        return result;
    }

    /**
     * 从HttpServletRequest中取指定名称的header
     *
     * @param request
     * @param header
     * @return
     */
    public static String getHeader(HttpServletRequest request, String header) {

        return StringUtils.trimToNull(request.getHeader(header));
    }

    /**
     * 从HttpServletRequest中取指定名称的cookie
     *
     * @param request
     * @param cookieName
     * @param defaultValue
     * @return
     */
    public static String getCookie(HttpServletRequest request, String cookieName, String defaultValue) {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return defaultValue;
        }
        String result = null;
        for (Cookie cookie : cookies) {
            if (cookie != null && cookie.getName().equals(cookieName)) {
                result = StringUtils.trimToNull(cookie.getValue());
                break;
            }
        }
        if (StringUtils.isEmpty(result)) {
            result = defaultValue;
        }
        return result;
    }


    /**
     * 从HttpServletRequest中取指定名称的cookie
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static String[] getSessionIds(HttpServletRequest request, String cookieName) {

        List<String> sessionIds = new ArrayList<>();
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        String result = null;
        for (Cookie cookie : cookies) {
            if (cookie != null && cookie.getName().equals(cookieName)) {
                result = StringUtils.trimToNull(cookie.getValue());
                sessionIds.add(result);
            }
        }
        if (sessionIds.isEmpty()) {
            result = null;
        }
        String[] strings = new String[sessionIds.size()];
        return sessionIds.toArray(strings);
    }

    /**
     * 从HttpServletRequest中取指定名称的cookie
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static String getCookie(HttpServletRequest request, String cookieName) {

        return getCookie(request, cookieName, null);
    }

    /**
     * 从HttpServletRequest中取指定名称的参数
     *
     * @param request
     * @param param
     * @param defaultValue
     * @return
     */
    public static String getParam(HttpServletRequest request, String param, String defaultValue) {

        String result = StringUtils.trimToNull(request.getParameter(param));
        if (StringUtils.isEmpty(result)) {
            result = defaultValue;
        }
        return result;
    }

    /**
     * 从HttpServletRequest中取指定名称的参数
     *
     * @param request
     * @param param
     * @return
     */
    public static String getParam(HttpServletRequest request, String param) {

        return StringUtils.trimToNull(request.getParameter(param));
    }

    /**
     * 取得客户端ip地址
     *
     * @param request
     * @return
     */
    public static String getClientIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * @param request
     * @return
     */
    public static Map<String, Object> getRequestMap(HttpServletRequest request) {

        logger.info(" get request string {}", request.getQueryString());
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> returnMap = new HashMap<>();
        for (String key : (Set<String>) requestMap.keySet()) {
            String[] values = requestMap.get(key);
            returnMap.put(key, values[0]);
            logger.info(key + " = " + values[0]);
        }
        return returnMap;
    }

    /**
     * 获取当前springmvc 子容器
     * 只能用于由web容器发起的http线程
     * 对于自己发起的异步任务等自定义线程，无法使用该工具类
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {

        return WebApplicationContextUtils.findWebApplicationContext(getCurrentServletContext());
    }

    /**
     * 获取当前线程ServletContext
     * 只能用于由web容器发起的http线程
     * 对于自己发起的异步任务等自定义线程，无法使用该工具类
     *
     * @return
     */
    public static ServletContext getCurrentServletContext() {

        return getCurrentRequest().getServletContext();
    }

    /**
     * 获取当前线程request
     * 只能用于由web容器发起的http线程
     * 对于自己发起的异步任务等自定义线程，无法使用该工具类
     *
     * @return
     */
    public static HttpServletRequest getCurrentRequest() {

        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ra == null) {
            throw new IllegalStateException("当前线程中不存在 Request 上下文");
        }
        return ra.getRequest();
    }

    /**
     * 获取当前线程response
     * 只能用于由web容器发起的http线程
     * 对于自己发起的异步任务等自定义线程，无法使用该工具类
     *
     * @return
     */
    public static HttpServletResponse getCurrentResponse() {

        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ra == null) {
            throw new IllegalStateException("当前线程中不存在 Request 上下文");
        }
        return ra.getResponse();
    }

    /**
     * 将输入流写入response
     * 只能用于由web容器发起的http线程
     * 对于自己发起的异步任务等自定义线程，无法使用该工具类
     *
     * @param in
     * @throws IOException
     */
    public static void writeStreamToResponse(InputStream in) throws IOException {

        Preconditions.checkArgument(in != null);
        HttpServletResponse currentResponse = getCurrentResponse();
        currentResponse.setHeader(HttpHeaders.CONTENT_LENGTH, in.available() + "");
        //currentResponse.reset();
        ServletOutputStream out = currentResponse.getOutputStream();
        IOUtils.copy(in, out);
        out.flush();
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
    }

    /**
     * 将对象json 写入response
     * 只能用于由web容器发起的http线程
     * 对于自己发起的异步任务等自定义线程，无法使用该工具类
     *
     * @param object
     * @param features
     * @throws IOException
     */
    public static void writeJsonToResponse(Object object, SerializerFeature... features) throws IOException {

        byte[] bytes;
        if (features == null) {
            bytes = JSON.toJSONBytes(object);
        } else {
            bytes = JSON.toJSONBytes(object, features);
        }
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        HttpServletResponse currentResponse = getCurrentResponse();
        currentResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
        writeStreamToResponse(in);
    }

    /**
     * 将对象json 写入response
     * 只能用于由web容器发起的http线程
     * 对于自己发起的异步任务等自定义线程，无法使用该工具类
     *
     * @param object
     * @throws IOException
     */
    public static void writeJsonToResponse(Object object) throws IOException {

        writeJsonToResponse(object, null);

    }

    /**
     * 文件下载
     * tomcat webapp目录
     * 只能用于由web容器发起的http线程
     * 对于自己发起的异步任务等自定义线程，无法使用该工具类
     *
     * @param path
     * @param fileName
     * @throws IOException
     */
    public static void writeServletResourceToResponse(String path, String fileName) throws IOException {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(path));
        String realPath = getCurrentServletContext().getRealPath(path);
        FileSystemResource fileSystemResource = new FileSystemResource(realPath);
        if (Strings.isNullOrEmpty(fileName)) {
            fileName = fileSystemResource.getFilename();
        }
        writeResourceToResponse(fileSystemResource, fileName);
    }

    /**
     * 文件下载
     * spring 资源名 如 classpath:common-spring.xml
     * 只能用于由web容器发起的http线程
     * 对于自己发起的异步任务等自定义线程，无法使用该工具类
     *
     * @param path
     * @param fileName
     * @throws IOException
     */
    public static void writeResourceToResponse(String path, String fileName) throws IOException {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(path));
        Resource resource = getApplicationContext().getResource(path);
        if (Strings.isNullOrEmpty(fileName)) {
            fileName = resource.getFilename();
        }
        writeResourceToResponse(resource, fileName);
    }

    /**
     * 文件下载
     * 指定文件名
     * 只能用于由web容器发起的http线程
     * 对于自己发起的异步任务等自定义线程，无法使用该工具类
     *
     * @param resource
     * @param fileName
     * @throws IOException
     */
    public static void writeResourceToResponse(Resource resource, String fileName) throws IOException {

        InputStream in = resource.getInputStream();
        writeFileToResponse(in, fileName);
    }

    /**
     * 文件下载
     * 只能用于由web容器发起的http线程
     * 对于自己发起的异步任务等自定义线程，无法使用该工具类
     *
     * @param in
     * @param fileName
     * @throws IOException
     */
    public static void writeFileToResponse(InputStream in, String fileName) throws IOException {

        Preconditions.checkArgument(in != null);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(fileName));

        HttpServletResponse currentResponse = getCurrentResponse();
        currentResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + new String(fileName.getBytes(), Charsets.UTF_8));
        currentResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.OCTET_STREAM.toString());
        writeStreamToResponse(in);
    }

}
