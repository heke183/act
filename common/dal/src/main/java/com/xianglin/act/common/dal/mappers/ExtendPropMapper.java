package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.ExtendProp;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface ExtendPropMapper extends Mapper<ExtendProp> {

    /**
     * 打卡断了之后，重置连续打卡天数
     *
     * @param partyId
     * @return
     */
    int resetSignInTimes(@Param("partyId") Long partyId);

    /**
     * 连续打卡天数自增
     *
     * @param partyId
     * @return
     */
    int increaseSignInTimes(@Param("partyId") Long partyId);
}