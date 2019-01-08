package com.xianglin.act.common.service.integration.impl;

import com.xianglin.act.common.service.integration.MessageServiceClient;
import com.xianglin.appserv.common.service.facade.model.Request;
import com.xianglin.appserv.common.service.facade.model.vo.MsgVo;
import com.xianglin.xlStation.base.model.Response;
import com.xianglin.xlStation.base.model.SmsResponse;
import com.xianglin.xlStation.common.service.facade.MessageService;
import com.xianglin.xlStation.common.service.facade.userFacade.MessageFacade;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * The type Message service client.
 *
 * @author yefei
 * @date 2018 -01-22 16:37
 */
@Service
public class MessageServiceClientImpl implements MessageServiceClient {

    @Resource
    private MessageFacade messageFacade;

    @Resource(name = "appMessageService")
    private com.xianglin.appserv.common.service.facade.MessageService messageService;

    @Override
    public Response sendSmsCode(String mobilePhone, String smsContentTemplate, String smsValidTime) {
        return messageFacade.sendSmsCodeByTemplate(mobilePhone, "act", smsContentTemplate, smsValidTime);
    }

    @Override
    public SmsResponse checkSmsCode(String mobilePhone, String smsCode, boolean isDeleted) {
        return messageFacade.checkSmsCode(mobilePhone, "act", smsCode, isDeleted);
    }

    @Override
    public Response sendSmsByTemplate(String mobilePhone, String smsContentTemplate, String[] params) {
        return messageFacade.sendSmsByTemplate(mobilePhone, smsContentTemplate, params);
    }

    @Override
    public com.xianglin.appserv.common.service.facade.model.Response<Boolean> sendMsg(Request<MsgVo> request, List<Long> partyIds) {
        return messageService.sendMsg(request, partyIds);
    }
}
