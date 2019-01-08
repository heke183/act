package com.xianglin.act.web.home.exception.resolver;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.BizException;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.act.web.home.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理
 *
 * @author yefei
 * @date 2018-02-02 9:14
 */
@ControllerAdvice
public class GlobalHandlerExceptionResolver {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(GlobalHandlerExceptionResolver.class);

    @ExceptionHandler(BizException.class)
    public void resolveBizException(HttpServletResponse response, Exception ex) {
        Response<Object> result = new Response<>();
        ActPreconditions.ResponseEnum responseEnum = ((BizException) ex).getResponseEnum();
        if (responseEnum == null) {
            result.setResponseEnum(ActPreconditions.ResponseEnum.ERROR);
            result.setTips(ex.getMessage());
        } else {
            result.setResponseEnum(responseEnum);
        }
        if (responseEnum == ActPreconditions.ResponseEnum.ERROR) {
            logger.error(ex.getMessage(), ex);
        }
        result.setResult(((BizException) ex).getResult());
        if (logger.isDebugEnabled()) {
            logger.debug("resolveBizException:{}", JSON.toJSONString(result));
        }
        WebUtil.writeJsonToResponse(response, JSON.toJSONString(result));
    }

    @ExceptionHandler(Throwable.class)
    public void resolveException(HttpServletResponse response, Exception ex) {
        Response<?> result = new Response<>();
        // 前后端分离 暂时不考虑跳转到 error页面逻辑
        result.setResponseEnum(ActPreconditions.ResponseEnum.ERROR);
        logger.error(ex.getMessage(), ex);
        if (!response.isCommitted()) {
            WebUtil.writeJsonToResponse(response, JSON.toJSONString(result));
        }
    }
}
