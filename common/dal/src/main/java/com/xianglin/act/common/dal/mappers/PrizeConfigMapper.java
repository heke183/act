package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.PrizeConfig;

import java.util.List;
import java.util.Set;

/**
 * The interface Prize config mapper.
 *
 * @author yefei
 * @date 2018 -01-24 9:48
 */
public interface PrizeConfigMapper {

    /**
     * Select grand pariz prize config.
     *
     * @param customerType the customer type
     * @return the prize config
     */
    List<PrizeConfig> selectGrandPrize(String customerType);

    /**
     * 抽中后删除大奖配置
     *
     * @param id the id
     * @return the int
     */
    int deleteGrandPrizeConfig(long id);
}
