package com.xianglin.core.service.impl;

import com.xianglin.core.service.AttendanceUserAwardBySignOrderService;
import org.apache.ibatis.session.SqlSession;

import java.math.BigDecimal;
import java.util.List;

public class AttendanceAwardByRandomServiceImpl implements AttendanceUserAwardBySignOrderService {
    @Override
    public void dispatchProbabilityAward(List<ProbabilityAwardRange> probabilityAardRanges, Long partyId, long userCount, long order, BigDecimal activityFee) {

    }

    @Override
    public void commit() {

    }

    @Override
    public void setSqlSession(SqlSession sqlSession) {

    }

    @Override
    public void removeSqlSession() {

    }
}
