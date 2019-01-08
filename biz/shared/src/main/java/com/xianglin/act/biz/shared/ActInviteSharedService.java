package com.xianglin.act.biz.shared;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.xianglin.act.common.dal.model.ActInvite;
import com.xianglin.act.common.dal.model.ActInvite;
import com.xianglin.act.common.dal.model.ActInviteDetail;
import com.xianglin.appserv.common.service.facade.model.vo.AppUserRelationVo;
import com.xianglin.core.model.vo.*;

import com.xianglin.act.common.service.facade.model.ActInviteDTO;
import com.xianglin.act.common.service.facade.model.PageParam;
import com.xianglin.act.common.service.facade.model.PageResult;

import java.util.List;
import java.util.Map;

/**
 * Describe :
 * Created by xingyali on 2018/8/23 14:22.
 * Update reason :
 */
public interface ActInviteSharedService {
    /**
     * top端查询地推用户
     * @param pageParam
     * @return
     */
    PageResult<ActInviteDTO> queryInviteListByParam(PageParam<ActInviteDTO> pageParam);

    /**
     * 更新信息
     * @param map
     * @return
     */
    Boolean updateInvite(ActInvite map);


    /**
     * 用户报名信息提交
     * @param actInviteVo 用户报名信息
     * @return 影响的行数
     */
    Long userApply(ActInviteVo actInviteVo,Long partyId);


    /**
     * 好友争霸活动主页信息
     * @param partyId 用户partyID
     * @return 用户主页信息
     */
    ActInviteHomePageVo homePageInfo(Long partyId);


    /**
     * 查询所有的已报名的名单
     * @return
     */
    List<ActInviteVo> selectActInvites();


    /**
     * 好友争霸分享信息
     * @return
     */
    ActShareVo actShareInfo();

    /**
     * 查询用户报名信息 (一期)
     * @return 用户报名详细信息
     */
    ActInviteVo selectByPartyId(Long partyId);


    /**
     * 更新用户报名信息
     * @param actInviteVo 用户报名信息
     * @return
     */
    Boolean updateActInviteById(ActInviteVo actInviteVo);

    /**
     * 查询用户报名信息 (一期)
     * @return 用户报名详细信息
     */
    ActInviteVo selectApplyInfo(Long partyId);

    /**
     * 排行榜
     * @return
     */
    List<ActInviteVo> queryActRankList();


    /**
     * 好友争霸活动主页（二期）
     * @return
     */
    ActInviteHomePageVo homePageInfoTwo(Long partyId);


    /**
     * 好友争霸分享信息，(二期)
     * @return
     */
    ActShareVo actShareInfoTwo();

    /**
     * 查询活动规则
     * @return
     */
    Map<String,Object> selectRule();

    /**
     * 用户注册
     * @param plantRegisterVo 用户注册信息
     * @return 是否注册成功
     */
    Boolean userRegister(PlantRegisterVo plantRegisterVo);

    /**
     * 推荐码以及个人信息
     * @param partyId
     * @return
     */
    ReferralVo referralCode(Long partyId);

    /**
     * 查询用户信息 (二期)
     * @param partyId 用户partyid
     * @return
     */
    ActInviteVo queryApplyInfoTwo(Long partyId);

    /**
     * 发送验证码同时判断用户是否注册APP true 已注册，false 未注册
     * @param phone
     * @return
     */
    Boolean sendMsg(String phone);

    /**
     * 查询所有的地推用户
     * @return
     */
    List<ActInvite> queryInviteList(ActInvite actInvite);

    /**
     * 查询推荐用户数
     * @param actInviteDetail
     * @return
     */
    int queryInviteDetailCount(ActInviteDetail actInviteDetail);

    /**
     * 查询推荐用户
     * @param actInviteDetail
     * @return
     */
    List<ActInviteDetail> queryInviteDetailList(ActInviteDetail actInviteDetail);

    /**
     * 同步地推用户数据
     * @return
     */
    Boolean syncInviteList();
}
