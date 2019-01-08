package com.xianglin.act.biz.service.implement;

import com.xianglin.act.common.util.SessionHelper;
import com.xianglin.cif.common.service.facade.constant.SessionConstants;
import com.xianglin.gateway.common.service.spi.impl.JSONParamProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * app客户端输入参数日志打印
 *
 * @author
 * @create 2017-04-11 10:59
 **/
public class AppservParamProcessor extends JSONParamProcessor{

    @Autowired
    private SessionHelper sessionHelper;

    private static final Logger logger = LoggerFactory.getLogger(AppservParamProcessor.class);

    @Override
    public Object[] processParam (String serviceId, String requestData, Type[] types) {
        try {
            Thread.currentThread().setName(UUID.randomUUID().toString());
            Long partyId = sessionHelper.getSessionProp(SessionConstants.PARTY_ID, Long.class);
            logger.info("appserv {} req partyId:{},requestData:{}",serviceId,partyId,requestData);
        }catch (Exception e){
            logger.warn("para process error",e);
        }
        return super.processParam(serviceId, requestData, types);
    }
}
