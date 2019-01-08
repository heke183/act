package com.xianglin.act.common.util.rocketmq;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/17 15:04.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/*.xml")
public class MqProducerTest {

    @Autowired
    private MqProducer mqProducer;

    @Test

    public void testMq() throws InterruptedException, RemotingException, MQClientException, MQBrokerException, IOException {

        mqProducer.sendMessage("CIF_TOPIC", "BINDING_ROLE", "{[{partyId:12324}]}");
        System.in.read();
    }
}