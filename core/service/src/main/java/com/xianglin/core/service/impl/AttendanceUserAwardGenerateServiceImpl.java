package com.xianglin.core.service.impl;

import com.xianglin.act.common.dal.enums.AttendanceAwardTypeEnum;
import com.xianglin.act.common.dal.enums.TargetAttendanceCustomerTypeEnum;
import com.xianglin.act.common.dal.mappers.ActivityMapper;
import com.xianglin.act.common.dal.mappers.ActivityPartakeMapper;
import com.xianglin.act.common.dal.mappers.AttendanceCoinAwardMapper;
import com.xianglin.act.common.dal.model.Activity;
import com.xianglin.act.common.dal.model.ActivityPartake;
import com.xianglin.act.common.dal.model.AttendanceCoinAward;
import com.xianglin.core.model.enums.ActivityEnum;
import com.xianglin.core.model.enums.CustomerAttendanceStatusEnum;
import com.xianglin.core.model.enums.UserType;
import com.xianglin.core.service.AttendanceUserAwardBySignOrderService;
import com.xianglin.core.service.AttendanceUserAwardGenerateService;
import com.xianglin.core.service.SystemGoldCoinService;
import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/23 15:32.
 */

/**
 * 用户奖励服务接口实现类
 */
//@Component
public class AttendanceUserAwardGenerateServiceImpl implements AttendanceUserAwardGenerateService {

    private static final double PAGE_SIZE = 500.0D;

    private static final Logger logger = LoggerFactory.getLogger(AttendanceUserAwardGenerateServiceImpl.class);

    @Autowired
    private ActivityPartakeMapper activityPartakeMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private AttendanceCoinAwardMapper attendanceCoinAwardMapper;

    @Autowired
    private AttendanceUserAwardBySignOrderService attendanceUserAwardBySignOrderService;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private SystemGoldCoinService goldCoinService;

    public void calculateUserAward() {
        //检查是否已经结算
        boolean hasAwardToday = checkHasAwardToday();
        if (hasAwardToday) {
            logger.info("=========== 今日已经跑批，无需计算金币奖励 ===========");
            return;
        }
        //统计当日打卡签到人数
        long activityUserCount = countAllSignInUserToday();
        if (activityUserCount == 0) {
            logger.info("=========== 今日打卡人数为0，无需计算金币奖励 ===========");
            return;
        }
        //若当日所有真实打卡成功人数<4(翻倍奖励数量),按照随机概率分配，大奖不用必须分配
        //查询翻倍奖励数量
        Example example = new Example(AttendanceCoinAward.class);
        example.and()
                .andEqualTo("awardType", AttendanceAwardTypeEnum.MULTIPLY)
                .andEqualTo("isDeleted", false);
        int mutiAwardSize = attendanceCoinAwardMapper.selectByExample(example).size();
        long activityDays = getActivityDays();
        if (activityUserCount < mutiAwardSize && activityDays > 6) {
            logger.info("===========当前打卡人数小于翻倍奖励人数：[[ {} ]],走特殊情况流程===========", mutiAwardSize);
            try {
                //随机发放翻倍奖励
                calculateProbabilityDoubleAwards();
            } catch (Exception e) {
                logger.error("===========人数小于4且活动天数大于6时发放翻倍奖励异常，忽略异常后发放随机奖励===========", e);
            }
            try {
                //发放随机奖励
                calculateProbabilityAwards();
            } catch (Exception e) {
                logger.error("===========发放随机奖励异常，需要进行数据订正流程===========", e);
            }
            return;
        }
        //计算翻倍大奖
        //翻倍奖励发放完成后，用户状态已经变为AWARD
        logger.info("===========用户人数：[[ {} ]]大于翻倍奖励个数：[[ {} ]]===========", activityUserCount, mutiAwardSize);
        try {
            calculateBigDoubledAwards(activityDays);
        } catch (Exception e) {
            logger.error("===========人数大于3时发放翻倍奖励异常，忽略异常后发放随机奖励===========", e);
        }
        //计算随机金币奖励
        try {
            calculateProbabilityAwards();
        } catch (Exception e) {
            logger.error("===========发放随机奖励异常，需要进行数据订正流程===========", e);
        }
        logger.info("===========今日打卡奖励计算完成===========");
    }

