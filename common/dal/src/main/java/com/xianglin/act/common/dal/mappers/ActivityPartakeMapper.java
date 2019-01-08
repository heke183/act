package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.ActivityPartake;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/20 19:30.
 */
public interface ActivityPartakeMapper extends Mapper<ActivityPartake> {

    Map<String, Object> getSignStatusSummary(@Param("partyId") Long partyId);

    /**
     * 早起之星
     *
     * @return
     */
    ActivityPartake getEarliestStar();

    /**
     * 手气之星
     *
     * @return
     */
    ActivityPartake getBiggestAwardStar();

    /**
     * 毅力之星
     *
     * @return
     */
    Map<String, Object> getMostPersistStar();

    /**
     * 达到连续打卡天数要求的用户
     *
     * @param fromDays 连续打卡最低天数
     * @param toDays   连续打卡最多天数
     * @param size     获奖人数
     * @return
     */
    List<Long> getBigDoubledAwardUsersFromContinuousSignInUser(@Param("fromDays") long fromDays, @Param("toDays") long toDays, @Param("size") int size);

    List<Long> getBigDoubledAwardUsersFromDisContinuousSignInUser(@Param("size") int size);

    List<Long> getBigDoubledAwardUsersFromAllSignInUser(@Param("size") int size);

    long countProbabilityAwardUser();

    List<Map<String, Object>> getProbabilityAwardUsers(@Param("offset") long offset, @Param("limit") long limit);

    long updateProbabilityAwardUserStatus();

    long updateProbabilityAwardValue(@Param("partyId") Long partyId, @Param("awardCode") String awardCode, @Param("prizeValue") BigDecimal prizeValue);

    ActivityPartake getActivityPartakeByPartyId(@Param("partyId") Long partyId);

    long updateActivityPartakeAwardAmount(@Param("partyId") Long partyId, @Param("awardCode") String awardCode, @Param("awardAmount") BigDecimal awardAmount);

    /**
     *  获取总金币数量
     * @return
     */
    BigDecimal getAwardCoinTotal();

}
