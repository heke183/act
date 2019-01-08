package com.xianglin.core.service.impl;

import com.xianglin.act.common.dal.mappers.ActivityPartakeMapper;
import com.xianglin.core.service.AttendanceUserAwardBySignOrderService;
import com.xianglin.core.service.SystemGoldCoinService;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 计算随机金币奖励
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/24 23:06.
 */
@Component
public class AttendanceUserAwardBySignOrderServiceImpl implements AttendanceUserAwardBySignOrderService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceUserAwardBySignOrderServiceImpl.class);

    private static final ThreadLocal<SqlSession> SQL_SESSION_THREAD_LOCAL = new ThreadLocal<>();

    private static final ProbabilityAwardRange DEFAULT_AWARD = new ProbabilityAwardRange("DEFAULT_AWARD", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(1), BigDecimal.valueOf(1));

    private static final int DEFAULT_AWARD_AMOUNT = 101;

    @Autowired
    private SystemGoldCoinService goldCoinService;

    @Override
    public void dispatchProbabilityAward(List<ProbabilityAwardRange> probabilityAardRanges, Long partyId, long userCount, long order, BigDecimal activityFee) {

        try {
            if (probabilityAardRanges.isEmpty()) {
                logger.info("===========[[ 没有随机金币奖励，跳过随机金币奖励分配 partyId:{} ]]===========", partyId);
                return;
            }
            BigDecimal orderPercent;
            //人数小于10，则放在前 10%区间里
            if (userCount >= 10) {
                BigDecimal orderBigDecimal = new BigDecimal(order);
                BigDecimal userCountBigDecimal = new BigDecimal(userCount);
                orderPercent = orderBigDecimal.divide(userCountBigDecimal, 8, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            } else {
                orderPercent = new BigDecimal(5);
            }
            //得到满足获奖区间条件的第一奖励区间
            Optional<ProbabilityAwardRange> firstAwardRange = probabilityAardRanges.stream()
                    .filter(input -> input.matches(orderPercent))
                    .findFirst();
            //如果没有，就发默认奖励101
            ProbabilityAwardRange probabilityAwardRange = firstAwardRange.get();
            //得到随机金币奖励值
            int awardCoinNum = probabilityAwardRange.awardCoinNum();
            //将奖励值加到活动参与报名费上，将报名费和随机奖励一并发放给打卡用户
            BigDecimal prizeValue = new BigDecimal(awardCoinNum).add(activityFee);
            //更新用户奖励金额
            goldCoinService.dispathCoin2People(partyId, prizeValue.intValue());
            SQL_SESSION_THREAD_LOCAL.get().getMapper(ActivityPartakeMapper.class).updateProbabilityAwardValue(partyId, probabilityAwardRange.getAwardCode(), prizeValue);
        } catch (Exception e) {
            logger.error("===========发放奖励异常，发放默认奖励101金币：partyId -》 [[ {} ]]===========", partyId, e);
            //发放默认奖励
            try {
                goldCoinService.dispathCoin2People(partyId, DEFAULT_AWARD_AMOUNT);
                SQL_SESSION_THREAD_LOCAL.get().getMapper(ActivityPartakeMapper.class).updateProbabilityAwardValue(partyId, DEFAULT_AWARD.getAwardCode(), BigDecimal.valueOf(DEFAULT_AWARD_AMOUNT));
            } catch (Exception e1) {
                logger.error("===========，发放默认奖励101金币奖励异常，忽略异常，继续跑批流程：[[ {} ]]===========", partyId, e1);
            }
        }
    }

    @Override
    public void commit() {

        SQL_SESSION_THREAD_LOCAL.get().commit();
        //清理缓存
        SQL_SESSION_THREAD_LOCAL.get().clearCache();
    }

    @Override
    public void setSqlSession(SqlSession sqlSession) {

        SQL_SESSION_THREAD_LOCAL.set(sqlSession);
    }

    @Override
    public void removeSqlSession() {

        SQL_SESSION_THREAD_LOCAL.get().close();
        SQL_SESSION_THREAD_LOCAL.remove();
    }


}
