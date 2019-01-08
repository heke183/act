package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.ActGroupInfoDetail;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Describe :
 * Created by xingyali on 2018/12/19 10:49.
 * Update reason :
 */
public interface ActGroupInfoDetailMapper extends Mapper<ActGroupInfoDetail> {
    /**查询用户参与的团 
     * @param 
     * @return
     */
    @Select("SELECT * from act_group_info_detail a LEFT JOIN act_group_info b on a.INFO_ID =b.ID where a.PARTY_ID = #{partyId} and b.`STATUS` = #{status} and a.IS_DELETED='N' and b.IS_DELETED='N' order by a.id desc")
    List<ActGroupInfoDetail> selectGroupInfoDetailByPatyId(Long partyId, String status);
}
