package com.xianglin.act.biz.shared;

import com.xianglin.act.common.dal.model.*;
import com.xianglin.act.common.service.facade.model.ActPlantLvTranDTO;
import com.xianglin.act.common.service.facade.model.ActPlantLvTranPageDTO;
import com.xianglin.act.common.service.facade.model.PageParam;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import com.xianglin.core.model.vo.*;

import java.util.List;
import java.util.Map;

/**
 * @author ex-jiangyongtao
 * @date 2018/8/2  16:50
 */

public interface ActPlantSharedService {

    /**
     * 排行榜前10
     * @return
     */
    ActPlantRankingVo queryRankingList(Long partyId);

    /**
     * 查近三天的消息明细列表
     * @return
     */
    List<ActPlantMessageDetailVo> messageDetailsList(Long partyId);

    /**
     * 查用户的任务表
     * @return
     */
    List<ActPlantTaskVo> task(Long partyId);

    /**
     *兑换信息查询 
     * @param actPlantLvTranPageDTO
     * @return
     */
    List<ActPlantLvTranDTO> queryPlantExchange(ActPlantLvTranPageDTO actPlantLvTranPageDTO);

    /**
     * 更新兑换信息
     * @param actPlantLvTran
     * @return
     */
    Boolean updatePlantExchange(ActPlantLvTran actPlantLvTran);

    /**
     * 添加用户的明细记录 手机充值、购物
     * @param actPlantTaskDetail
     * @return
     */
    Boolean insertActPlantTaskDetail(ActPlantTaskDetail actPlantTaskDetail);

    /**
     * 查询活动的配置
     * @param actCode 活动code
     * @return 活动配置信息
     */
    String selectActConfigValue(String actCode);

    /**
     * 奖品列表
     * @return 奖品list
     */
    List<ActPlantPrize> queryPrizeList();

    /**
     * 判断用户是否已经参与了活动
     * @param partyId 用户id
     * @return
     */
    ActPlantVo isJoinAct(Long partyId);

    /**
     * 用户开始参与活动
     * @param partyId
     * @param openId
     * @return
     */
    ActPlant joinAct(Long partyId,String openId);

    /**
     * 查询用户头像和当前爱心值
     * @return
     */
    Map<String,Object> selectUserLvAndImg(Long partyId);

    /**
     * 查询可显示的能量(包括可收取，可显示)
     * @param partyId
     * @return
     */
    List<ActPlantLvVo> showLv(Long partyId);

    /**
     * 收取爱心值
     * @param actPlantLvVo 爱心值vo
     * @return 返回本次收取的爱心值
     */
    ActPlantLvObtainVo obtainLv(ActPlantLvVo actPlantLvVo,Long partyId);

    /**
     * 礼品兑换
     * @param partyId 用户id
     * @param actPlantPrizeCode 兑换礼品的code
     * @return 返回主键id
     */
    Long exchangePrize(Long partyId, String actPlantPrizeCode);

    /**
     * 用户实名认证查询
     * @param partyId 用户id
     * @return 是否实名认证
     */
    Boolean userCertification(Long partyId);

    /**
     * 提交地址信息
     * @return
     */
    Long addressCommit(ActPlantLvTranVo actPlantLvTranVo);

    /**
     * 根据手机号查用户
     * @param phone
     * @return
     */
    UserVo queryUserByPhone(String phone);

    /**
     * 新用户领取树苗的状态
     * @param openId
     * @return
     */
    Map<String,Object> queryNewUserReciveState(String openId,Long partyId);

    /**
     * 新用户注册，并领取树苗和爱心值奖励
     * @param  plantRegisterVo
     * @return
     */
    Long register(PlantRegisterVo plantRegisterVo);

    /**
     * 查询用户信息
     * @param partyId 用户id
     * @return 返回用户信息
     */
    UserVo selectUserInfo(Long partyId);

    /**查询当前用户活动
     * @param partyId
     * @return
     */
    ActPlant queryPlant(Long partyId);

    /**查询所有关注用户
     * @param partyId
     * @return
     */
    List<ActPlantFollow> queryFollows(Long partyId);

    /** 邀請好友加入
     * @param currentPartyId 当前登陆用户
     * @param followPartyId 光柱用户
     * @return
     */
    Boolean followInvite(Long currentPartyId,Long followPartyId);

    /**查询关注用户能量
     * @param currentPartyId 当前登陆用户
     * @param followPartyId 光柱用户
     * @return
     */
    List<ActPlantLvVo> queryPlantLvs(Long currentPartyId,Long followPartyId);

    /**
     * 查询单个爱心值信息
     * @param actPlantLvId 爱心值表主键
     * @return 爱心值信息
     */
    ActPlantLvVo selectActPlantLv(Long actPlantLvId);

    /**收取爱心值
     * @param currentPartyId
     * @param lvId
     * @return
     */
    Integer collectFollowLv(Long currentPartyId, Long lvId);

    /**
     *发送验证码 
     * @param phone
     * @return
     */
    Boolean sendMsg(String phone);
    /**
     * 判断当前处于哪一个活动时间之内，返回该活动的code
     * @return
     */
    String findActCode();

    /**
     * (每个接口必须先调用此方法)判断活动是否结束
     * @return
     */
    Boolean activityIsStop();


    /**
     * 查询用户弹窗信息
     * @param partyId
     * @return
     */
    List<ActPlantTipVo> findByPartyId(Long partyId);

    /**
     * 更新用户弹窗信息
     * @param partyId
     * @return
     */
    void updateByPartyId(Long partyId);

    /**
     * 查詢用戶的openId和partyID
     * @return
     */
    Map<String,Object> queryUserOpenId(String code,Long fromPartyId);

    /**
     * 根据参数查询兑换数
     * @param actPlantLvTranPageDTO
     * @return
     */
    int queryPlantExchangeCount(ActPlantLvTranPageDTO actPlantLvTranPageDTO);


    /**
     * 用户主页信息
     * @param partyId  当前登录用户的partyid
     * @return 返回用户主页信息
     */
    ActPlantHomePageVo userHomePageInfo(Long partyId);

    /**
     * 分享爱心
     * @param currentPartyId
     * @return
     */
    Boolean shareLv(Long currentPartyId);

    /**
     * est
     * @param phone
     * @return
     */
    Map<String,Boolean> isRegisterOrisReceiveTree(String phone);

    /**
     * 获取随机奖励
     * @param partyId
     * @return
     */
    Map<String,Object> getRandomPrize(Long partyId);

    /**查询二维码
     * @param partyId
     * @return
     */
    String queryQrCode(Long partyId);

    /**
     * 发放最终排名提示
     */
    void sendRankMsg();

    /**
     * 分页查询公告消息列表
     * @param pageParam
     * @param isApp 是否app端使用
     * @return
     */
    List<ActPlantNotice> queryActPlantNotices(PageParam pageParam, Boolean isApp);

    /**
     * 查询公告条数
     * @return
     */
    Integer queryActPlantNoticesCount();

    /**
     * 新增公告消息
     * @param actPlantNotice
     * @return
     */
    Long inserActPlantNotice(ActPlantNotice actPlantNotice);

    /**
     * 编辑公告消息
     * @param actPlantNotice
     * @return
     */
    Boolean updateActPlantNotice(ActPlantNotice actPlantNotice);


    /**
     * 爱心兑换————审核
     * @param id
     * @return
     */
    Boolean updateExchangeStatus(Long id,String status);
}
