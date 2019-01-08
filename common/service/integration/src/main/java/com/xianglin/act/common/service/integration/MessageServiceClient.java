package com.xianglin.act.common.service.integration;

import com.xianglin.appserv.common.service.facade.model.Request;
import com.xianglin.appserv.common.service.facade.model.vo.MsgVo;
import com.xianglin.xlStation.base.model.Response;
import com.xianglin.xlStation.base.model.SmsResponse;

import java.util.List;

/**
 * The interface Message service client.
 *
 * @author yefei
 * @date 2018 -01-22 16:33
 */
public interface MessageServiceClient {

    /***
     *
     * 发送验证码到指定手机号，包含有效时间
     *
     * @param mobilePhone the mobile phone
     * @param smsContentTemplate the sms content template
     * @param smsValidTime the sms valid time
     * @return response
     */
    Response sendSmsCode(String mobilePhone, String smsContentTemplate, String smsValidTime);

    /***
     *
     * 查询短信验证码信息
     *
     * @param mobilePhone the mobile phone
     * @param smsCode the sms code
     * @param isDeleted 验证码验证通过是否删除redis中的验证码
     * @return sms response
     */
    SmsResponse checkSmsCode(String mobilePhone, String smsCode, boolean isDeleted);

    /***
     *
     * 发送自定义模板短信到指定手机号
     *
     * @param mobilePhone 手机号码
     * @param smsContentTemplate 自定义模板字符串（占位符为 #{XXX}）
     * @param params 模板占位符参数（从左向右）
     * @return response
     */
    Response sendSmsByTemplate(String mobilePhone, String smsContentTemplate, String[] params);

    /**
     * 批量发送消息
     *
     * @param request the request
     * @param partyId the party id
     * @return com . xianglin . appserv . common . service . facade . model . response
     */
    com.xianglin.appserv.common.service.facade.model.Response<Boolean> sendMsg(Request<MsgVo> request, List<Long> partyId);
}
