/**
 *
 */
package com.xianglin.act.common.util;

import com.xianglin.fala.session.Session;

import java.util.HashMap;
import java.util.Map;


/**
 * @author zhangyong 2016年8月23日下午8:16:03
 */
public class GlobalRequestContext {

    /**
     * sessionId
     */
    public static final String SESSION_KEY = "session";

    private static final String PARTY_ID = "partyId";

    /**
     * ThreadLocal
     */
    private static ThreadLocal<Map<Object, Object>> context = new ThreadLocal<Map<Object, Object>>() {

        public Map<Object, Object> initialValue() {

            return new HashMap<Object, Object>();
        }
    };

    /**
     * 取得指定的key对应的上下文信息，支持泛型
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Object key) {

        return (T) context.get().get(key);
    }

    /**
     * 设置上下文信息
     *
     * @param key
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T put(Object key, Object value) {

        return (T) context.get().put(key, value);
    }

    /**
     * 取得session
     *
     * @return
     */
    public static Session getSession() {

        return get(SESSION_KEY);
    }

    /**
     * 设置sessionId
     *
     * @param session
     */
    public static void setSession(Session session) {

        put(SESSION_KEY, session);
    }

    public static void removeSession() {

        context.remove();
    }

    /**
     * 获取当前用户的partyId,未登录则返回null
     *
     * @return
     */
    public static Long currentPartyId() {

        Long temp;
        try {
            Session session = GlobalRequestContext.getSession();
            if (session == null) {
                return null;
            }
            temp = Long.parseLong(session.getAttribute(PARTY_ID));
        } catch (NumberFormatException e) {
            temp = null;
        }
        return temp;
    }
}
