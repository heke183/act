package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.ActVoteItem;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ActVoteItemMapper extends Mapper<ActVoteItem> {

    int updateVoteNum(long partyId);

    List<ActVoteItem> selectItemList(@Param("activityCode") String activityCode, @Param("order") String order,@Param("pageSize") Integer pageSize,@Param("lastId")Long lastId);

    /**
     * 我的
     *
     * @param partyId
     * @return
     */
    ActVoteItem myItem(@Param("partyId") long partyId, @Param("activityCode") String activityCode);

    /**
     * 查询世界杯队伍
     *
     * @return
     */
    List<ActVoteItem> selectItemListOfWorldCup();
}