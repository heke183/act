package com.xianglin.act.biz.shared;

import com.xianglin.act.common.dal.model.ActGamePlane;

import java.util.List;

/**
 * 飞机大战服务类
 */
public interface GamePlaneSharedService {

    /** 开始游戏，查询并初始化一条游戏数据
     * @param partyId
     * @return
     */
    ActGamePlane start(Long partyId);

    /**查询周排行榜（从周一开始）
     * @return
     */
    List<ActGamePlane> queryWeekRanking();

    /**发放奖励
     * @param plane
     * @return
     */
    ActGamePlane reward(ActGamePlane plane);
}
