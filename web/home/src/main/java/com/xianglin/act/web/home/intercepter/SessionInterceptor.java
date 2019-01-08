/**
 *
 */
package com.xianglin.act.web.home.intercepter;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.common.util.EnvironmentUtils;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.act.web.home.util.SessionCookieHelper;
import com.xianglin.core.service.ActivityContext;
import com.xianglin.fala.session.Session;
import com.xianglin.fala.session.SessionRepository;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

/**
 * session初始化拦截器
 *
 * @author pengpeng
 */
public class SessionInterceptor extends HandlerInterceptorAdapter {

    /**
     * 默认sessionId前缀
     */
    public static final String DEFAULT_SESSION_ID_PREFIX = "sessions";

    private static final String THIS_APP_ENV = "THIS_APP_ENV";

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(SessionInterceptor.class);

    /**
     * sessionRepository
     */
    private SessionRepository<Session> sessionRepository;

    /**
     * sessionCookieHelper
     */
    private SessionCookieHelper sessionCookieHelper;

    private String env;

    private String[] noNeedAuthUrls = {"/send/message", "/check/message", "/customer/acquire", "/act/api/activity/attendance/detail", "/act/api/weixin/applet/userInfo",
            "/act/api/weixin/applet/mobileInfo"};

    /**
     * sessionId前缀
     */
    private String sessionIdPrefix = DEFAULT_SESSION_ID_PREFIX;

    private int sessionMaxInactiveIntervalInSeconds;

    /**
     * 初始化session
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("---------SessionInterceptor preHandle URI {},,paras:{}", request.getRequestURI(), JSON.toJSON(request.getParameterMap()));
        Session session = getSessionFromCookie(request, response);
        if (!isNeedInterceptor(request)) {
            return true;
        }
        //忽略登录检查标记注解
        if (handler != null) {
            if (handler instanceof HandlerMethod) {
                IntercepterIngore intercepterIngore = ((HandlerMethod) handler).getMethodAnnotation(IntercepterIngore.class);
                if (intercepterIngore != null) {
                    //如果方法上有登录检查忽略注解标记，这忽略登录检查
                    return true;
                }
            }
        }
        //继续登录检查
        // 滿足需求寫死 业务代码判断seesion
        if (request.getRequestURI().contains("/selectSharer")) {
            return true;
        }
        if (session == null || StringUtils.isBlank(session.getAttribute("partyId"))) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }
        return true;
    }

    private Session getSessionFromCookie(HttpServletRequest request, HttpServletResponse response) {

        Session session = null;
        String[] sessionIds = ArrayUtils.add(sessionCookieHelper.getSessionIds(request), Optional.ofNullable(request.getHeader("sessionid")).orElse(""));
        if (sessionIds != null) {
            for (String sessionId : sessionIds) {
                session = getSession(request, sessionId);
                // 已登录
                if (session != null && StringUtils.isNotBlank(session.getAttribute("partyId"))) {
                    logger.info("SessionInterceptor preHandle sessionId--------:" + session.getId());
                    GlobalRequestContext.setSession(session);
                    sessionCookieHelper.setSessionCookie(response, session);
                    break;
                }
            }
        }
        return session;
    }

    private boolean isNeedInterceptor(HttpServletRequest request) {

        String requesturi = request.getRequestURI();

        if (requesturi.endsWith(".html")) {
            return false;//针对html页面
        }
        for (String uri : noNeedAuthUrls) {
            if (requesturi.contains(uri)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 保存session，设置sessionId cookie
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (!isNeedInterceptor(request)) {
            return;
        }
        if (EnvironmentUtils.isNotPrdEnv(env)) {
            // 添加跨域响应头，非生产环境为方便测试做的适配，生产环境不会进入该if语句
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        /*Session session = GlobalRequestContext.getSession();
        if (session.isExpired()) {
            sessionRepository.delete(session.getId());
        } else {
            session.setMaxInactiveIntervalInSeconds(sessionMaxInactiveIntervalInSeconds);
            sessionRepository.save(session);
        }
        sessionCookieHelper.setSessionCookie(response, session);
        logger.debug("SessionInterceptor postHandle sessionId--------:" + session.getId());*/
        logger.info("---------SessionInterceptor postHandle URI----:" + request.getRequestURI());

    }

    /**
     * 清理ThreadLocal
     * 本方法为业务请求责任链最外层
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清理ThreadLocal
        GlobalRequestContext.removeSession();
        ActivityContext.remove();
    }

    /**
     * 取得分布式session
     *
     * @param request
     * @param sessionId
     * @return
     */
    private Session getSession(HttpServletRequest request, String sessionId) {

        Session session = null;
        if (StringUtils.isEmpty(sessionId) || (session = sessionRepository.getSession(sessionId)) == null) {
            return null;
        }
        return session;
    }

    /**
     * @param sessionRepository the sessionRepository to set
     */
    public void setSessionRepository(SessionRepository<Session> sessionRepository) {

        this.sessionRepository = sessionRepository;
    }

    /**
     * @param sessionCookieHelper the sessionCookieHelper to set
     */
    public void setSessionCookieHelper(SessionCookieHelper sessionCookieHelper) {

        this.sessionCookieHelper = sessionCookieHelper;
    }

    /**
     * @param sessionMaxInactiveIntervalInSeconds the sessionMaxInactiveIntervalInSeconds to set
     */
    public void setSessionMaxInactiveIntervalInSeconds(int sessionMaxInactiveIntervalInSeconds) {

        this.sessionMaxInactiveIntervalInSeconds = sessionMaxInactiveIntervalInSeconds;
    }

    /**
     * @param env the env to set
     */
    public void setEnv(String env) {
        //添加到system中，便于其他业务使用
        System.setProperty(THIS_APP_ENV, env);
        this.env = env;
    }

    public String getSessionIdPrefix() {

        return sessionIdPrefix;
    }

    public void setSessionIdPrefix(String sessionIdPrefix) {

        this.sessionIdPrefix = sessionIdPrefix;
    }

    /**
     * 标记注解，忽略登录检查
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface IntercepterIngore {

    }

}
