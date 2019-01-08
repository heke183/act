package com.xianglin.core.service.impl;

import com.xianglin.act.common.dal.annotation.PopTipMapper;
import com.xianglin.act.common.dal.annotation.PopTipSelector;
import com.xianglin.act.common.dal.mappers.DynamicPopWindowMapper;
import com.xianglin.act.common.dal.model.PopWindow;
import com.xianglin.act.common.dal.model.redpacket.RedPacketInfo;
import com.xianglin.core.service.GenericPopWindowGenerator;
import org.apache.ibatis.annotations.Param;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/20 10:34.
 */
@Component
@PopTipMapper
public class GenericPopWindowGeneratorImpl implements GenericPopWindowGenerator {

    @Autowired
    DynamicPopWindowMapper dynamicPopWindowMapper;

    @Autowired
    private RedissonClient redissonClient;

    @PopTipSelector(popTipType = 4, returnType = 100000)
    public RedPacketInfo getPopWindow(@Param("partyId") Long partyId) {

        RSet<Object> set = redissonClient.getSet("ACT:SET:TIPS");
        if (set.contains(partyId)) {
            set.remove(partyId);
            RedPacketInfo redPacketInfo = new RedPacketInfo();
            return redPacketInfo;
        }
        return null;
    }

    /**
     * 注解中的popTipType，returnType为默认值
     *
     * @param partyId
     * @return
     */
    @Override
    @PopTipSelector(popTipType = 2, returnType = 435435)
    public List<PopWindow> generateDynamicPopWindow(@Param("partyId") Long partyId) {

        return dynamicPopWindowMapper.queryDynamicPopWindow(partyId);
    }
}
