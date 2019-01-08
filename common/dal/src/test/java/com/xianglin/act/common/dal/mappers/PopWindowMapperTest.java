package com.xianglin.act.common.dal.mappers;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.common.dal.model.AttendanceCoinAward;
import com.xianglin.act.common.dal.model.PopWindow;
import com.xianglin.act.common.dal.model.redpacket.PartakerInfo;
import com.xianglin.act.common.dal.model.redpacket.RedPacketInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 11:52.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/*.xml")
public class PopWindowMapperTest {

    @Autowired
    PopWindowMapper popWindowMapper;

    @Autowired
    RedPacketMapper redPacketMapper;

    @Autowired
    RedPacketPartakerMapper redPacketPartakerMapper;

    @Autowired
    AttendanceCoinAwardMapper attendanceCoinAwardMapper;

    @Autowired
    ActivityPartakeMapper activityPartakeMapper;
    @Test
    public void run() {

        List<PopWindow> popWindows = popWindowMapper.selectAll();
        System.out.println(popWindows);
    }

    @Test
    public void run2() {

        RedPacketInfo redPacketInfo = redPacketMapper.selectUnExpireRedPacketOfDay(111L);
        System.out.println(redPacketInfo);
    }

    @Test
    public void run3() {

        PartakerInfo partakerInfo = redPacketPartakerMapper.selectLastUnexpireRedPacket(5199881);
        System.out.println(partakerInfo);
    }

    @Test
    public void name() {

        Map<String, Object> mostPersistStar = activityPartakeMapper.getMostPersistStar();
        System.out.println(mostPersistStar);
    }
}
