/**
 *
 */
package com.xianglin.act.biz.service.implement;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.common.service.facade.model.Response;
import com.xianglin.act.common.util.SessionHelper;
import com.xianglin.cif.common.service.facade.constant.SessionConstants;
import com.xianglin.gateway.common.service.spi.ResponseProcessor;
import com.xianglin.gateway.common.service.spi.model.ServiceResponse;
import com.xianglin.gateway.common.service.spi.model.enums.ResultEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * appgw网关响应转换器
 *
 * @author pengpeng 2016年2月24日下午4:31:58
 */
public class AppgwResponseProcessor implements ResponseProcessor<String, Object> {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AppgwResponseProcessor.class);

    @Autowired
    private SessionHelper sessionHelper;

    /**
     * @see ResponseProcessor#process(String,
     * Object)
     */
    @Override
    public ServiceResponse<Object> process(String serviceId, Object response) {
        ServiceResponse<Object> result = null;
        if (response instanceof Response<?>) {
            Response<?> resp = (Response<?>) response;
            result = new ServiceResponse<Object>(resp.getCode(), resp.getMemo(), resp.getTips());
            String jsonResult = "";
            if (resp.getResult() instanceof String) {
                result.setResult(resp.getResult().toString());
            } else {
                result.setResult(JSON.toJSON(resp.getResult()));
            }
            String strResult = ToStringBuilder.reflectionToString(result, ToStringStyle.JSON_STYLE);
            Long partyId = sessionHelper.getSessionProp(SessionConstants.PARTY_ID, Long.class);
            logger.info("appserv {} resp partyId:{},result:{}", serviceId, partyId, StringUtils.substring(strResult, 0, 1000));
        } else {
            logger.error("illegal response! response:" + response);
            result = new ServiceResponse<Object>(ResultEnum.BizException);
        }
        // 清理ThreadLocal
        SessionHelper.clear();
        return result;
    }

}
