package com.xianglin.act.common.service.facade;

import com.xianglin.act.common.service.facade.model.*;

import java.util.List;

/**
 * 步步生金活动接口
 */
public interface StepService {

    /** 同步客户端数据
     * @param details
     * @return
     */
    Response<List<ActStepDetailDTO>> synchStepDetail(List<ActStepDetailDTO> details,String day);

    /**查询用户步步生金活动总量
     * @return
     */
    Response<ActStepTotal> queryStepTotail();

    /**领取活动奖励
     * @param type
     * @return
     */
    Response<Integer> reward(String type,String day);

    /** 排行榜查询
     * @return
     */
    Response<List<ActStepDetailDTO>> queryRanking();

    /** 奖励明细查询
     * @param lastId 最后一条数据id
     * @return
     */
    Response<List<ActStepDetailDTO>> queryRewardList(Long lastId);

    /**
     * 查询分享文案内容
     * @return
     */
    Response<ActStepDetailShareInfo> queryContentShare();

    /**
     * 查询分享明细
     * @return
     */
   /* Response<List<ActStepDetailDTO>> queryActStepDetailShare();*/

    
}
