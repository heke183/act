package com.xianglin.act.biz.shared;

import com.xianglin.act.common.dal.model.CustomerAcquire;
import com.xianglin.act.common.dal.model.redpacket.*;
import com.xianglin.core.model.CheckMessageVO;

/**
 * @author yefei
 * @date 2018-04-02 11:27
 */
public interface RedPacketActService {

    /**
     * 查询分享着信息比如是否绑定微信等, 未过期的团信息
     *
     * @param sharer
     * @return
     */
    SharerInfo selectSharerInfo(Sharer sharer);

    /**
     * 分享之前创建红包
     *
     * @param sharerInfo
     * @return
     */
    RedPacket create(SharerInfo sharerInfo);

    /**
     * 新人开红包
     *
     * @param partaker
     * @return
     */
    Partaker openRedPacket(Partaker partaker);

    /**
     * 分享者领取微信红包
     *
     * @param sharer
     */
    SharerInfo wxRedPacket(Sharer sharer);

    /**
     * 发送短信校验码
     *
     * @param partaker
     * @return
     */
    CheckMessageVO sendMessage(Partaker partaker);

    /**
     * 校验短信验证码
     *
     * @param checkMessageVO
     */
    User checkMessage(CheckMessageVO checkMessageVO);

    /**
     * 根据openId 查询是否是分享者, 返回分享者和分享者红包信息
     *
     * @param sharer
     * @return
     */
    User isSharer(Sharer sharer);

    /**
     * 查询二维码
     *
     * @param partyId
     * @return
     */
    SharerQrCode getSharerQrCode(long partyId);

    /**
     * 先决条件判断
     *
     * @param sharerInfo
     * @return
     */
    SharerInfo precondition(SharerInfo sharerInfo);

    /**
     *
     * @param partyId
     */
    void tipsRecord(long partyId);

}
