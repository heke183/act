package com.xianglin.core.service.impl;

import com.xianglin.act.common.dal.enums.PrizeType;
import com.xianglin.act.common.dal.mappers.ActivityMapper;
import com.xianglin.act.common.dal.mappers.ActivityPartakeMapper;
import com.xianglin.act.common.dal.model.Activity;
import com.xianglin.act.common.dal.model.ActivityPartake;
import com.xianglin.core.model.enums.ActivityEnum;
import com.xianglin.core.model.enums.CustomerAttendanceStatusEnum;
import com.xianglin.core.model.enums.UserType;
import com.xianglin.core.service.AttendanceUserAwardGenerateService;
import com.xianglin.core.service.SystemGoldCoinService;
import com.xianglin.xlschedule.common.service.spi.annotation.ServiceInterface;
import com.xianglin.xlschedule.common.service.spi.annotation.ServiceMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service("randomAwardService")
@ServiceInterface(AttendanceUserAwardGenerateService.class)
public class RandomAwardServiceImpl implements AttendanceUserAwardGenerateService {

    private static final Logger logger = LoggerFactory.getLogger(RandomAwardServiceImpl.class);

    private static final int DEFAULT_AWARD_AMOUNT = 101;

    private static final int BIGGEST_AWARD_STAR = 600;

    @Autowired
    private ActivityPartakeMapper activityPartakeMapper;

    @Autowired
    private SystemGoldCoinService systemGoldCoinService;

    @Autowired
    private ActivityMapper activityMapper;

    /**
     * 计算用户奖励
     */
    @Override
    @ServiceMethod(description = "打卡结算")
    public void calculateUserAward() {
        boolean hasAwardToday = checkHasAwardToday();
        if (hasAwardToday) {
            logger.info("========== 今日已跑批，无需计算用户金币奖励 ==========");
            return;
        }
        long activityUserCount = countAllSignInUserToday();
        if (activityUserCount == 0) {
            logger.info("========== 今日打卡人数为0，无需计算用户金币奖励 ==========");
            return;
        }
        //前一天报名人数
        int signUpCount = countAllSignUpUserYesterday();
        //活动当天打卡成功人数
        int signInCount = countAllSignInUserToday();
        //活动当天打卡失败人数
        int signFailCount = signUpCount - signInCount;
        //金币池金币总数量
        int totalCoin = activityPartakeMapper.getAwardCoinTotal().intValue();

        LocalDate now = LocalDate.now();
        Example example = new Example(ActivityPartake.class);
        example.and()
                .andEqualTo("userType", UserType.ATTENDANCE_USER.name())
                .andEqualTo("activityCode", ActivityEnum.ATTENDANCE_AWARD.name())
                .andEqualTo("status", CustomerAttendanceStatusEnum.SIGN_IN.name())
                .andEqualTo("isDeleted", 0)
                .andGreaterThanOrEqualTo("signInDate", now);
        List<ActivityPartake> awardUserList = activityPartakeMapper.selectByExample(example);
        if(awardUserList == null) {
            logger.info("========== 今日打卡人数为0，无需计算用户金币奖励 ==========");
            return;
        }

        Collections.shuffle(awardUserList);
       /* if (signInCount == 1) {
            ActivityPartake activityPartake = awardUserList.get(0);
            logger.info("一人打开成功：{}", activityPartake.getPartyId());
            systemGoldCoinService.dispathCoin2People(activityPartake.getPartyId(), DEFAULT_AWARD_AMOUNT + BIGGEST_AWARD_STAR);
            activityPartakeMapper.updateActivityPartakeAwardAmount(activityPartake.getPartyId(),
                    PrizeType.XL_GOLD_COIN.name(), BigDecimal.valueOf(DEFAULT_AWARD_AMOUNT + BIGGEST_AWARD_STAR));
            return;
        }*/

        if (100 * signFailCount > signInCount) {

            //参与随机分配的金币数量
            int awardCount = totalCoin - DEFAULT_AWARD_AMOUNT * signInCount;
            List<Integer> awardCoinCountList = getUserAwardCoinCount(awardCount, signInCount);
            Collections.shuffle(awardCoinCountList);
//            Integer max = awardCoinCountList.stream().max(Integer::max).get();
            Integer max = Collections.max(awardCoinCountList);
            boolean flag = false;
            for (int i = 0; i < awardUserList.size(); i++) {
                Long partyId = awardUserList.get(i).getPartyId();
                int amount = awardCoinCountList.get(i);
                if (amount == max && !flag) {
                    flag = true;
                    amount += BIGGEST_AWARD_STAR;
                }
                systemGoldCoinService.dispathCoin2People(partyId, amount + DEFAULT_AWARD_AMOUNT);
                activityPartakeMapper.updateActivityPartakeAwardAmount(partyId, PrizeType.XL_GOLD_COIN.name(), BigDecimal.valueOf(amount + DEFAULT_AWARD_AMOUNT));
            }
        } else {
            Random random = new Random();
            int i = random.nextInt(awardUserList.size());
            ActivityPartake activityPartake = awardUserList.get(i);
            Long partyId = activityPartake.getPartyId();

            systemGoldCoinService.dispathCoin2People(partyId, BIGGEST_AWARD_STAR + DEFAULT_AWARD_AMOUNT);
            activityPartakeMapper.updateActivityPartakeAwardAmount(partyId, PrizeType.XL_GOLD_COIN.name(), BigDecimal.valueOf(BIGGEST_AWARD_STAR + DEFAULT_AWARD_AMOUNT));
            awardUserList.remove(i);

            for (int j = 0; j < awardUserList.size(); j++) {
                activityPartake = awardUserList.get(j);
                partyId = activityPartake.getPartyId();
                systemGoldCoinService.dispathCoin2People(partyId, DEFAULT_AWARD_AMOUNT);
                activityPartakeMapper.updateActivityPartakeAwardAmount(partyId, PrizeType.XL_GOLD_COIN.name(), BigDecimal.valueOf(DEFAULT_AWARD_AMOUNT));
            }
        }
    }

