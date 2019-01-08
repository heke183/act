package com.xianglin.act.web.home.config;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.biz.shared.VoteActService;
import com.xianglin.act.common.util.rocketmq.MqProducer;
import com.xianglin.act.web.home.intercepter.SessionInterceptor;
import com.xianglin.core.service.AttendanceUserAwardGenerateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.MessageFormat;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/7 20:30.
 */
@RestController
@RequestMapping("dev")
@Profile({"dev", "local"})
public class DevEndPointController {

    private static final Logger logger = LoggerFactory.getLogger(DevEndPointController.class);

    @Resource(name = "randomAwardService")
    private AttendanceUserAwardGenerateService attendanceUserAwardGenerateService;

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private VoteActService voteActService;

    @SessionInterceptor.IntercepterIngore
    @RequestMapping("triggerAttendanceAwardJob")
    public String triggerAttendanceAwardJob() {

        logger.info("===========开始进行打卡奖励结算===========");
        String message = "OK! 打卡奖励结算完成";
        try {
            attendanceUserAwardGenerateService.calculateUserAward();
            logger.info("===========打卡奖励结算完成===========");
        } catch (Exception e) {
            logger.info("===========打卡奖励结算失败===========", e);
            message = "FAIL! 打卡奖励结算失败";
        }
        return message;
    }

    @SessionInterceptor.IntercepterIngore
    @RequestMapping("sendMq")
    public String sendMq(@RequestParam("topic") String topic, @RequestParam("tag") String tag, @RequestParam("message") String message) {

        logger.info("===========开始进发送消息===========");
        String echoMessage = "OK! 发送消息完成";
        try {
            Object parse = JSON.parse(message);
            mqProducer.sendMessage(topic, tag, parse);
            logger.info("===========发送消息完成===========");
        } catch (Exception e) {
            logger.info("===========发送消息失败===========", e);
            echoMessage = "FAIL! 发送消息失败";
        }
        return echoMessage;
    }

    @SessionInterceptor.IntercepterIngore
    @RequestMapping("cal-vote-act")
    public String calVoteAct(@RequestParam("activityCode") String activityCode, @RequestParam("userType") String userType) {

        logger.info("===========开始结算活动奖励：activityCode -> {}，userType -> {}===========", activityCode, userType);
        voteActService.calThisActivityAward(activityCode, userType);
        logger.info("===========结算活动奖励完成：activityCode -> {}，userType -> {}===========", activityCode, userType);
        return MessageFormat.format("OK! activityCode -> {0}，userType -> {1} 结算完成完成", activityCode, userType);
    }
}
