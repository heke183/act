package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.annotation.PopTipSelector;
import com.xianglin.act.common.dal.model.redpacket.RedPacket;
import com.xianglin.act.common.dal.model.redpacket.RedPacketInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yefei
 * @date 2018-04-02 15:50
 */
public interface RedPacketMapper {

    /**
     * 发红包 生成团
     *
     * @param redPacket
     * @return
     */
    int createRedPacket(RedPacket redPacket);

    /**
     * 新用户参与活动, 如果满足条件更新团完成度
     *
     * @return
     */
    int updateRedPacketCompletion(String packetId);

    /**
     * 查询当天已经完成的红包
     *
     * @return
     */
    List<RedPacketInfo> selectCompleteRedPacketOfDay(long partyId);

    /**
     * 查询当天未过期的团，不包含已经完成的
     *
     * @return
     */
    @PopTipSelector(popTipType = 4, returnType = 2)
    RedPacketInfo selectUnExpireRedPacketOfDay(@Param("partyId") long partyId);

    /**
     * 查询分享者最新的红包
     *
     * @return
     */
    @PopTipSelector(popTipType = 4, returnType = 3)
    RedPacketInfo selectLastRedPacket(@Param("partyId") long partyId);

    /**
     * 更新红包奖励流水
     */
    int updateRedpacketMemcCode(
            @Param("memcCode") String memcCode,
            @Param("packetId") String packetId
    );

    /**
     * 查询已经完成的红包/未通过检查的
     *
     * @return
     */
    List<RedPacket> selectCompleteRedPacketUnchecked(long partyId);

    /**
     *
     * @param packetId
     * @return
     */
    RedPacket selectRedPacket(String packetId);


    /**
     * 查询当天未过期的所有得团
     *
     * @return
     */
    List<RedPacket> selectAllUnExpireRedPacketOfDay();
}
