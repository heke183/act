package com.xianglin.act.common.dal.mappers;


import com.xianglin.act.common.dal.model.ActGamePlane;
import com.xianglin.act.common.dal.model.ActPlantTip;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * @author wanglei
 */
public interface ActGamePlaneMapper extends Mapper<ActGamePlane> {


    /**周排行榜查询
     * @param startDay
     * @return
     */
    @Select("select a.id,a.PARTY_ID as partyId,a.`DAY` as day,a.SCORE as score,a.SHOT_COUNT as shotCount,a.STAGE as stage,a.COIN_REWARD as coinReward,a.STAGE_REWARD as stageReward,a.RANDOM_REWARD as randomReward,a.`STATUS` as status from act_game_plane a,( select min(t.ID) id from act_game_plane t,( select t.PARTY_ID,max(t.SCORE) score from act_game_plane t where t.IS_DELETED='N' and `DAY` >= #{startDay} group by t.PARTY_ID) s where t.IS_DELETED='N' and t.PARTY_ID=s.PARTY_ID and t.SCORE=s.score group by t.PARTY_ID ) b where a.ID=b.id order by a.SCORE desc,a.ID asc")
    List<ActGamePlane> selectWorkRanking(String startDay);
}