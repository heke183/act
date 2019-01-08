package com.xianglin.act.biz.service.implement;

import com.xianglin.act.biz.shared.VoteActService;
import com.xianglin.act.common.service.facade.VoteService;
import com.xianglin.act.common.service.facade.model.*;
import com.xianglin.act.common.util.DTOUtils;
import com.xianglin.core.model.vo.VoteActivityVo;
import com.xianglin.gateway.common.service.spi.annotation.ServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe :
 * Created by xingyali on 2018/11/19 11:01.
 * Update reason :
 */
@com.alibaba.dubbo.config.annotation.Service
@org.springframework.stereotype.Service
@ServiceInterface(VoteService.class)
public class VoteServiceImpl implements VoteService {

    private static final Logger logger = LoggerFactory.getLogger(ActServiceImpl.class);
    
    @Autowired
    private VoteActService voteActService;
    

    /**
     *查询活动列表 
     * @return
     */
    @Override
    public Response<List<ActVoteDTO>> queryVoteActivityList() {
        List<ActVoteDTO> list = new ArrayList<>();
        ActVoteDTO actVoteDTO = voteActService.queryVoteActivityList();
        list.add(actVoteDTO);
        return  Response.ofSuccess(list);
    }

    /**
     *修改活动
     * @return
     */
    @Override
    public Response<Boolean> updateActivity(String activityCode,String type) {
        return Response.ofSuccess(voteActService.updateActivity(activityCode,type));
    }

    /**
     *核销管理列表
     * @return
     */
    @Override
    public Response<PageResult<VoteAcquireRecordDTO>> queryAcquireRecordList(PageParam pageParam) {
        return Response.ofSuccess(voteActService.queryAcquireRecordList(pageParam));
    }

    /**
     *修改物流单号
     * @return
     */
    @Override
    public Response<Boolean> updateAcquireRecord(VoteAcquireRecordDTO voteAcquireRecordDTO) {
        return Response.ofSuccess(voteActService.updateAcquireRecord(voteAcquireRecordDTO));
    }

    @Override
    public Response<Boolean> randomVote(String activityCode) {
        return Response.ofSuccess(voteActService.randomVote(activityCode));
    }

}
