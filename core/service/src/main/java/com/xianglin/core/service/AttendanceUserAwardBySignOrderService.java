package com.xianglin.core.service;

import com.google.common.collect.Range;
import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.session.SqlSession;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/24 22:07.
 */
public interface AttendanceUserAwardBySignOrderService {

    void dispatchProbabilityAward(List<ProbabilityAwardRange> probabilityAardRanges, Long partyId, long userCount, long order, BigDecimal activityFee);

    void commit();

    void setSqlSession(SqlSession sqlSession);

    void removeSqlSession();

    class ProbabilityAwardRange {

        private String awardCode;

        private Range<BigDecimal> orderRange;

        private BigDecimal minValue;

        private BigDecimal maxValue;

        public ProbabilityAwardRange(String awardCode, BigDecimal minOrderalue, BigDecimal maxOrderValue, BigDecimal minPrizeValue, BigDecimal maxPrizeValue) {

            orderRange = Range.openClosed(minOrderalue, maxOrderValue);
            this.awardCode = awardCode;
            this.minValue = minPrizeValue;
            this.maxValue = maxPrizeValue.add(BigDecimal.ONE);
        }

        public boolean matches(BigDecimal orderValue) {
            return orderRange.contains(orderValue);
        }


        public int awardCoinNum() {
            return RandomUtils.nextInt(minValue.toBigInteger().intValue(), maxValue.toBigInteger().intValue());
        }

        public String getAwardCode() {
            return awardCode;
        }

        public void setAwardCode(String awardCode) {
            this.awardCode = awardCode;
        }

        public Range<BigDecimal> getOrderRange() {
            return orderRange;
        }

        public void setOrderRange(Range<BigDecimal> orderRange) {

            this.orderRange = orderRange;
        }

        public BigDecimal getMinValue() {

            return minValue;
        }

        public void setMinValue(BigDecimal minValue) {

            this.minValue = minValue;
        }

        public BigDecimal getMaxValue() {

            return maxValue;
        }

        public void setMaxValue(BigDecimal maxValue) {

            this.maxValue = maxValue;
        }
    }
}
