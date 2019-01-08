package com.xianglin.act.web.home.util;

import com.xianglin.fala.session.Session;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionCookieHelper {

    /**
     * sessionId cookie名称
     */
    private String sessionCookieName;

    /**
     * sessionCookieDomain
     */
    private String sessionCookieDomain;

    /**
     * sessionCookiePath
     */
    private String sessionCookiePath = "/";

    /**
     * sessionCookieHttpOnly
     */
    private boolean sessionCookieHttpOnly = true;

    /**
     * sessionCookieSecure
     */
    private boolean sessionCookieSecure = true;

    /**
     * sessionCookieMaxAge
     */
    private int sessionCookieMaxAge = -1;

    /**
     * 取得sessionId
     *
     * @param request
     * @return
     */
    public String getSessionId(HttpServletRequest request) {
        return WebUtil.getCookie(request, sessionCookieName, null);
    }


    /**
     * 取得sessionId
     *
     * @param request
     * @return
     */
    public String[] getSessionIds(HttpServletRequest request) {
        return WebUtil.getSessionIds(request, sessionCookieName);
    }

    /**
     * 设置sessionId cookie
     *
     * @param response
     * @param session
     */
    public void setSessionCookie(HttpServletResponse response, Session session) {
        String sessionId = session.getId();
        Cookie cookie = new Cookie(sessionCookieName, sessionId);
        if (StringUtils.isNotEmpty(sessionCookieDomain)) {
            cookie.setDomain(sessionCookieDomain);
        }
        /*if (StringUtils.isNotEmpty(sessionCookiePath)) {
			cookie.setPath(sessionCookiePath);
		} else {*/
        cookie.setPath("/");
        //}
        if (sessionCookieHttpOnly) {
            cookie.setHttpOnly(true);
        } else {
            cookie.setHttpOnly(false);
        }
        if (sessionCookieSecure) {
            cookie.setSecure(true);
        } else {
            cookie.setSecure(false);
        }

        if (session.isExpired()) {
            // 如果session已过期，则设置cookie过期时间为0，使浏览器中cookie立即失效
            cookie.setMaxAge(0);
        } else {
            // 重设cookie过期时间
            cookie.setMaxAge(sessionCookieMaxAge);
        }

        response.addCookie(cookie);
    }

    public String getSessionCookieName() {
        return sessionCookieName;
    }

    public void setSessionCookieName(String sessionCookieName) {
        this.sessionCookieName = sessionCookieName;
    }

    public String getSessionCookieDomain() {
        return sessionCookieDomain;
    }

    public void setSessionCookieDomain(String sessionCookieDomain) {
        this.sessionCookieDomain = sessionCookieDomain;
    }

    public String getSessionCookiePath() {
        return sessionCookiePath;
    }

    public void setSessionCookiePath(String sessionCookiePath) {
        this.sessionCookiePath = sessionCookiePath;
    }

    public boolean isSessionCookieHttpOnly() {
        return sessionCookieHttpOnly;
    }

    public void setSessionCookieHttpOnly(boolean sessionCookieHttpOnly) {
        this.sessionCookieHttpOnly = sessionCookieHttpOnly;
    }

    public boolean isSessionCookieSecure() {
        return sessionCookieSecure;
    }

    public void setSessionCookieSecure(boolean sessionCookieSecure) {
        this.sessionCookieSecure = sessionCookieSecure;
    }

    public int getSessionCookieMaxAge() {
        return sessionCookieMaxAge;
    }

    public void setSessionCookieMaxAge(int sessionCookieMaxAge) {
        this.sessionCookieMaxAge = sessionCookieMaxAge;
    }
}
