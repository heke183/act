package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.redpacket.Partaker;
import com.xianglin.act.common.dal.model.redpacket.RedPacket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author yefei
 * @date 2018-04-24 17:23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/common-dal.xml")
public class RedPacketPartakerMapperTest {

    @Autowired
    private RedPacketMapper redPacketMapper;

    @Test
    public void insertTest() {

        RedPacket redPacket = new RedPacket();
        redPacket.setPacketId("12312312312");
        redPacket.setPartyId(5188771L);
        redPacket.setPacketInfo("asdasdasd");
        redPacketMapper.createRedPacket(redPacket);
    }

}
