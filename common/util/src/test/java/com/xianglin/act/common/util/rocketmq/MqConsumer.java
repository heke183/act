package com.xianglin.act.common.util.rocketmq;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.xianglin.act.common.util.annotation.MqListener;
import org.springframework.stereotype.Component;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/17 15:08.
 */
@Component
public class MqConsumer {

    @MqListener(topic = "SONGACT_TEST", tag = "test")
    public boolean consumeMq(MessageExt msgs, ConsumeConcurrentlyContext context, String body) {

        System.out.println(body);

        System.out.println(msgs);
        return true;
    }
}
