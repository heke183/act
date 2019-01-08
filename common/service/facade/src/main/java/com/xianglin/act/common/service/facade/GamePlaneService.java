package com.xianglin.act.common.service.facade;

import com.xianglin.act.common.service.facade.model.GamePlaneDTO;
import com.xianglin.act.common.service.facade.model.Response;

import java.util.List;
import java.util.Map;

public interface GamePlaneService {

    /**开始游戏
     * @param partyId
     * @return
     */
    Response<GamePlaneDTO> start(Long partyId);

    /**本周排行
     * @param partyId
     * @return
     */
    Response<List<GamePlaneDTO>> weekRanking(Long partyId);

    /**发放奖励
     * @param req
     * @return
     */
    Response<GamePlaneDTO> reward(GamePlaneDTO req);

    /**分享游戏
     * @param partyId
     * @return
     */
    Response<Object> share(Long partyId);

}
