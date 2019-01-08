package com.xianglin.act.common.dal.mappers;
import java.util.Date;
import org.apache.ibatis.annotations.Param;
import java.util.List;


import com.xianglin.act.common.dal.model.ActPlantLv;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author ex-jiangyongtao
 */
public interface ActPlantLvMapper extends Mapper<ActPlantLv> {

    /**
     * 查询用爱心值，(展示，收取)
     * @return
     */
    List<ActPlantLv> findByShouTimeAndMatureTimeAndexpireTime(@Param("partyId")Long partyId);

}