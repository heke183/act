package com.xianglin.act.biz.shared;

import com.xianglin.act.common.dal.model.*;
import com.xianglin.act.common.service.facade.model.ActVoteDTO;
import com.xianglin.act.common.service.facade.model.PageParam;
import com.xianglin.act.common.service.facade.model.PageResult;
import com.xianglin.act.common.service.facade.model.VoteAcquireRecordDTO;
import com.xianglin.core.model.enums.OrderTypeEnum;
import com.xianglin.core.model.vo.VoteActivityVo;
import com.xianglin.core.model.vo.VoteItemVO;
import com.xianglin.core.model.vo.VoteShareVO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yungyu
 */

public interface VoteActService {

    /**
     * 获取投票活动
     *
     * @param activityCode
     * @return
     */
    Activity getVoteActContext(String activityCode);

    /**
     * 参与活动
     *
     * @param description
     * @param imgUrl
     */
    void addVoteItem(String description, String imgUrl);

    /**
     * 参与人数总数
     *
     * @return
     */
    int countPartakePeople();

    /**
     * 活动是否已经结束（开始展示）
     *
     * @return
     */
    boolean isExpire();

    /**
     * 展示开始时间
     *
     * @return
     */
    LocalDateTime getEndTime();

    /**
     * 站内投票
     *
     * @return
     */
    CustomerAcquire voteItem(Long partyId, Long toPartyId);

    /**
     * 用户是否已经参与当前活动
     *
     * @param currentPartyId
     * @return
     */
    boolean hasPartakeInAct(Long currentPartyId);

    /**
     * 参赛排行
     *
     * @param orderType
     * @param lastItemId
     * @param pageSize
     * @param lastId
     * @return
     */
    List<VoteItemVO> queryItemList(OrderTypeEnum orderType, Integer lastItemId, Integer pageSize,Long lastId);

    /**
     * 人气排行
     *
     * @return
     */
    List<VoteItemVO> queryPopularityItemList();

    /**
     * 搜索
     *
     * @param serialNumber
     * @return
     */
    VoteItemVO searchVoteItem(String serialNumber);

    /**
     * 短信发送
     *
     * @param mobilePhone
     */
    void sendMessage(String mobilePhone);

    /**
     * 短信校验，投票
     *
     * @param mobilePhone
     * @param code
     */
    void checkMessage(String mobilePhone, String code, long toPartyId);

    /**
     * 我的参与详情
     *
     * @param id
     * @return
     */
    VoteItemVO getVoteItem(Long id);

    /**
     * 用户是否参与当前活动
     *
     * @param partyId
     * @return
     */
    ActVoteItem getVoteItemByPartyId(Long partyId);

    /**
     * 领取奖励
     *
     * @return
     */
    CustomerAcquire drawAward(long toPartyId);

    /**
     * 晒单
     */
    void shareAward(long toPartyId);

    /**
     * 结算发放奖励
     */
    void calThisActivityAward(String activityCode, String userType);

    /**
     * 我的页面
     *
     * @param partyId
     * @return
     */
    VoteItemVO myItem(Long partyId);

    /**
     * 提交联系人信息
     */
    void contactInfo(ContactInfo contactInfo);

    boolean checkPartakeInStatus();

    ActVoteDTO queryVoteActivityList();

    /**
     * 查询分享信息
     * @return
     */
    VoteShareVO itemShareInfo();

    /**
     * 查询参数配置
     * @param activityCode 活动code
     * @param keys 某个参数的Key
     * @return 以Map集合返回
     */
    Map<String,String> queryActivityConfigByCode(String activityCode,List<String> keys);

    /**
     * 结束活动
     * @param activityCode
     * @return
     */
    Boolean updateActivity(String activityCode,String type);


    /**
     * 核销管理列表
     * @param pageParam
     * @return
     */
    PageResult<VoteAcquireRecordDTO> queryAcquireRecordList(PageParam<VoteAcquireRecordDTO> pageParam);

    /**
     * 修改物流单号
     * @param voteAcquireRecordDTO
     * @return
     */
    Boolean updateAcquireRecord(VoteAcquireRecordDTO voteAcquireRecordDTO);


    /**
     * 根据code 和 key 查询 value
     * @param activityCode
     * @param key
     * @return
     */
    ActivityConfig queryActivityConfigByCode(String activityCode,String key);

    /**
     * 报名是否开始/结束
     * @param start
     * @param end
     * @return
     */
    Boolean isStopRegister(String start,String end);

    /**
     * 检查当前用户是否是预览用户
     * @param partyId
     */
    void checkPreviewUserByPartyId(Long partyId);

    /**随机增加票数
     * @param activityCode
     * @return
     */
    Boolean randomVote(String activityCode);

}
