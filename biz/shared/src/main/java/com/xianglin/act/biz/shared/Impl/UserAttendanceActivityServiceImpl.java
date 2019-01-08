package com.xianglin.act.biz.shared.Impl;

import com.github.pagehelper.PageRowBounds;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.xianglin.act.biz.shared.UserAttendanceActivityService;
import com.xianglin.act.common.dal.mappers.ActivityMapper;
import com.xianglin.act.common.dal.mappers.ActivityPartakeMapper;
import com.xianglin.act.common.dal.mappers.ExtendPropMapper;
import com.xianglin.act.common.dal.model.Activity;
import com.xianglin.act.common.dal.model.ActivityPartake;
import com.xianglin.act.common.service.integration.PersonalServiceClient;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.ConfigPropertyUtils;
import com.xianglin.act.common.util.config.db.AttendanceConfiguration;
import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import com.xianglin.core.model.enums.*;
import com.xianglin.act.common.util.BizException;
import com.xianglin.core.model.exception.attendance.ActivityExpireException;
import com.xianglin.core.model.exception.attendance.ActivityOperationFailException;
import com.xianglin.core.model.vo.AttendanceActivityDetailVO;
import com.xianglin.core.model.vo.AttendanceRecordVO;
import com.xianglin.core.model.vo.AttendanceStarVO;
import com.xianglin.core.model.vo.UserAttendanceDetailVO;
import com.xianglin.core.service.AttendanceUserAwardGenerateService;
import com.xianglin.core.service.AttendanceUserStatusService;
import com.xianglin.core.service.SystemGoldCoinService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/18 10:32.
 */
@Service
public class UserAttendanceActivityServiceImpl implements UserAttendanceActivityService {

    /**
     * 分享log
     */
    private static final String SHARE_IMG_URL = "https://cdn02.xianglin.cn/d4c347924b2426bd8babc31f4ed4e7a7-20823.png";

    private static final String SHARE_DESC = "每天早上{0}点到{1}点来打卡，随机瓜分金币，稳赚不赔，可提现！";

    private static final int ACTIVITY_BASIC_AWARD = 100;

    private static final String ACT_SIGN_UP_LOCK_FLAG = "ACT:SIGN_UP:";

    private static final String ACT_SIGN_IN_LOCK_FLAG = "ACT:SIGN_IN:";

    private static final String SHARE_URL = "%s/act/page/goldCoins/daka.html";

    private static final String HISTORY_SHARE_MESSAGE = "我在乡邻通过早起打卡已经赚取{0}金币，坚持{1}天，加入打卡队列吧！";

    private static final String SIGIN_IN_FAIL = "打卡失败";

    private static final String WAITING_SIGN_IN = "待打卡";

    private static final ActivityEnum THIS_ACTITY = ActivityEnum.ATTENDANCE_AWARD;

    private static final String ACT_SHARE_TITLE = "快和我一起来乡邻早起打卡，保持健康还能赢金币,可提现哦！";

    @Autowired
    private AttendanceUserStatusService userStatusCheckService;

    @Autowired
    private PersonalServiceClient personalServiceClient;

    @Autowired
    private SystemGoldCoinService systemGoldCoinService;

    @Autowired
    private ActivityPartakeMapper activityPartakeRecordMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private AttendanceConfiguration attendanceConfiguration;

    @Autowired
    private ExtendPropMapper extendPropMapper;

    @Autowired
    private AttendanceUserAwardGenerateService attendanceUserAwardGenerateService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public Optional<UserAttendanceDetailVO> getUserAttendanceHistory(Long currentPartyId) {
        //查询奖励概述
        Map<String, Object> signStatusSummary = activityPartakeRecordMapper.getSignStatusSummary(currentPartyId);
        Example example = new Example(ActivityPartake.class);
        example.setOrderByClause("CREATE_DATE DESC");
        example.and()
                .andEqualTo("partyId", currentPartyId)
                .andEqualTo("userType", UserType.ATTENDANCE_USER.name())
                .andEqualTo("activityCode", ActivityEnum.ATTENDANCE_AWARD.name())
                .andEqualTo("isDeleted", 0);
        List<ActivityPartake> activityPartakes = activityPartakeRecordMapper.selectByExample(example);
        //生成打卡记录
        LocalTime signInEndTime = getSignInEndTime();
        List<AttendanceRecordVO> records = activityPartakes.stream()
                .map(inputDO -> convertAttendanceRecord(inputDO, signInEndTime))
                .collect(Collectors.toList());

        String successDays = signStatusSummary.get("SUCCESS_DAYS").toString();
        String awardAmount = ((BigDecimal) signStatusSummary.get("AWARD_AMOUNT")).toBigInteger().toString();
        UserAttendanceDetailVO buildVO = UserAttendanceDetailVO.builder()
                .shareImg(SHARE_IMG_URL)
                .shareTitle(MessageFormat.format(HISTORY_SHARE_MESSAGE, awardAmount, successDays))
                .shareUrl(String.format(SHARE_URL, ConfigPropertyUtils.get("h5.server.url")))
                .shareDesc(MessageFormat.format(SHARE_DESC, getSignInStartTime().getHour(), getSignInEndTime().getHour()))
                .successDays(successDays)
                .successAmout(awardAmount)
                .recordList(records)
                .build();
        return Optional.of(buildVO);
    }

