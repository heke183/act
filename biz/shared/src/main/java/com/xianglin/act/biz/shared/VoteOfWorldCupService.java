package com.xianglin.act.biz.shared;

import com.xianglin.act.common.dal.model.VoteActivity;
import com.xianglin.core.model.vo.VoteActivityBaseInfoVO;
import com.xianglin.core.model.vo.VoteRecord;
import com.xianglin.core.model.vo.VoterVO;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author yefei
 * @date 2018-06-13 14:00
 */
public interface VoteOfWorldCupService {

    /**
     * 查询活动基本信息
     *
     * @param activityCode
     * @return
     */
    VoteActivity getVoteActivity(String activityCode);

    /**
     * 投票
     *
     * @return
     */
    VoterVO vote(long partyId);

    /**
     * 投票提交
     * @param partyId
     * @param amount
     */
    void voteSubmit(long partyId, BigDecimal amount);

    /**
     * 首页信息
     *
     * @return
     */
    VoteActivityBaseInfoVO index();

    /**
     * 淘汰队伍
     *
     * @param activityCode
     * @param id
     */
    void knockOut(String activityCode, Long id);

    /**
     * 更新投票记录状态，更新后可以继续投票
     *
     * @param activityCode
     */
    void updateVoteRecordStatus(String activityCode, String date);

    /**
     * 结算
     *
     * @param activityCode
     */
    void deal(String activityCode);

    /**
     * 投票记录
     *
     * @return
     */
    List<VoteRecord> voteRecord();
}
