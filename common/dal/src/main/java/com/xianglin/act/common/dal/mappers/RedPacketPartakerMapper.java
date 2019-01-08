package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.annotation.PopTipMapper;
import com.xianglin.act.common.dal.annotation.PopTipSelector;
import com.xianglin.act.common.dal.model.redpacket.Partaker;
import com.xianglin.act.common.dal.model.redpacket.PartakerInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author yefei
 * @date 2018-04-02 17:43
 */
@PopTipMapper
public interface RedPacketPartakerMapper {

    /**
     * 插入
     *
     * @param partaker
     * @return
     */
    int insert(Partaker partaker);

    /**
     * @param partaker
     * @return
     */
    List<Partaker> selectRedPacketPartaker(Partaker partaker);

    /**
     * 查询红包的参与者以及获得奖励
     *
     * @return
     */
    List<PartakerInfo> selectRedPacketPartakerInfo(String packetId);

    /**
     * 查询参与者未过期的 最新开过的红包
     *
     * @param partyId
     * @return
     */
    @PopTipSelector(popTipType = 1,returnType = 1)
    PartakerInfo selectLastUnexpireRedPacket(@Param("partyId") long partyId);

    /**
     * 查询参与者开过的红包
     *
     * @return
     */
    PartakerInfo selectLastOpenedRedPacket(
            @Param("packetId") String packetId,
            @Param("partyId") long partyId);

    /**
     * 重复打开红包，更新打开的时间
     *
     * @param partyId
     * @param packetId
     * @return
     */
    int updateRedPacketOpenDate(
            @Param("partyId") long partyId,
            @Param("packetId") String packetId);

    /**
     * 根据 参与者查询wxOpenId 查询参与者是否参加过活动
     *
     * @param wxOpenId
     * @return
     */
    PartakerInfo selectRedPacketPartakerUnique(String wxOpenId);

    /**
     * 查询需要发弹匣
     *
     * @param startDate
     * @param endDate
     * @return
     */
    List<Partaker> selectPartakerForSms(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 更新已提醒
     *
     * @param partyId
     * @param packetInfo
     * @return
     */
    int updateRemind(
            @Param("partyId") long partyId,
            @Param("packetInfo") String packetInfo,
            @Param("memcCode") String memcCode
    );

}