    /**
     * 打卡人数不足翻倍奖励人数时，随机发放翻倍奖励
     */
    private void calculateProbabilityDoubleAwards() {
        //发放的翻倍奖励个数
        //连续打卡翻倍奖励
        List<AttendanceCoinAward> bigDoubledAwards4Con = getBigDoubledAwards(TargetAttendanceCustomerTypeEnum.CONTINUOUS);
        //非连续打卡翻倍奖励
        List<AttendanceCoinAward> bigDoubledAwards4DisCon = getBigDoubledAwards(TargetAttendanceCustomerTypeEnum.DISCONTINUOUS);
        //把非连续打卡翻倍奖励的集合元素放入到翻倍奖励的集合中，得到总的翻倍奖励人数
        bigDoubledAwards4Con.addAll(bigDoubledAwards4DisCon);
        //打乱翻倍奖励集合元素
        Collections.shuffle(bigDoubledAwards4Con);
        //获取[0,翻倍奖励集合元素个数)之间随机数
        int count = RandomUtils.nextInt(0, bigDoubledAwards4Con.size());
        //获取翻倍奖励用户集合
        List<Long> user = activityPartakeMapper.getBigDoubledAwardUsersFromAllSignInUser(count);
        //发放翻倍奖励
        for (int i = 0; i < user.size(); i++) {
            dispatchBigDoubledAward(bigDoubledAwards4Con.get(i), user.get(i));
        }
    }

    /**
     * 计算随机金币奖励
     */
    private void calculateProbabilityAwards() {
        //初始化batchSqlsesion
        initSqlSession();
        //获取非翻倍的随机奖励
        List<AttendanceCoinAward> unmultiplyAwards = attendanceCoinAwardMapper.getUnmutifyAwards();
        //根据比例生成打卡时序区间
        List<AttendanceUserAwardBySignOrderService.ProbabilityAwardRange> awardRangeList = unmultiplyAwards
                .stream()
                //奖励排序
                .sorted(Comparator.comparing(AttendanceCoinAward::getSignInOrderFloorPercent))
                .map(input -> new AttendanceUserAwardBySignOrderService.ProbabilityAwardRange(input.getAwardCode(), input.getSignInOrderFloorPercent(), input.getSignInOrderCeilPercent(), input.getMinValue(), input.getMaxValue()))
                .collect(Collectors.toList());
        //分页处理打卡用户的奖励
        long probabilityAwardUserCount = activityPartakeMapper.countProbabilityAwardUser();
        //每次处理500个用户
        long times = (long) Math.ceil(probabilityAwardUserCount / PAGE_SIZE);
        for (long i = 0; i < times; i++) {
            //拿到用户partyId和排序，默认处理当天打卡成功，status为SIGN_IN的用户
            List<Map<String, Object>> probabilityAwardUsers = activityPartakeMapper.getProbabilityAwardUsers(((long) (i * PAGE_SIZE)), ((long) PAGE_SIZE));
            probabilityAwardUsers
                    .stream()
                    .filter(Objects::nonNull)
                    //处理每一个用户的金币奖励，委托给attendanceUserAwardBySignOrderService
                    .forEach(input -> attendanceUserAwardBySignOrderService.dispatchProbabilityAward(
                            awardRangeList, (long) input.get("PARTY_ID"),
                            probabilityAwardUserCount, (long) input.get("ROW_NUM"),
                            ((BigDecimal) input.get("ACTIVITY_FEE"))));
            //每500条提交
            attendanceUserAwardBySignOrderService.commit();
        }
        //关闭SqlSession
        attendanceUserAwardBySignOrderService.removeSqlSession();
        //更新随机金币奖励用户的状态为AWARD
        activityPartakeMapper.updateProbabilityAwardUserStatus();    }

    private void initSqlSession() {

        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        //注入到ThreadLocal中
        attendanceUserAwardBySignOrderService.setSqlSession(sqlSession);
    }

