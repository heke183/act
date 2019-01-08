package com.xianglin.act.common.dal.mappers;


import com.xianglin.act.common.dal.model.ActPlantTip;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author ex-jiangyongtao
 */
public interface ActPlantTipMapper extends Mapper<ActPlantTip> {

    /**
     * 更新用户弹窗信息
     * @param partyId 用户id
     */
    void updateByPartyId(@Param("partyId") Long partyId);

}