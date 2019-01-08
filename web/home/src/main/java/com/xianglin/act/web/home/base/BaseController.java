package com.xianglin.act.web.home.base;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.act.web.home.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/17 17:10.
 */

public abstract class BaseController {

    private static ThreadLocal<Stopwatch> STOPWATCH_HOLDER = new ThreadLocal<>();

    public static Logger logger;

    public BaseController() {

        logger = LoggerFactory.getLogger(getClass());
    }

    /**
     * 自定义处理异常，用于子类重写
     * 返回空时进行默认的异常处理
     *
     * @return
     * @see BaseController#hanleDefaultException(java.lang.Exception, long, java.lang.String, java.util.Map)
     * 该方法未被重写是默认返回为空
     */
    protected Response handleCustomizeException(Exception e, long executeTime, String uri, Map<String, String[]> parameterMap) {

        return null;
    }

    /**
     * 异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> resolveException(Exception e) throws Exception {

        HttpServletRequest request = WebUtil.getCurrentRequest();
        Response Response;
        String uri = request.getRequestURI();
        Map<String, String[]> parameterMap = request.getParameterMap();

        long executeTime = getExecuteTime();
        //处理自定义异常
        Response customizeExceptionResult = handleCustomizeException(e, executeTime, uri, parameterMap);
        //自定义异常处理放回为null时处理走默认异常处理逻辑
        if (customizeExceptionResult != null) {
            return ResponseEntity.ok(customizeExceptionResult);
        }
        //处理默认异常
        Response = hanleDefaultException(e, executeTime, uri, parameterMap);
        //执行时间日志记录日志
        return ResponseEntity.ok(Response);
    }

    private Response hanleDefaultException(Exception e, long executeTime, String uri, Map<String, String[]> parameterMap) throws Exception {

        if (e instanceof IllegalArgumentException
                || e instanceof IllegalStateException) {
            logger.warn("===========[[ 异常:{} 请求uri -> {}, 执行时间 -> {}ms, 参数 -> {} ]]===========", getControllerDescription(), uri, executeTime, JSON.toJSONString(parameterMap, true), e.getMessage());
            return Response.ofFail(e.getMessage());
        } else {
            logger.warn("===========[[ 异常:{} 请求uri -> {}, 执行时间 -> {}ms, 参数 -> {} ]]===========", getControllerDescription(), uri, executeTime, JSON.toJSONString(parameterMap, true), e.getMessage());
            return Response.ofFail("异常：未知错误");
        }
    }

    protected void logExecuteTime() {

        if (!shouldLogSession()) {
            return;
        }
        long elapsed = getExecuteTime();
        HttpServletRequest request = WebUtil.getCurrentRequest();
        String uri = request.getRequestURI();
        Map<String, String[]> parameterMap = request.getParameterMap();

        logger.info("===========[[ 执行时间 -> {}ms,请求uri -> {},参数 -> {} ]]===========", elapsed, uri, JSON.toJSONString(parameterMap, true));
    }

    /**
     * 获取执行时间
     *
     * @return
     */
    private long getExecuteTime() {

        Stopwatch stopwatch = STOPWATCH_HOLDER.get();
        if (stopwatch == null) {
            return -1;
        }
        long elapsed = 0;
        if (stopwatch != null) {
            if (stopwatch.isRunning()) {
                stopwatch.stop();
                elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            }
        }
        return elapsed;
    }

    /**
     * 执行controller方法前初始化计时器
     *
     * @return
     */
    @ModelAttribute("initExecuteTime")
    public void initExecuteTime() {

        if (!shouldLogSession()) {
            return;
        }
        Stopwatch started = Stopwatch.createStarted();
        STOPWATCH_HOLDER.set(started);
    }

    protected abstract boolean shouldLogSession();


    /**
     * 获取Controller描述，在日志中使用
     * 子类应该重写该方法
     * 默认返回类名
     *
     * @return
     */
    protected abstract String getControllerDescription();
}