    /**
     * 翻倍大奖用户
     * 包括连续打卡和非连续打卡
     * 奖励发放时，修改用户的状态为AWARD
     *
     * @param activityDays
     */
    private void calculateBigDoubledAwards(long activityDays) {
        //打卡天数是上线天数减1
        if (activityDays <= 6) {  //翻倍大奖随机分配给所有用户，
            logger.info("===========当前是活动第[[ {} ]]天，满足活动前五天的规则===========", activityDays);
            List<AttendanceCoinAward> bigDoubledAwards = getBigDoubledAwards(TargetAttendanceCustomerTypeEnum.CONTINUOUS);
            bigDoubledAwards.addAll(getBigDoubledAwards(TargetAttendanceCustomerTypeEnum.DISCONTINUOUS));
            List<Long> bigDoubledAwardUsers = activityPartakeMapper.getBigDoubledAwardUsersFromAllSignInUser(bigDoubledAwards.size());
            //获奖人数小于或等于奖励个数，因此以获奖人数遍历，避免越界异常
            for (int i = 0; i < bigDoubledAwardUsers.size(); i++) {
                this.dispatchBigDoubledAward(bigDoubledAwards.get(i), bigDoubledAwardUsers.get(i));
            }
            return;
        }
        //打卡天数是上线天数减1
        long from = activityDays - 3 - 1;
        long to = activityDays - 1;
        //连续打卡用户翻倍奖励
        List<AttendanceCoinAward> bigDoubledAwards = getBigDoubledAwards(TargetAttendanceCustomerTypeEnum.CONTINUOUS);
        //连续打卡翻倍奖励用户
        List<Long> continuousBigDoubledAwardUsers = activityPartakeMapper.getBigDoubledAwardUsersFromContinuousSignInUser(from, to, bigDoubledAwards.size());
        //随机打乱
        Collections.shuffle(bigDoubledAwards);
        //获奖人数小于或等于奖励个数，因此以获奖人数遍历，避免越界异常
        for (int i = 0; i < continuousBigDoubledAwardUsers.size(); i++) {
            this.dispatchBigDoubledAward(bigDoubledAwards.get(i), continuousBigDoubledAwardUsers.get(i));
        }
        //非连续打卡用户翻倍奖励
        List<AttendanceCoinAward> discontinuousBigDoubledAwards = getBigDoubledAwards(TargetAttendanceCustomerTypeEnum.DISCONTINUOUS);
        //连续打卡用户翻倍奖励未发放完的奖励发放给非连续打卡用户
        if (bigDoubledAwards.size() - continuousBigDoubledAwardUsers.size() > 0) {
            int fromIndex = continuousBigDoubledAwardUsers.size();
            if (fromIndex <= 0) {
                fromIndex = 0;
            }
            int toIndex = bigDoubledAwards.size();
            List<AttendanceCoinAward> tempAwardList = bigDoubledAwards.subList(fromIndex, toIndex);
            discontinuousBigDoubledAwards.addAll(tempAwardList);
        }
        //非连续打卡翻倍奖励用户
        List<Long> disContinuousSignInUser = activityPartakeMapper.getBigDoubledAwardUsersFromDisContinuousSignInUser(discontinuousBigDoubledAwards.size());
        //获奖人数小于或等于奖励个数，因此以获奖人数遍历，避免越界异常
        for (int i = 0; i < disContinuousSignInUser.size(); i++) {
            this.dispatchBigDoubledAward(discontinuousBigDoubledAwards.get(i), disContinuousSignInUser.get(i));
        }
        logger.info("===========[[ {连续打卡翻倍大奖励发放完成} ]]===========");
    }

    /**
     * 成功打卡人数
     *
     * @return
     */
    private long countAllSignInUserToday() {

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

    /**
     * 今天是否已经结算过
     *
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
     * 活动进行了多少天
     *
     * @return
     */
    public long getActivityDays() {

        Activity thisActivity = activityMapper.selectActivity(ActivityEnum.ATTENDANCE_AWARD.name());
        Date startDate = thisActivity.getStartDate();
        LocalDate nowDate = LocalDate.now();
        LocalDate activityStartLocalDate = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
        return 1 + (nowDate.toEpochDay() - activityStartLocalDate.toEpochDay());
    }

    /**
     * 获取连续打卡翻倍奖励
     *
     * @return
     */
    private List<AttendanceCoinAward> getBigDoubledAwards(TargetAttendanceCustomerTypeEnum customerType) {

        Example example = new Example(AttendanceCoinAward.class);
        //翻倍奖励乱序
        //example.orderBy("RAND()");
        example.and()
                .andEqualTo("targetCustomerType", customerType)
                .andEqualTo("awardType", AttendanceAwardTypeEnum.MULTIPLY)
                .andEqualTo("isDeleted", false);
        List<AttendanceCoinAward> temp = attendanceCoinAwardMapper.selectByExample(example);
        Collections.shuffle(temp);
        return temp;
    }

    /**
     * 发放翻倍奖励
     *
     * @param award
     * @param partyId
     */
    private void dispatchBigDoubledAward(AttendanceCoinAward award, Long partyId) {

        try {
            ActivityPartake activityPartake = activityPartakeMapper.getActivityPartakeByPartyId(partyId);
            BigDecimal activityFee = activityPartake.getActivityFee();
            Integer awardTimes = award.getAwardTimes();
            if (activityFee == null) {
                activityFee = BigDecimal.ZERO;
            }
            if (awardTimes == null) {
                awardTimes = 1;
            }
            //奖励金额
            BigDecimal amount = activityFee.multiply(new BigDecimal(awardTimes));
            //发放金币奖励
            goldCoinService.dispathCoin2People(partyId, amount.intValue());
            activityPartakeMapper.updateActivityPartakeAwardAmount(partyId, award.getAwardCode(), amount);
        } catch (Exception e) {
            logger.error("===========发放翻倍金币奖励异常，后续进行数据订正，忽略异常继续跑批。partyId：[[ {} ]]===========", partyId, e);
        }
    }
}
