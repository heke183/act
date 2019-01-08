package com.xianglin.core.service.strategy;

import com.xianglin.act.common.dal.model.Prize;
import com.xianglin.core.model.Player;
import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;

/**
 * The interface ActivityDTO strategy.
 *
 * @author yefei
 * @date 2018 -01-18 16:09
 */
public interface ActivityStrategy {

    /**
     * Handle prize.
     *
     * @param player the player
     * @param prize  the prize
     * @return the prize
     */
    void handle(ActivityRequest<Player> player, ActivityResponse prize);
}
