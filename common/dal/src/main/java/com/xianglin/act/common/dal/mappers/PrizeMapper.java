package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.Prize;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author yefei
 * @date 2018-01-22 9:25
 */
public interface PrizeMapper extends Mapper<Prize> {

    /**
     * 查询奖品信息
     *
     * @param activityCode
     * @param prizeCode
     * @return
     */
    Prize selectActivityPrize(@Param("activityCode") String activityCode, @Param("prizeCode") String prizeCode);
}