    @Override
    public long getActivityDays() {
        Activity thisActivity = activityMapper.selectActivity(ActivityEnum.ATTENDANCE_AWARD.name());
        Date startDate = thisActivity.getStartDate();
        LocalDate nowDate = LocalDate.now();
        LocalDate activityStartLocalDate = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
        return 1 + (nowDate.toEpochDay() - activityStartLocalDate.toEpochDay());
    }

    /**
     * 采用二倍均值算法，求出用户随机金币奖励集合
     * @param awardCount
     * @param signInCount
     * @return
     */
    private List<Integer> getUserAwardCoinCount(Integer awardCount, Integer signInCount) {
        List<Integer> amountList = new ArrayList<>();
        int restCoinAmount = awardCount;
        int restUserCount = signInCount;
        Random random = new Random();
        int amount = 0;
        for (int i = 0; i < restUserCount - 1; i++) {
            if (logger.isDebugEnabled()) {
                logger.debug("--------二倍均值：{}", restCoinAmount / restUserCount * 2 + 1);
            }
            amount = random.nextInt(restCoinAmount / restUserCount * 2 + 1);
            restCoinAmount -= amount;
            signInCount--;
            amountList.add(amount);
        }
        amountList.add(restCoinAmount);
        return amountList;
    }

    /**
     * 今天是否已经结算过
     * @return
     */
    private boolean checkHasAwardToday() {
        LocalDate now = LocalDate.now();
        Example example = new Example(ActivityPartake.class);

        example.and()
                .andEqualTo("userType", UserType.ATTENDANCE_USER.name())
                .andEqualTo("activityCode", ActivityEnum.ATTENDANCE_AWARD.name())
                .andEqualTo("status", CustomerAttendanceStatusEnum.AWARD.name())
                .andEqualTo("isDeleted", 0)
                .andGreaterThanOrEqualTo("signInDate", now);
        return activityPartakeMapper.selectCountByExample(example) > 0;
    }

    /**
     * 前一天报名人数
     * @return
     */
    private int countAllSignUpUserYesterday() {
        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minus(1, ChronoUnit.DAYS);
        Example example = new Example(ActivityPartake.class);
        example.and()
                .andEqualTo("userType", UserType.ATTENDANCE_USER.name())
                .andEqualTo("activityCode", ActivityEnum.ATTENDANCE_AWARD.name())
                .andEqualTo("isDeleted", 0)
                .andGreaterThanOrEqualTo("createDate", yesterday);
        return activityPartakeMapper.selectCountByExample(example);
    }

    /**
     * 成功打卡人数
     * @return
     */
    private int countAllSignInUserToday() {
        LocalDate now = LocalDate.now();
        Example example = new Example(ActivityPartake.class);
        example.and()
                .andEqualTo("userType", UserType.ATTENDANCE_USER.name())
                .andEqualTo("activityCode", ActivityEnum.ATTENDANCE_AWARD.name())
                .andEqualTo("status", CustomerAttendanceStatusEnum.SIGN_IN.name())
                .andEqualTo("isDeleted", 0)
                .andGreaterThanOrEqualTo("signInDate", now);
        return activityPartakeMapper.selectCountByExample(example);
    }

}
