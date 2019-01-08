package com.xianglin.act.common.util.rocketmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.nio.charset.StandardCharsets;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/16 15:49.
 */

public class RocketMqProducer implements MqProducer, DisposableBean, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RocketMqProducer.class);

    private DefaultMQProducer producer;

    private String producerGroupName;

    private String namesrvAddr;

    private int sendMsgTimeout;

    @Override
    public void afterPropertiesSet() throws Exception {

        producer = new DefaultMQProducer(producerGroupName);
        producer.setNamesrvAddr(namesrvAddr);
        producer.setSendMsgTimeout(sendMsgTimeout);
        producer.start();
        logger.info("===========rocketMq初始化成功~  producerGroupName：[[ {} ]]  namesrvAddr：[[ {} ]]===========", producerGroupName, namesrvAddr);
    }

    @Override
    public SendResult sendMessage(String topic, String tag, Object messageObj) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {

        String messageJsonStr = "";
        if (messageObj != null) {
            messageJsonStr = JSON.toJSONString(messageObj);
        }
        Message message = new Message();
        message.setTopic(topic);
        message.setTags(tag);
        message.setBody(messageJsonStr.getBytes(StandardCharsets.UTF_8));
        logger.debug("===========发送mq：[[ {} ]]===========", message);
        return producer.send(message);

    }

    @Override
    public void sendMessageWithResultCheck(String topic, String tag, Object messageObj) {

    }

    @Override
    public SendResult sendMessageAsync(String topic, String tag, Object messageObj, MessageQueueSelector callBack, Object callBackContext) {

        return null;
    }

    @Override
    public void sendMessageAsyncWithResultCheck(String topic, String tag, Object messageObj, MessageQueueSelector callBack, Object callBackContext) {

    }

    public String getProducerGroupName() {

        return producerGroupName;
    }

    public void setProducerGroupName(String producerGroupName) {

        this.producerGroupName = producerGroupName;
    }

    public String getNamesrvAddr() {

        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {

        this.namesrvAddr = namesrvAddr;
    }

    public int getSendMsgTimeout() {

        return sendMsgTimeout;
    }

    public void setSendMsgTimeout(int sendMsgTimeout) {

        this.sendMsgTimeout = sendMsgTimeout;
    }

    @Override
    public void destroy() throws Exception {

        producer.shutdown();
    }
}
