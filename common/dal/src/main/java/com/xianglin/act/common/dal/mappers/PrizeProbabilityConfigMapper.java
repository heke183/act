package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.PrizeProbabilityConfig;

/**
 * The interface Activity mapper.
 *
 * @author yefei
 * @date 2018 -01-22 9:24
 */
public interface PrizeProbabilityConfigMapper {

    /**
     * Select add probability prize probability config.
     *
     * @param customerType the customer type
     * @return the prize probability config
     */
    PrizeProbabilityConfig selectAddProbability(String customerType);

    /**
     * 重置用户大奖概率
     *
     * @param customerType the customer type
     * @return the int
     */
    int resetProbability(String customerType);

    /**
     * Add probability int.
     *
     * @return the int
     */
    int addProbability();
}
