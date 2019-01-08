package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.ActStepDetail;
import com.xianglin.act.common.dal.model.PageReq;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ActStepMapper extends Mapper<ActStepDetail> {

//    /**
//     * 更新一条记录
//     * @param partyId
//     * @param day
//     * @param type
//     * @return
//     */
//    boolean updateOne(@Param("partyId") Long partyId, @Param("day") String day, @Param("type") String type);
//
//    /**
//     * 查询一条记录
//     * @param partyId
//     * @param day
//     * @param type
//     * @return
//     */
//    ActStepDetail selectOne(@Param("partyId") Long partyId, @Param("day") String day, @Param("type") String type);
//
//    /**
//     * 插入一条记录
//     * @param detail
//     * @return
//     */
//    boolean insertOne(ActStepDetail detail);
//
//    /**
//     * 查询用户记录
//     * @param partyId
//     * @return
//     */
//    ActStepDetail selectActStepDetail(@Param("partyId") Long partyId, @Param("day") String day, @Param("type") String type);
//
//    /**
//     * 获取用户当天总步数
//     * @param partyId
//     * @return
//     */
//    int selectTotalStepNumber(@Param("partyId") Long partyId, @Param("day") String day, @Param("type") String type);
//
//    /**
//     * 获取用户当天兑换金币总数
//     * @param partyId
//     * @return
//     */
//    int selectTotalGoldCoin(@Param("partyId") Long partyId, @Param("day") String day);
//
//
//
//    /**
//     * 获取用户累计参与天数
//     * @return
//     */
//    int selectDays(@Param("partyId") Long partyId);
//
//    /**
//     * 获取用户累计兑换金币次数
//     * @param partyId
//     * @return
//     */
//    int selectConversions(@Param("partyId") Long partyId, @Param("status") String status);
//
//    /**
//     * 获取用户累计奖励金币总数
//     * @param partyId
//     * @return
//     */
//    int selectGoldCoins(@Param("partyId") Long partyId);
//
//    /**
//     * 同步客户端步数
//     * @param partyId
//     * @return
//     */
//    boolean updateStepNumber(@Param("stepNumber") int stepNumber, @Param("partyId") Long partyId, @Param("type") String type);

     /**
     * 获取幸运奖励
     * @return
     */
    ActStepDetail selectLuckyAward(@Param("day") String day,@Param("status") String status);


    /**更新当日总步数，总金币数
     * @param partyId
     * @param day
     * @return
     */
    boolean updateDayTotail(@Param("partyId") Long partyId, @Param("day") String day);

    /**
     * 根据条件查询记录
     * @param actStepDetail
     * @param orderBy
     * @param pageReq
     * @return
     */
    List<ActStepDetail> selectActStepDetailList(@Param("paras") ActStepDetail actStepDetail,@Param("orderBy")  String orderBy,
                                                @Param("excludeStepNumber") String excludeStepNumber,@Param("page") PageReq pageReq);

    /**
     * 查总金币数
     * @param build
     * @return
     */
    int selectGoldRewardSum(@Param("paras") ActStepDetail build);

    /**
     * 
     * @param build
     * @return
     */
    int selectActStepCount(@Param("paras") ActStepDetail build);
}
