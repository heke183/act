/**
 *
 */
package com.xianglin.act.web.home.util;

import com.xianglin.fala.session.RedisSessionRepository;
import com.xianglin.fala.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Session帮助类
 *
 * @author pengpeng 2016年2月25日下午2:38:52
 */
@Component
public class SessionHelper {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(SessionHelper.class);
    /**
     * sessionRepository
     */
    @Autowired
    @Qualifier("sessionRepository")
    private RedisSessionRepository sessionRepository;
    @Autowired
    private SessionCookieHelper sessionCookieHelper;

    public RedisSessionRepository getSessionRepository() {
        return sessionRepository;
    }

    public void setSessionRepository(RedisSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public SessionCookieHelper getSessionCookieHelper() {
        return sessionCookieHelper;
    }

    public void setSessionCookieHelper(SessionCookieHelper sessionCookieHelper) {
        this.sessionCookieHelper = sessionCookieHelper;
    }

    /**
     * 取得session
     *
     * @return
     */
    public Session getSession(HttpServletRequest request) {
        String sessionId = sessionCookieHelper.getSessionId(request);
        logger.debug("sessionId {}", sessionId);
        Session session = sessionRepository.getSession(sessionId);
        return session;
    }

}
