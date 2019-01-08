package com.xianglin.act.common.dal.mappers;
import java.util.List;


import com.xianglin.act.common.dal.model.ActPlant;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author ex-jiangyongtao
 */
public interface ActPlantMapper extends Mapper<ActPlant> {

    /**
     * 查询用户当前的爱心值
     * @param partyId
     * @return
     */
  ActPlant findByPartyId(@Param("partyId")Long partyId);

}