package com.xianglin.act.common.util;

import com.xianglin.fala.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: zhangyong
 * Date: 2016/10/31
 * Time: 16:05
 */
public class LoginAttrUtil {

    private static Logger logger = LoggerFactory.getLogger(LoginAttrUtil.class);

    private SessionHelper sessionHelper;

    public Long getPartyId() {

        //Session session = sessionHelperStatic.getSession();
        Session session = sessionHelper.getSession();


        String userId = session.getAttribute("partyId");

        if (userId != null) {
            return Long.valueOf(userId);
        }
        return null;
    }


    public SessionHelper getSessionHelper() {
        return sessionHelper;
    }

    public void setSessionHelper(SessionHelper sessionHelper) {
        this.sessionHelper = sessionHelper;
    }
}
