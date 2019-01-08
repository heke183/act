package com.xianglin.act.common.util.rocketmq;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/16 15:56.
 */
public interface MqProducer {

    /**
     * 同步发送消息
     *
     * @param topic
     * @param tag
     * @param messageObj
     */
    SendResult sendMessage(String topic, String tag, Object messageObj) throws InterruptedException, RemotingException, MQClientException, MQBrokerException;

    /**
     * 同步发送消息，并检查是否发送成功
     * 发送失败则throw 异常
     *
     * @param topic
     * @param tag
     * @param messageObj
     * @throws MqSendFailException 发送失败
     */
    void sendMessageWithResultCheck(String topic, String tag, Object messageObj);

    /**
     * 异步发送消息
     *
     * @param topic
     * @param tag
     * @param messageObj
     * @param callBack
     * @param callBackContext
     * @return
     */
    SendResult sendMessageAsync(String topic, String tag, Object messageObj, MessageQueueSelector callBack, Object callBackContext);

    /**
     * 异步发送消息，并检查是否发送成功
     * 发送失败则throw 异常
     *
     * @param topic
     * @param tag
     * @param messageObj
     * @throws MqSendFailException 发送失败
     */
    void sendMessageAsyncWithResultCheck(String topic, String tag, Object messageObj, MessageQueueSelector callBack, Object callBackContext);
}
