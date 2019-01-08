package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.redpacket.Sharer;
import com.xianglin.act.common.dal.model.redpacket.SharerInfo;

/**
 * @author yefei
 * @date 2018-04-02 17:08
 */
public interface SharerMapper {

    /**
     *
     * @param sharer
     * @return
     */
    int insertSharer(Sharer sharer);

    /**
     * 更新分享者微信相关信息
     *
     * @param sharer
     * @return
     */
    int updateSharer(Sharer sharer);

    /**
     * 根据partyId 查询
     *
     * @param partyId
     * @return
     */
    SharerInfo selectSharerByPartyId(long partyId);

    /**
     * 根据partyId 查询
     *
     * @param partyId
     * @return
     */
    SharerInfo selectSharerByOpenId(String partyId);

    /**
     * 根据设备号查询是否领取过红包
     *
     * @param deviceId
     * @return
     */
    int selectRedPacketInfo(String deviceId);

    /**
     * 根据参与者更新分享者 openId
     *
     * @return
     */
    int updateSharerOpenIdUnion();
}