    /**
     * 将打卡记录DO转换成VO
     *
     * @param inputDO
     * @param signInEndTime
     * @return
     */
    private AttendanceRecordVO convertAttendanceRecord(ActivityPartake inputDO, LocalTime signInEndTime) {

        LocalDateTime createDate = inputDO.getCreateDate();
        String statusMessage = "";
        boolean showIcon = false;
        String status = inputDO.getStatus();
        CustomerAttendanceStatusEnum attendanceStatus = CustomerAttendanceStatusEnum.valueOf(status);
        switch (attendanceStatus) {
            case SIGN_UP: //报名状态，有两种情况，等待打卡和打卡失败
                //昨天
                LocalDate yesterday = LocalDate.now().minusDays(1);
                LocalDate createLocalDate = createDate.toLocalDate();

                if (createLocalDate.isBefore(yesterday)) { //昨天之前报名了但是未打卡
                    statusMessage = SIGIN_IN_FAIL;
                }
                if (createLocalDate.equals(yesterday)) { //昨天报名了但是为打卡
                    //两种情况，今天还没结束打卡，今天已经结束打卡
                    if (LocalDateTime.now().toLocalTime().isBefore(signInEndTime)) {
                        statusMessage = WAITING_SIGN_IN;
                    } else {
                        statusMessage = SIGIN_IN_FAIL;
                    }
                }
                if (createLocalDate.isAfter(yesterday)) { //今天报名了但是未打卡
                    statusMessage = WAITING_SIGN_IN;
                }
                break;
            case SIGN_IN: //今天已经打卡，但是还没有分到金币
                statusMessage = "瓜分金币中";
                break;
            case AWARD: //已经分到金币
                BigDecimal prizeValue = inputDO.getPrizeValue();
                String goldCoinValue = prizeValue.toBigInteger().toString();
                statusMessage = "+" + goldCoinValue;
                showIcon = true;
                break;
        }
        String showDate = createDate.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));  // createDate 说明的是打卡报名日期，打卡状态表现的日期应该是报名日期加1
        return AttendanceRecordVO.builder()
                .date(showDate)
                .statusMessage(statusMessage)
                .showIcon(showIcon)
                .build();
    }

    @Override
    public Optional<AttendanceActivityDetailVO> getActivityDetail(Long currentPartyId, Long shareShowPartyId) {

        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDate nowDate = nowDateTime.toLocalDate();

        //默认展示开奖时间戳
        AttendanceH5StatusEnum attendanceStatus = AttendanceH5StatusEnum.SHOW_TIME_STAMP;
        String bonusAmout;
        String partakeCount;
        Long timestamps = 0L;
        String message = "";
        String successNum = "";
        String failNum = "";
        List<AttendanceStarVO> attendanceStars = Lists.newArrayList();

        Example example = new Example(ActivityPartake.class);
        example.and()
                .andEqualTo("userType", UserType.ATTENDANCE_USER.name())
                .andEqualTo("activityCode", ActivityEnum.ATTENDANCE_AWARD.name())
                .andCondition("(status = 'SIGN_UP' OR status = 'SIGN_IN' OR status = 'AWARD')")
                .andEqualTo("isDeleted", false)
                .andGreaterThanOrEqualTo("createDate", nowDate)
                .andLessThan("createDate", nowDate.plusDays(1));
        int count = activityPartakeRecordMapper.selectCountByExample(example);

        Integer tempCount = getInitSignInPeopleNum() + count;
        partakeCount = new DecimalFormat(",###").format(tempCount);
        bonusAmout = new DecimalFormat(",###").format(tempCount * ACTIVITY_BASIC_AWARD);
        attendanceStatus = userAttendanceStatus4Show(currentPartyId, nowDateTime);
        //计算时间戳
        LocalTime awardResultTime = getAwardResultTime();

        if (Objects.equals(AttendanceH5StatusEnum.SHOW_TIME_STAMP, attendanceStatus)) {
            LocalTime localTime = nowDateTime.toLocalTime();//现在时间
            LocalTime signInStartTime = getSignInStartTime();//打卡开始时间
            if (localTime.isBefore(signInStartTime)) { //如果要显示时间戳，且时间早于打卡开始时间，这说明是距离今天的打卡开始时间的时间戳
                timestamps = Duration.between(localTime, signInStartTime).getSeconds();
                //此时直接返回，显示时间戳
                message = String.format("每日%s点公布当日打卡之星敬请期待", awardResultTime.getHour());
                AttendanceActivityDetailVO buildVO = AttendanceActivityDetailVO.builder()
                        .shareTitle(ACT_SHARE_TITLE)
                        .shareImg(SHARE_IMG_URL)
                        .shareUrl(String.format(SHARE_URL, ConfigPropertyUtils.get("h5.server.url")))
                        .shareDesc(MessageFormat.format(SHARE_DESC, getSignInStartTime().getHour(), getSignInEndTime().getHour()))
                        .successNum(successNum)
                        .failNum(failNum)
                        .bonusAmout(bonusAmout)
                        .timestamps(timestamps)
                        .partakeCount(partakeCount)
                        .attendanceStatus(attendanceStatus)
                        .message(message)
                        .attendanceStars(attendanceStars)
                        .build();
                return Optional.of(buildVO);
            }
            LocalTime signInEndTime = getSignInEndTime();
            if (localTime.isAfter(signInEndTime)) {//如果要显示时间戳，且时间晚于打卡结束时间，这说明是距离今天的打卡开始时间的时间戳
                long oneDaySeconds = Duration.ofDays(1).getSeconds();
                long betweenSecondes = Duration.between(localTime, signInEndTime).getSeconds();
                timestamps = oneDaySeconds -
                        Math.abs(betweenSecondes)
                        - Math.abs(Duration.between(signInStartTime, signInEndTime).getSeconds());
            }
            if (localTime.isBefore(signInEndTime)) {//如果要显示时间戳，且时间晚于打卡结束时间，这说明是距离今天的打卡开始时间的时间戳
                long oneDaySeconds = Duration.ofDays(1).getSeconds();
                timestamps = oneDaySeconds - Math.abs(Duration.between(signInStartTime, localTime).getSeconds());
            }
        }
        long activityDays = attendanceUserAwardGenerateService.getActivityDays();
        if (activityDays > 1 && nowDateTime.toLocalTime().isBefore(awardResultTime)) {
            message = String.format("每日%s点公布当日打卡之星敬请期待", awardResultTime.getHour());
        } else {
            //失败人数
            example.clear();
            example.and()
                    .andEqualTo("userType", UserType.ATTENDANCE_USER.name())
                    .andEqualTo("activityCode", ActivityEnum.ATTENDANCE_AWARD.name())
                    .andEqualTo("status", CustomerAttendanceStatusEnum.SIGN_UP)
                    .andEqualTo("isDeleted", false)
                    .andGreaterThanOrEqualTo("createDate", nowDate.minusDays(1))
                    .andLessThan("createDate", nowDate);
            int failCount = activityPartakeRecordMapper.selectCountByExample(example);
            failNum = new DecimalFormat(",###").format(failCount);
            //成功人数
            example.clear();
            example.and()
                    .andEqualTo("userType", UserType.ATTENDANCE_USER.name())
                    .andEqualTo("activityCode", ActivityEnum.ATTENDANCE_AWARD.name())
                    .andEqualTo("status", CustomerAttendanceStatusEnum.AWARD)
                    .andEqualTo("isDeleted", false)
                    .andGreaterThanOrEqualTo("createDate", nowDate.minusDays(1))
                    .andLessThan("createDate", nowDate);
            int successCount = activityPartakeRecordMapper.selectCountByExample(example);
            successNum = new DecimalFormat(",###").format(successCount + getInitSignInPeopleNum());
            if (activityDays > 1) {  //上线第一天没有打卡之星
                ActivityPartake earliestStar = activityPartakeRecordMapper.getEarliestStar();
                if (earliestStar == null) {
                    attendanceStars.add(AttendanceStarVO.builder()
                            .headImg(attendanceConfiguration.getDefaultUserHeadimg())
                            .name("暂无数据")
                            .message("暂无数据")
                            .build());
                } else {
                    attendanceStars.add(AttendanceStarVO.builder()
                            .headImg(earliestStar.getHeadImageUrl())
                            .name(earliestStar.getUserName())
                            .message(earliestStar.getSignInDate().toLocalTime().format(ISO_LOCAL_TIME) + "打卡")
                            .build());
                }
                ActivityPartake biggestAwardStar = activityPartakeRecordMapper.getBiggestAwardStar();
                if (biggestAwardStar == null) {
                    attendanceStars.add(AttendanceStarVO.builder()
                            .headImg(attendanceConfiguration.getDefaultUserHeadimg())
                            .name("暂无数据")
                            .message("暂无数据")
                            .build());
                } else {
                    attendanceStars.add(AttendanceStarVO.builder()
                            .headImg(biggestAwardStar.getHeadImageUrl())
                            .name(biggestAwardStar.getUserName())
                            .message(biggestAwardStar.getPrizeValue().intValue() + "金币")
                            .build());
                }

                if (activityDays > 7) {
                    Map<String, Object> mostPersistStar = activityPartakeRecordMapper.getMostPersistStar();
                    if (mostPersistStar == null) {
                        attendanceStars.add(AttendanceStarVO.builder()
                                .headImg(attendanceConfiguration.getDefaultUserHeadimg())
                                .name("暂无数据")
                                .message("暂无数据")
                                .build());
                    } else {
                        attendanceStars.add(AttendanceStarVO.builder()
                                .headImg(((String) mostPersistStar.get("HEAD_IMAGE_URL")))
                                .name(((String) mostPersistStar.get("USER_NAME")))
                                .message(MessageFormat.format("连续{0}次", mostPersistStar.get("SIGN_IN_TIMES")))
                                .build());
                    }

                } else {
                    attendanceStars.add(AttendanceStarVO.builder()
                            .headImg(attendanceConfiguration.getDefaultUserHeadimg())
                            .name("暂无数据")
                            .message("暂无数据")
                            .build());
                }
            }
        }

        AttendanceActivityDetailVO.AttendanceActivityDetailVOBuilder builder = AttendanceActivityDetailVO.builder()
                .partyId(currentPartyId)
                .shareTitle(ACT_SHARE_TITLE)
                .shareImg(SHARE_IMG_URL)
                .shareUrl(String.format(SHARE_URL, ConfigPropertyUtils.get("h5.server.url")))
                .shareDesc(MessageFormat.format(SHARE_DESC, getSignInStartTime().getHour(), getSignInEndTime().getHour()))
                .successNum(successNum)
                .failNum(failNum)
                .bonusAmout(bonusAmout)
                .timestamps(timestamps)
                .signInTimeZone("(" + getSignInStartTimeString() + "-" + getSignInEndTimeString() + ")")
                .partakeCount(partakeCount)
                .attendanceStatus(attendanceStatus)
                .message(message)
                .attendanceStars(attendanceStars);
        //有party参数就返回战绩分享信息
        if (shareShowPartyId != null) {
            //战绩风险title
            Map<String, Object> signStatusSummary = activityPartakeRecordMapper.getSignStatusSummary(shareShowPartyId);
            String successDays = signStatusSummary.get("SUCCESS_DAYS").toString();
            String awardAmount = ((BigDecimal) signStatusSummary.get("AWARD_AMOUNT")).toBigInteger().toString();
            builder.partyId(shareShowPartyId)
                    .shareImgz(SHARE_IMG_URL)
                    .shareTitlez(MessageFormat.format(HISTORY_SHARE_MESSAGE, awardAmount, successDays))
                    .shareUrlz(String.format(SHARE_URL, ConfigPropertyUtils.get("h5.server.url")))
                    .shareDesc(MessageFormat.format(SHARE_DESC, getSignInStartTime().getHour(), getSignInEndTime().getHour()));
        }
        return Optional.of(builder.build());
    }

    /**
     * 活动状态
     *
     * @return
     */
    public AttendanceH5StatusEnum userAttendanceStatus4Show(Long currentPartyId, LocalDateTime nowDateTime) {

        LocalDate nowDate = nowDateTime.toLocalDate();
        LocalDate yesterdayDate = nowDateTime.minusDays(1).toLocalDate();
        if (currentPartyId == null) {
            //未登录显示报名打卡按钮
            return AttendanceH5StatusEnum.SHOW_SIGN_UP;
        }
        Example example = new Example(ActivityPartake.class);
        example.clear();
        example.setOrderByClause("CREATE_DATE DESC");
        example.and()
                .andEqualTo("partyId", currentPartyId)
                .andEqualTo("userType", UserType.ATTENDANCE_USER.name())
                .andEqualTo("activityCode", ActivityEnum.ATTENDANCE_AWARD.name())
                .andEqualTo("isDeleted", 0);
        PageRowBounds rowBounds = new PageRowBounds(0, 1); //查询最近一条打卡记录
        List<ActivityPartake> activityPartakes = activityPartakeRecordMapper.selectByExampleAndRowBounds(example, rowBounds);

        if (activityPartakes.isEmpty()) {
            //没有相关记录，则显示参与打卡按钮，跟AWARD类型场景一致，借用该类型
            return AttendanceH5StatusEnum.SHOW_SIGN_UP;
        }
        ActivityPartake activityPartake = activityPartakes.get(0);
        String status = activityPartake.getStatus();
        CustomerAttendanceStatusEnum customerAttendanceStatus = CustomerAttendanceStatusEnum.valueOf(status);
        LocalDateTime createDateTime = activityPartake.getCreateDate();

        //最新一条数据的时间超过了昨天，则说明打卡不连续，已经断开
        if (createDateTime.toLocalDate().compareTo(yesterdayDate) < 0) {
            return AttendanceH5StatusEnum.SHOW_SIGN_UP;
        }
        //最新是今天创建的记录，那肯定是报名待打卡
        if (createDateTime.toLocalDate().equals(nowDate)) {
            return AttendanceH5StatusEnum.SHOW_TIME_STAMP;
        }
        //最新是昨天创建的记录，有两种情况
        if (createDateTime.toLocalDate().equals(yesterdayDate)) {
            switch (customerAttendanceStatus) {
                case SIGN_UP: //获取毫秒数
                    if (nowDateTime.toLocalTime().isBefore(getSignInStartTime())) {
                        return AttendanceH5StatusEnum.SHOW_TIME_STAMP;
                    }
                    if (nowDateTime.toLocalTime().isBefore(getSignInEndTime()) && nowDateTime.toLocalTime().isAfter(getSignInStartTime())) {
                        return AttendanceH5StatusEnum.SHOW_SIGN_IN;
                    }
                    if (nowDateTime.toLocalTime().isAfter(getSignInEndTime())) {
                        return AttendanceH5StatusEnum.SHOW_SIGN_UP;
                    }
                case SIGN_IN: //已经完成打卡，只可能是昨天和昨天之前的记录
                    return AttendanceH5StatusEnum.SHOW_SIGN_UP;
                case AWARD: //已经发放奖励，只可能是昨天和昨天之前的记录
                    return AttendanceH5StatusEnum.SHOW_SIGN_UP;
            }
        }
        throw new BizException(String.format("无法获取用户打卡状态，存在超过当前时间的记录: %s", currentPartyId));
    }

    /**
     * 活动是否正在进行
     *
     * @param actity
     * @return
     */
    @Override
    public void isRunning(ActivityEnum actity) {

        Activity targetAct = activityMapper.selectActivity(actity.name());
        if (targetAct == null) {
            throw new ActivityExpireException("活动不存在");
        }
        Date startDate = targetAct.getStartDate();
        Date expireDate = targetAct.getExpireDate();
        Preconditions.checkState(startDate != null, "数据异常：活动开始时间不能为空");
        Date now = new Date();
        boolean isBegin = now.after(startDate);
        if (!isBegin) {
            throw new ActivityExpireException("活动尚未开始");
        }
        if (expireDate != null) {
            if (now.after(expireDate)) {
                throw new ActivityExpireException("活动已结束");
            }
        }
    }

    /**
     * 报名打卡赢金币活动
     *
     * @param partyId
     * @param signUpFee
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<Boolean> signUpActivity(Long partyId, Integer signUpFee) {

        RLock lock = redissonClient.getLock(ACT_SIGN_UP_LOCK_FLAG + partyId);
        if (!lock.tryLock()) {
            throw new BizException(ActPreconditions.ResponseEnum.REPEAT);
        }

        try {
            LocalDate from = LocalDate.now();
            LocalDate to = from.plusDays(1);
            boolean flag = userStatusCheckService.hasSignUp(partyId, from, to);
            if (flag) {
                throw new ActivityOperationFailException("已参与，请勿重复点击");
            }
            //检查是否是活动结束日期当天
            checkIfExpireSoon(from);
            //冗余用户数据
            Response<UserVo> appUserResp = personalServiceClient.queryUser(partyId);
            com.google.common.base.Optional<UserVo> userVoOptional = Response.checkResponse(appUserResp);
            UserVo appUser = userVoOptional.or(() -> {
                throw new IllegalArgumentException("app用户查询失败");
            });
            boolean insertFlag = userStatusCheckService.signUp(appUser, signUpFee);
            if (!insertFlag) {
                throw new ActivityOperationFailException("活动参与失败");
            }
            //扣报名费
            systemGoldCoinService.checkBalance(partyId, signUpFee); //检查余额
            Optional<Boolean> chargeFeeFlag = systemGoldCoinService.chargeCoin2System(partyId, signUpFee);
            chargeFeeFlag.orElseThrow(() -> new ActivityOperationFailException("异常：扣取报名费失败"));
            chargeFeeFlag.ifPresent(input -> {
                if (!input) {
                    throw new ActivityOperationFailException("异常：扣取报名费失败");
                }
            });
            return Optional.of(true);
        } finally {
            lock.unlock();
        }
    }

    private void checkIfExpireSoon(LocalDate from) {

        Activity activity = activityMapper.selectActivity(THIS_ACTITY.name());
        if (activity == null) {
            throw new ActivityExpireException("活动不存在");
        }
        Date expireDate = activity.getExpireDate();
        if (expireDate != null) {
            Instant instant = expireDate.toInstant();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            //如果是活动结束前一天，则不允许报名打卡，活动结束时间的定义为当天的23分59分59秒，落实到数据库中，则为结束日期加一天
            LocalDate expireLocalDate = localDateTime.toLocalDate().minusDays(1);
            if (expireLocalDate.equals(from)) {
                throw new ActivityOperationFailException("活动已结束");
            }
        }
    }

    /**
     * 打卡
     *
     * @param currentPartyId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<Boolean> finishActivity(Long currentPartyId) {

        RLock lock = redissonClient.getLock(ACT_SIGN_IN_LOCK_FLAG + currentPartyId);
        if (!lock.tryLock()) {
            throw new BizException(ActPreconditions.ResponseEnum.REPEAT);
        }

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalTime localTime = now.toLocalTime();
            if (localTime.isAfter(getSignInEndTime()) || localTime.isBefore(getSignInStartTime())) {
                throw new BizException("不在打卡时间范围内");
            }
            userStatusCheckService.hasSignInCondition(currentPartyId);
            boolean keepContinuousSignInFlag = userStatusCheckService.keepContinuousSignIn(currentPartyId, now);
            boolean signInFlag = userStatusCheckService.signIn(currentPartyId, now);
            if (!(keepContinuousSignInFlag && signInFlag)) {
                throw new ActivityOperationFailException("错误：打卡失败");
            }
            return Optional.of(true);
        } finally {
            lock.unlock();
        }
    }

    public LocalTime getSignInStartTime() {

        String signInStartTime = attendanceConfiguration.getSignInStartTime();
        return LocalTime.parse(signInStartTime, DateTimeFormatter.ofPattern("H:m"));
    }


    public LocalTime getSignInEndTime() {

        String signInEndTime = attendanceConfiguration.getSignInEndTime();
        return LocalTime.parse(signInEndTime, DateTimeFormatter.ofPattern("H:m"));
    }

    public String getSignInStartTimeString() {

        return attendanceConfiguration.getSignInStartTime();
    }


    public String getSignInEndTimeString() {

        return attendanceConfiguration.getSignInEndTime();
    }


    public LocalTime getAwardResultTime() {

        String awardResultTime = attendanceConfiguration.getAwardResultTime();
        return LocalTime.parse(awardResultTime, DateTimeFormatter.ofPattern("H:m"));
    }

    public int getInitSignInPeopleNum() {

        String initSignInPeopleNum = attendanceConfiguration.getInitSignInPeopleNum();
        return Integer.valueOf(initSignInPeopleNum);
    }


}
