package com.xianglin.act.biz.shared;

import com.xianglin.act.common.dal.model.ActStepDetail;
import com.xianglin.act.common.service.facade.model.ActStepDetailDTO;
import com.xianglin.act.common.service.facade.model.ActStepDetailShareInfo;

import java.util.List;

public interface StepSharedService {

    /** 步数信息同步
     * @param details
     * @return
     */
    List<ActStepDetail> synchStepDetail(List<ActStepDetail> details,Long partyId);

    /**
     * 累计参与天数
     * @param partyId
     * @return
     */
    int queryPartakeDay(Long partyId, String type);

    /**
     * 累计兑换次数
     * @param partyId
     * @param status
     * @return
     */
    int queryConversions(Long partyId, String status);

    /**
     *累计金币数量 
     * @param partyId
     * @param status
     * @return
     */
    int queryGoldCoins(Long partyId, String status);

    /**
     * 查明细
     * @param build
     * @return
     */
    ActStepDetail queryActStepDetail(ActStepDetail build);

    /**
     * 查根据兑换状态和用户
     * @param day
     * @param status
     * @return
     */
    ActStepDetail queryLuckyUser(String day, String status);

    /**
     * 更新步步生金活动明细
     * @param actStepDetail
     * @return
     */
    Boolean updateActStepDetail(ActStepDetail actStepDetail);

    /**
     * 查询排行榜
     * @param day
     * @return
     */
    List<ActStepDetail> queryTopList(String day);

    /**
     * 奖励明细查询
     * @param lastId
     * @return
     */
    List<ActStepDetail> queryRewardList(Long partyId,Long lastId);

    /**
     * 查询分享明细
     * @return
     */
    List<ActStepDetailDTO> queryActStepDetailShare();

    /**
     * 查询分享文案内容
     * @return
     */
    ActStepDetailShareInfo queryContentShare(Long partyId);

    /**
     * 
     * @param partyId
     * @return
     */
    int rewardStepNumber(Long partyId,String type,String day);

    /**
     * 查询用户当天总兑换次数
     * @return
     */
    int queryConversionsByDate(Long partyId,String day);
}
