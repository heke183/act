package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.ActVoteRel;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

public interface ActVoteRelMapper extends Mapper<ActVoteRel> {

    /**
     * 插入投票记录
     *
     * @return
     */
    int insertRecord(@Param("activityCode") String activityCode,
                     @Param("partyId") Long partyId,
                     @Param("userType") String userType,
                     @Param("toPartyId") Long toPartyId);

    /**
     * 插入投票记录
     *
     * @return
     */
    int insertRecordOfWorldCup(@Param("activityCode") String activityCode,
                     @Param("partyId") Long partyId,
                     @Param("userType") String userType,
                     @Param("toPartyId") Long toPartyId,
                     @Param("amount") BigDecimal amount);

    /**
     * activityCode
     * @param partyId
     * @param activityCode
     * @return
     */
    int selectVotedCountToday(@Param("partyId")long partyId, @Param("activityCode") String activityCode);

    /**
     *
     * @param activityCode
     */
    void updateVoteRecordStatus(String activityCode);

    /**
     * 计算奖池金额
     *
     * @return
     */
    int countGold(String activityCode);

    /**
     *
     * @param toPartyId
     * @return
     */
    List<ActVoteRel> selectVoterRatio(@Param("activityCode") String activityCode, @Param("toPartyId") Long toPartyId);

    /**
     * 查询投票记录
     *
     * @param activityCode
     * @param partyId
     * @return
     */
    List<ActVoteRel> selectVoteRecord(@Param("activityCode") String activityCode, @Param("partyId") Long partyId);

    /**
     * 更新获得的奖励
     *
     * @param activityCode
     * @return
     */
    int updateAwardAmount(String activityCode);
}