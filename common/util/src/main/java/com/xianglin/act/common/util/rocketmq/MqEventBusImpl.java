package com.xianglin.act.common.util.rocketmq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * mq消息分发
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/17 9:46.
 */
//@Component
public class MqEventBusImpl implements InitializingBean, MessageListenerConcurrently, MqEventBus, ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(MqEventBusImpl.class);

    private static final String ALL_TAGS = "*";

    private static final String TAG_DELIMITER = "||";

    private List<MqListenerContext> listeners = Lists.newArrayList();

    private DefaultMQPushConsumer consumer;

    private String namesrvAddr;

    private String consumerGroupName;

    private Map<String, List<MqListenerContext>> topicListener;

    @Override
    public void addMqListener(MqListenerContext listener) {

        listeners.add(listener);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //只有父容器刷新时
        if (event.getApplicationContext().getParent() == null) {

            topicListener = listeners.stream()
                    .collect(Collectors.groupingBy(o -> o.getTopic()));

            // 此处写活
            this.consumer = new DefaultMQPushConsumer("xl-act");
            consumer.setNamesrvAddr(namesrvAddr);
            InetAddress localHost;
            try {
                localHost = Inet4Address.getLocalHost();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            consumer.setInstanceName(localHost.getHostName() + "-" + localHost.getHostAddress());
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.setMessageModel(MessageModel.CLUSTERING);
            topicListener.forEach((topic, value) -> {
                try {
                    //是否监听该topic下所有tag
                    boolean allTags = value.contains(ALL_TAGS);
                    if (allTags) {
                        consumer.subscribe(topic, ALL_TAGS);
                        return;
                    }
                    String tagSetForTopic = value
                            .stream()
                            .map(MqListenerContext::getTag)
                            .distinct()
                            .collect(Collectors.joining(TAG_DELIMITER));
                    consumer.subscribe(topic, tagSetForTopic);
                } catch (MQClientException e) {
                    throw new RuntimeException(e);
                }
            });
            consumer.registerMessageListener(this);
            try {
                consumer.start();
            } catch (MQClientException e) {
                throw new RuntimeException(e);
            }
            logger.info("===========mq消费者初始化完毕，监听的topic和tag分别为[[ {} ]]===========", topicListener);
        }
    }

    @Override
    public void destroy() throws Exception {
        //关闭长连接
        consumer.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public String getNamesrvAddr() {

        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {

        this.namesrvAddr = namesrvAddr;
    }

    public String getConsumerGroupName() {

        return consumerGroupName;
    }

    public void setConsumerGroupName(String consumerGroupName) {

        this.consumerGroupName = consumerGroupName;
    }

    /**
     * 默认每次消费一条消息？
     *
     * @param msgs
     * @param context
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

        logger.info("===========收到mq消息：[[ {} ]]===========", msgs);
        try {
            //每条消息的消费状态
            List<ConsumeConcurrentlyStatus> consumeConcurrentlyStatuses = msgs.stream()
                    .map(messageExt -> {
                        String topic = messageExt.getTopic();
                        if (topic == null) {
                            //匿名消息默认消费成功？
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                        List<MqListenerContext> mqListenerContexts = topicListener.get(topic);
                        if (mqListenerContexts == null || mqListenerContexts.isEmpty()) {
                            logger.warn("===========mq推送过来但是没有找到监听方法：[[ {} ]]===========", messageExt);
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                        String messageExtTags = messageExt.getTags();
                        Stream<Boolean> invokeStatusStream = mqListenerContexts.stream()
                                .map(mqListenerContext -> {
                                    String listenerContextTag = mqListenerContext.getTag();
                                    if (ALL_TAGS.equals(listenerContextTag) || Objects.equals(listenerContextTag, messageExtTags)) {
                                        //执行监听方法
                                        return mqListenerContext.invoke(messageExt, context);
                                    }
                                    //同一主题下不同tag的消息默认消费成功
                                    return true;
                                });
                        if (invokeStatusStream.allMatch(Boolean::booleanValue)) {
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        } else {
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                    }).collect(Collectors.toList());
            if (msgs.size() == 1) {
                return consumeConcurrentlyStatuses.get(0);
            }
            // 一次发送多条消息，默认一条消费失败则都消费失败
            if (consumeConcurrentlyStatuses.contains(ConsumeConcurrentlyStatus.RECONSUME_LATER)) {
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.info("===========mq消费异常：[[ {} ]]===========", msgs, e);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}
