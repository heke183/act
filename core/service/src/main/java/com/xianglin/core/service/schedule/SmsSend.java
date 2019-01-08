package com.xianglin.core.service.schedule;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.common.dal.mappers.RedPacketPartakerMapper;
import com.xianglin.act.common.dal.model.redpacket.Partaker;
import com.xianglin.act.common.service.integration.MessageServiceClient;

import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.BizException;
import com.xianglin.xlStation.base.enums.XLStationEnums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 发短信 给参与者
 * <p>
 * 时间：11点或19点发送短信
 * 例子：
 * (1) 6-13点间开红包的用户，19点发送短信。
 * (2) 14-隔天5点间开红包的用户，11点发。
 *
 * @author yefei
 * @date 2018-04-12 13:30
 */
public class SmsSend {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(SmsSend.class);

    private final static String MESSAGE_CONTENT = "恭喜你注册成功，你已获3-10元现金，还没来领取，马上打开乡邻APP下载领取！现金可提现，去看看http://t.cn/RQzWYbC 回复 TD退订";

    private final static String MASTER_PATH = "/sms_send_master_select";

    @Resource
    private RedPacketPartakerMapper redPacketPartakerMapper;

    @Resource
    private MessageServiceClient messageServiceClient;

    public SmsSend() {
    }

    /**
     * 11点或者17点执行任务
     */
    public void selectAndSend() {
        logger.info("------------> 分享者短信发送");
        final Date startDate;
        final Date endDate;
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 11) {
            // 14:00-隔天5:59间开红包的用户，11点发送短信
            Calendar start = Calendar.getInstance();
            start.clear(Calendar.MILLISECOND);
            start.add(Calendar.DAY_OF_YEAR, -1);
            start.set(Calendar.HOUR_OF_DAY, 14);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            startDate = start.getTime();

            Calendar end = Calendar.getInstance();
            end.clear(Calendar.MILLISECOND);
            end.set(Calendar.HOUR_OF_DAY, 5);
            end.set(Calendar.MINUTE, 59);
            end.set(Calendar.SECOND, 59);
            endDate = end.getTime();
        } else {
            // 6：00-13：59:59间开红包的用户，19点发送短信
            Calendar start = Calendar.getInstance();
            start.clear(Calendar.MILLISECOND);
            start.set(Calendar.HOUR_OF_DAY, 6);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            startDate = start.getTime();

            Calendar end = Calendar.getInstance();
            end.clear(Calendar.MILLISECOND);
            end.set(Calendar.HOUR_OF_DAY, 13);
            end.set(Calendar.MINUTE, 59);
            end.set(Calendar.SECOND, 59);
            endDate = end.getTime();
        }

        List<Partaker> partakers = redPacketPartakerMapper.selectPartakerForSms(startDate, endDate);

        for (Partaker partaker : partakers) {
            com.xianglin.xlStation.base.model.Response response =
                    messageServiceClient.sendSmsByTemplate(
                            partaker.getMobilePhone(),
                            MESSAGE_CONTENT,
                            new String[0]);

            if (!(XLStationEnums.ResultSuccess.getCode() == response.getBussinessCode())) {
                logger.error("验证码发送失败：{}", JSON.toJSONString(response));
                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
            }
        }
    }

}
