package com.xianglin.act.biz.shared;

import com.xianglin.act.common.dal.model.ActGroupInfo;
import com.xianglin.act.common.dal.model.ActGroupUser;
import com.xianglin.core.model.vo.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Describe :
 * Created by xingyali on 2018/12/19 9:22.
 * Update reason :
 */
public interface ActGroupSharedService {
    /**
     * 活动详情页
     * @param currentPartyId
     * @return
     */
    ActGroupInfoVo queryGroupDetail(Long currentPartyId);

    /**
     * 加入团
     * @param partyId
     * @return
     */
    ActGroupInfoVo joinGroup(Long partyId,Long currentPartyId);

    /**
     * 根据用户的partyID可查询用户参与的团
     * @param currentPartyId
     * @return
     */
    List<ActGroupInfoVo> groupListByPartyId(Long currentPartyId);

    /**
     * 查询滚动100条消息
     * @return
     */
    List<ActGroupTipsVo> queryScrollMessage();

    /**
     * 个人团信息分享查询
     * @param partyId
     * @return
     */
    ActGroupInfoVo groupDetailShare(Long partyId);

    /**
     * 分享信息查询
     * @param currentPartyId
     * @return
     */
    ActGroupShareVo share(Long currentPartyId);

    /** 根据活动code查询奖品列表
     * @param activityCode 活动code
     * @return 奖品列表
     */
    List<PrizeVo> queryPrizeListByActivityCode(String activityCode);


    /** 兑换礼品
     * @param partyId 用户partyID
     * @param prizeCode 兑换礼品code
     * @return
     */
    String exchangePrize(Long partyId,String prizeCode,String activityCode);

    /** 提交地址信息
     * @param contactInfoVO 地址信息
     * @return
     */
    boolean commitAddress(ContactInfoVO contactInfoVO);

    /** 提现
     * @param partyId 用户partyID
     * @return
     */
    boolean withDraw(Long partyId);

    /** 根据类型查询兑换明细 明细包括(实物，优惠券，其中优惠券跳转指定电商链接)
     * @param partyId 用户partyId
     * @return
     */
    List<CustomerAcquireRecordVO> queryExchangeDetail(Long partyId,String activityCode,String type);

    /** 我的红包、红包明细
     *
     * @param partyId 用户partyId
     * @param type 消息列表类型
     * @param activityCode 活动code
     * @return
     */
    RedPackageVo queryRedPack(Long partyId,String type,String activityCode);


    /** 根据partyId和type查询提示信息
     *
     * @param partyId
     * @param type 类型
     * @return
     */
    List<ActGroupTipsVo> queryGroupTipsByPartyId(Long partyId,String type);

    /**
     * 修改团样式
     * @param id
     * @param style
     * @return
     */
    Boolean updateGroupStyle(Long partyId,Long id, String style);

    /**
     * 开团
     * @param currentPartyId
     * @return
     */
    ActGroupInfoVo createGroup(Long currentPartyId);

    /**微信小程序用户登陆
     * @param loginInfo
     *    mobilePhone,nickName,avatarUrl,openid,unionid
     * @return
     */
    Long wxAppletLogin(Map<String,String> loginInfo);


    /** 查询参与人数 和 兑换人数
     * @return
     */
    Map<String,String> queryPeopleNum();

    /** 查询当前余额和拆红包得到得余额
     * @param partyId
     * @return
     *  balance 当前余额
     *  dismantleBalance 拆红包得到的金额
     */
    Map<String,BigDecimal> queryDismantleBalance(Long partyId);

    /**
     * 查询活动规则
     * @return
     */
    Map<String,String> queryGroupRule();

    /**用户信息查询
     * @param partyId
     * @return
     */
    ActGroupUser queryGroupUser(Long partyId);

    /**拆红包
     * @param currentPartyId 当前用户
     * @param code 红包类型
     * @return 拆出来的红包金额
     */
    BigDecimal dismantlePacket(Long currentPartyId, String code);

    /**
     * 返回团主题样式
     * @param partyId
     * @return
     */
    String groupStyleByPartyId(Long partyId);
}
