package com.xianglin.act.common.service.facade;

import com.xianglin.act.common.service.facade.model.*;

import java.util.List;

/**
 * Describe :
 * Created by xingyali on 2018/11/19 11:01.
 * Update reason :
 */
public interface VoteService {

    /**
     * 查询活动列表
     * @return
     */
    Response<List<ActVoteDTO>> queryVoteActivityList();

    /**
     * 结束活动 清除活动记录 发布活动
     * @param activityCode
     * @return
     */
    Response<Boolean> updateActivity(String activityCode,String type);

    /**
     *核销管理列表 
     * @return
     */
    Response<PageResult<VoteAcquireRecordDTO>> queryAcquireRecordList(PageParam pageParam);

    /**
     * 修改物流单号
     * @param voteAcquireRecordDTO
     * @return
     */
    Response<Boolean> updateAcquireRecord(VoteAcquireRecordDTO voteAcquireRecordDTO);

    /**随机投票（内部使用，給固定账户）
     * @param activityCode
     * @return
     */
    Response<Boolean> randomVote(String activityCode);
}
