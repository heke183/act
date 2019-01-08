package com.xianglin.act.web.home.intercepter;

import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.web.home.controller.ActPlantController;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.act.web.home.util.WebUtil;
import com.xianglin.act.common.util.BizException;
import com.xianglin.fala.session.Session;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检查重复提交，在登录检查后执行
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/31 19:09.
 */

public class RepeatSubmitCheckInterceptor extends HandlerInterceptorAdapter {

    private static final String PARTY_ID = "partyId";

    private RedissonClient redissonClient;

    private static final String LOCK_PREFFIX = "ACT:LOCK:";

    private static ThreadLocal<RLock> THREAD_LOCK = ThreadLocal.withInitial(() -> null);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //忽略登录检查标记注解
        if (handler != null) {
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                RepeatSubmitCheck repeatSubmitCheck = handlerMethod.getMethodAnnotation(RepeatSubmitCheck.class);
                if (repeatSubmitCheck != null) {
                    String lockApi = request.getRequestURI();
                    try {
                        String paramName = repeatSubmitCheck.paramName();
                        String lockParameter = "";
                        if (StringUtils.isNotBlank(paramName)) {
                            lockParameter = request.getParameter(paramName);
                            logger.info("start to lock,lock paramName:{}",lockParameter);
                            if (StringUtils.isBlank(lockParameter)) {
                                throw new RuntimeException("重复提交锁参数为空：" + paramName);
                            }
                        }
                        doLock(lockApi, lockParameter);
                    } catch (Exception e) {
                        //返回响应
                        if (e instanceof BizException) {
                            if (((BizException) e).getResponseEnum() != null) {
                                Response<Object> result = new Response<>();
                                result.setResponseEnum(((BizException) e).getResponseEnum());
                                WebUtil.writeJsonToResponse(result);
                            }
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 加锁
     *
     * @param lockMethod
     * @param lockParameter
     */
    private void doLock(String lockMethod, String lockParameter) {

        if (StringUtils.isNotBlank(lockParameter)) {
            lockParameter = ":" + lockParameter;
        }
        RLock lock = redissonClient.getLock(LOCK_PREFFIX + lockMethod + ":" + getCurrentPartyId() + lockParameter);
        if (!lock.tryLock()) {
            throw new BizException(ActPreconditions.ResponseEnum.REPEAT);
        }
        THREAD_LOCK.set(lock);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //忽略登录检查标记注解
        if (handler != null) {
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                RepeatSubmitCheck repeatSubmitCheck = handlerMethod.getMethodAnnotation(RepeatSubmitCheck.class);
                if (repeatSubmitCheck != null) {
                    doUnlock();
                }
            }
        }
    }

    /**
     * 解锁
     */
    private void doUnlock() {

        THREAD_LOCK.get().unlock();
        THREAD_LOCK.remove();
    }

    /**
     * 是否需要加锁
     *
     * @param handler
     * @return
     */
    private boolean shouldCheck(Object handler) {
        //忽略登录检查标记注解
        if (handler != null) {
            if (handler instanceof HandlerMethod) {
                RepeatSubmitCheck repeatSubmitCheck = ((HandlerMethod) handler).getMethodAnnotation(RepeatSubmitCheck.class);
                return repeatSubmitCheck != null;
            }
        }
        return false;
    }

    /**
     * 获取当前partyId
     *
     * @return
     */
    private Long getCurrentPartyId() {
        //之前的拦截器已经得到了session
        Session session = GlobalRequestContext.getSession();
        return session.getAttribute(PARTY_ID, Long.class);
    }


    public RedissonClient getRedissonClient() {

        return redissonClient;
    }

    public void setRedissonClient(RedissonClient redissonClient) {

        this.redissonClient = redissonClient;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface RepeatSubmitCheck {

        String paramName() default "";
    }
}
