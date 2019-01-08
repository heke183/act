package com.xianglin.act.common.service.integration.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.xianglin.act.common.service.integration.AppgwService;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.gateway.common.service.spi.JSONGatewayService;
import com.xianglin.gateway.common.service.spi.model.ServiceRequest;
import com.xianglin.gateway.common.service.spi.model.ServiceResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by wanglei on 2017/11/21.
 */
@Service("appgwService")
public class AppgwServiceImpl implements AppgwService {

    private static final Logger logger = LoggerFactory.getLogger(AppgwServiceImpl.class);

    @Resource
    private JSONGatewayService appservService;


    /**
     * 调用系统服务通用方法（针对appserv项目）
     *
     * @param interfaceName 接口名
     * @param method        方法名
     * @param result        返回结果 类型
     * @param paras         变长参数
     * @return
     */
    @Override
    public <T> T service(Class interfaceName, String method, Class<T> result, Object... paras) {
        logger.info("appser service interfaceName:{},method:{},paras:{}", interfaceName.getName(), method, ArrayUtils.toString(paras, null));
        try {
            ServiceRequest<String> req = new ServiceRequest();
            req.setServiceId(getMethod(interfaceName, method));
            JSONArray array = new JSONArray();
            if (paras.length > 0) {
                for (Object p : paras) {
                    array.add(p);
                }
            }
            req.setRequestData(array.toJSONString());
            req.setSessionId(GlobalRequestContext.getSession().getId());
            ServiceResponse<String> resp = appservService.service(req);
            logger.info("appser service interfaceName:{},method:{},result:{}", interfaceName.getName(), method, resp);
            if (resp.isSuccess()) {
                Field resultFiels = resp.getClass().getDeclaredField("result");
                resultFiels.setAccessible(true);
                Object o = resultFiels.get(resp);
                if (o instanceof JSONObject) {
                    return JSON.parseObject(o.toString(), result);
                } else {
                    return (T) resp.getResult();
                }
            }else{
                return (T) resp.getResult();  
            }
        } catch (Exception e) {
            logger.warn("appser service", e);
        }
        return null;
    }

    @Override
    public <T> List<T> serviceList(Class interfaceName, String method, Class<T> geneType, Object... paras) {
        logger.info("appser service interfaceName:{},method:{},paras:{}", interfaceName.getName(), method, ArrayUtils.toString(paras, null));
        try {
            ServiceRequest<String> req = new ServiceRequest();
            req.setServiceId(getMethod(interfaceName, method));
            JSONArray array = new JSONArray();
            if (paras.length > 0) {
                for (Object p : paras) {
                    array.add(p);
                }
            }
            req.setRequestData(array.toJSONString());
            req.setSessionId(GlobalRequestContext.getSession().getId());
            ServiceResponse<String> resp = appservService.service(req);
            logger.info("appser service interfaceName:{},method:{},result:{}", interfaceName.getName(), method, resp);
            if (resp.isSuccess()) {
                Field resultFiels = resp.getClass().getDeclaredField("result");
                resultFiels.setAccessible(true);
                Object o = resultFiels.get(resp);
                Type resultType = new TypeReference<List<T>>() {
                }.getType();
                return JSON.parseObject(o.toString(), resultType);
            }
        } catch (Exception e) {
            logger.warn("appser service", e);
        }
        return null;
    }

    /**
     * 区方法全名
     *
     * @param c
     * @param methodName
     * @return
     */
    private String getMethod(Class c, String methodName) {
        String mName = "";
        Method[] methods = c.getMethods();
        for (Method m : methods) {
            if (StringUtils.equals(methodName, m.getName())) {
                mName = c.getName() + "." + m.getName() + "." + getMethodSign(m.getParameterTypes());
                break;
            }
        }
        return mName;
    }

    /**
     * 取方法参数签名
     *
     * @param paramTypes
     * @return
     */
    private String getMethodSign(Type[] paramTypes) {
        StringBuilder builder = new StringBuilder("");
        if (ArrayUtils.isNotEmpty(paramTypes)) {
            for (Type type : paramTypes) {
                builder.append(type.toString());
            }
        }
        String base64 = DigestUtils.md5Hex(builder.toString());
        return StringUtils.substring(base64, 0, 8);
    }

    public static void main(String[] args) {

    }
}
