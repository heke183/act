package com.xianglin.act.web.home.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.ExpiringSession;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class TokenProccessor {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TokenProccessor.class);

    /**
     * 生成Token
     * Token 20：7 uuid+System.currentTimeMillis
     *
     * @return
     */
    public static String makeToken() {  //checkException
        return UUID.randomUUID().toString().replace("-", "").substring(0, 7) + System.currentTimeMillis();
    }

    public static synchronized void createToken(ExpiringSession session) {

        if (session != null) {
            String token = TokenProccessor.makeToken();
            session.setAttribute(WebConstants.TOKEN, token);
        }
    }


    /**
     * 校验由服务器产生的Token
     * Token 20：7 uuid+System.currentTimeMillis
     *
     * @return
     */
    public static synchronized boolean isTokenValid(HttpServletRequest request, ExpiringSession session) {
        if (session == null) {
            return true;
        }
        // session中不含token,
        // 说明form被提交过后执行了resetToken()清除了token
        // 判为非法
        String stoken = (String) session.getAttribute(WebConstants.TOKEN);
        System.out.println("----------session stoken-------------------------:" + stoken);
        if (stoken == null) {
            return true;
        }


        // request请求参数中不含token,
        // 判为非法
        String rtoken = request.getParameter(WebConstants.TOKEN);

        System.out.println("----------get request stoken-------------------------:" + rtoken);
        if (rtoken == null) {
            return true;
        }
        // request请求中的token与session中保存的token不等,判为非法
        if (stoken.equals(rtoken)) {
            return false;
        }
        return false;
    }

    /*
     * 重新设置token，当页面被请求后，将session中的token属性去除
     */
    public static synchronized void resetToken(ExpiringSession session) {

        if (session != null) {
            session.removeAttribute(WebConstants.TOKEN);
        }
    }

    /*
     * 重新设置token，当页面被请求后，将session中的token属性去除
     */
    public static synchronized void resetAndStoreToken(ExpiringSession session, HttpServletRequest request) {

        if (session != null) {
            session.removeAttribute(WebConstants.TOKEN);
            String token = request.getParameter(WebConstants.TOKEN);
            session.setAttribute(WebConstants.TOKEN, token);
            logger.info("----------resetAndStoreToken stoken-------------------------:" + token);
        }
    }

    public static synchronized void storeClientToken(ExpiringSession session, HttpServletRequest request) {

        if (session != null) {
            String token = request.getParameter(WebConstants.TOKEN);
            session.setAttribute(WebConstants.TOKEN, token);
            logger.info("----------store ClientToken stoken-------------------------:" + token);
        }
    }

    /**
     * 校验由页面产生的Token
     *
     * @return
     */
    public static synchronized boolean isClientTokenValid(HttpServletRequest request, ExpiringSession session) {
        String rtoken = request.getParameter(WebConstants.TOKEN);
        logger.info("----------get request stoken-------------------------:" + rtoken);
        if (rtoken == null) {//页面必须要有token
            return true;
        }
        if (session == null) {//服务器session不能为空
            return true;
        }

        String stoken = (String) session.getAttribute(WebConstants.TOKEN);
        logger.info("----------get session stoken-------------------------:" + stoken);
        if (stoken == null) {//服务器没有token，则重新保存,是正常提交
            return false;
        }
        // request请求中的token与session中保存的token不等,判为非法
        if (stoken.equals(rtoken)) { //该token在服务器端已经做过提交
            logger.info("---------- session stoken equals result token-------------------------:" + stoken);
            return true;
        }
        return false;
    }

}
