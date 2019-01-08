package com.xianglin.core.service.impl;

import com.google.common.base.Strings;
import com.xianglin.act.common.dal.mappers.ActivityPartakeMapper;
import com.xianglin.act.common.dal.mappers.ExtendPropMapper;
import com.xianglin.act.common.dal.model.ActivityPartake;
import com.xianglin.act.common.dal.model.ExtendProp;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.config.db.AttendanceConfiguration;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import com.xianglin.core.model.enums.ActivityEnum;
import com.xianglin.core.model.enums.CustomerAttendanceStatusEnum;
import com.xianglin.core.model.enums.UserType;
import com.xianglin.act.common.util.BizException;
import com.xianglin.core.model.exception.attendance.AppUserNotExistException;
import com.xianglin.core.model.exception.attendance.HasSignInTodayException;
import com.xianglin.core.model.exception.attendance.NotSignUpYesterdayException;
import com.xianglin.core.service.AttendanceUserStatusService;
import com.xianglin.core.service.constant.ExtendPropKeyConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/19 12:14.
 */
@Service
public class AttendanceUserStatusServiceImpl implements AttendanceUserStatusService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceUserStatusServiceImpl.class);

    @Autowired
    private ActivityPartakeMapper activityPartakeRecordMapper;

    @Autowired
    private ExtendPropMapper extendPropMapper;

    @Autowired
    private AttendanceConfiguration attendanceConfiguration;


    public boolean signUp(UserVo appUser, Integer signUpFee) {

        if (appUser == null) {
            throw new AppUserNotExistException("用户不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        ActivityPartake activityPartake = new ActivityPartake();
        activityPartake.setPartyId(appUser.getPartyId());
        activityPartake.setUserName(getUserName(appUser));
        activityPartake.setUserType(UserType.ATTENDANCE_USER.name());
        activityPartake.setActivityCode(ActivityEnum.ATTENDANCE_AWARD.name());
        activityPartake.setHeadImageUrl(getHeadImg(appUser));
        activityPartake.setMobilePhone(appUser.getLoginName());
        activityPartake.setStatus(CustomerAttendanceStatusEnum.SIGN_UP.name());
        activityPartake.setActivityFee(new BigDecimal(signUpFee));
        activityPartake.setUpdateDate(now);
        activityPartake.setCreateDate(now);
        activityPartake.setUpdater("system");
        activityPartake.setCreator("system");
        activityPartake.setComments("打卡赢金币");
        activityPartake.setIsDeleted(false);
        //插入打卡记录
        return activityPartakeRecordMapper.insert(activityPartake) != 0;
    }

    public String getHeadImg(UserVo appUser) {

        if (!Strings.isNullOrEmpty(appUser.getHeadImg())) {
            return appUser.getHeadImg();
        }
        return attendanceConfiguration.getDefaultUserHeadimg();
    }

    public String getUserName(UserVo appUser) {

        if (!Strings.isNullOrEmpty(appUser.getNikerName())) {
            return appUser.getNikerName();
        }
        if (!Strings.isNullOrEmpty(appUser.getTrueName())) {
            return appUser.getTrueName();
        }
        String loginName = appUser.getLoginName();
        if (!Strings.isNullOrEmpty(loginName) && loginName.length() >= 11) {
            return loginName.substring(0, 3).concat("****").concat(loginName.substring(loginName.length() - 4));
        }
        return loginName;
    }

    @Override
    public List<ActivityPartake> querysignUp(Long partyId, LocalDate from, LocalDate to) {
        Example example = new Example(ActivityPartake.class);
        example.and()
                .andEqualTo("partyId", partyId)
                .andEqualTo("userType", UserType.ATTENDANCE_USER.name())
                .andEqualTo("activityCode", ActivityEnum.ATTENDANCE_AWARD.name())
                //.andEqualTo("status", CustomerAttendanceStatusEnum.SIGN_UP.name())
                .andEqualTo("isDeleted", 0)
                .andGreaterThanOrEqualTo("createDate", from)
                .andLessThan("createDate", to);
        return activityPartakeRecordMapper.selectByExample(example);
    }

    @Override
    public boolean hasSignUp(Long partyId, LocalDate from, LocalDate to) {

        Example example = new Example(ActivityPartake.class);
        example.and()
                .andEqualTo("partyId", partyId)
                .andEqualTo("userType", UserType.ATTENDANCE_USER.name())
                .andEqualTo("activityCode", ActivityEnum.ATTENDANCE_AWARD.name())
                //.andEqualTo("status", CustomerAttendanceStatusEnum.SIGN_UP.name())
                .andEqualTo("isDeleted", 0)
                .andGreaterThanOrEqualTo("createDate", from)
                .andLessThan("createDate", to);
        List<ActivityPartake> activityPartakes = activityPartakeRecordMapper.selectByExample(example);
        return !activityPartakes.isEmpty();
    }

    @Override
    public boolean hasSignInCondition(Long partyId) {

        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(1);
        Example example = new Example(ActivityPartake.class);
        example.and()
                .andEqualTo("partyId", partyId)
                .andEqualTo("userType", UserType.ATTENDANCE_USER.name())
                .andEqualTo("activityCode", ActivityEnum.ATTENDANCE_AWARD.name())
                .andEqualTo("isDeleted", 0)
                .andGreaterThanOrEqualTo("createDate", from)
                .andLessThan("createDate", to);
        List<ActivityPartake> activityPartakes = activityPartakeRecordMapper.selectByExample(example);

        if (activityPartakes.isEmpty()) {
            logger.info("===========用户昨天未参与活动，无法打卡-》 partyId:[[ {} ]]===========", partyId);
            throw new NotSignUpYesterdayException("异常：未参与活动，无法打卡");
        }
        ActivityPartake activityPartake = activityPartakes.get(0);
        if (CustomerAttendanceStatusEnum.SIGN_IN.name().equals(activityPartake.getStatus())) {
            logger.info("===========用户今日已打卡，请勿重复打卡 -》 partyId:[[ {} ]]===========", partyId);
            throw new HasSignInTodayException("请勿重复打卡");
        }
        if (CustomerAttendanceStatusEnum.AWARD.name().equals(activityPartake.getStatus())) {
            logger.info("===========用户今日已打卡，请勿重复打卡 -》 partyId:[[ {} ]]===========", partyId);
            throw new HasSignInTodayException("请勿重复打卡");
        }
        if (CustomerAttendanceStatusEnum.SIGN_UP.name().equals(activityPartake.getStatus())) {
            return true;
        }
        throw new BizException(ActPreconditions.ResponseEnum.UNEXCEPT_STATUS);
    }

    public boolean signIn(Long currentPartyId, LocalDateTime now) {

        Example example = new Example(ActivityPartake.class);
        LocalDate nowDate = now.toLocalDate();
        example.and()
                .andEqualTo("partyId", currentPartyId)
                .andEqualTo("userType", UserType.ATTENDANCE_USER.name())
                .andEqualTo("activityCode", ActivityEnum.ATTENDANCE_AWARD.name())
                .andEqualTo("status", CustomerAttendanceStatusEnum.SIGN_UP)
                .andEqualTo("isDeleted", false)
                .andGreaterThanOrEqualTo("createDate", nowDate.minusDays(1))
                .andLessThan("createDate", nowDate);
        ActivityPartake activityPartake = new ActivityPartake();
        activityPartake.setStatus(CustomerAttendanceStatusEnum.SIGN_IN.name());
        activityPartake.setUpdateDate(now);
        activityPartake.setSignInDate(now);
        return activityPartakeRecordMapper.updateByExampleSelective(activityPartake, example) == 1;
    }

    @Override
    public boolean keepContinuousSignIn(Long currentPartyId, LocalDateTime now) {

        ExtendProp record = new ExtendProp();
        record.setRelationId(currentPartyId);
        record.setIsDeleted("0");
        record.setPropType(ActivityEnum.ATTENDANCE_AWARD.name());
        record.setPropKey(ExtendPropKeyConstant.PROP_KEY);
        List<ExtendProp> extendPropList = extendPropMapper.select(record);
        if (extendPropList.isEmpty()) {
            record.setPropValue("1");
            record.setUpdateDate(now);
            record.setCreateDate(now);
            record.setCreator("system");
            record.setUpdater("system");
            return extendPropMapper.insertSelective(record) == 1;
        } else {
            ExtendProp extendProp = extendPropList.get(0);
            LocalDate nowDate = LocalDate.now();
            LocalDate yesterDayDate = nowDate.minusDays(1);
            LocalDateTime updateDate = extendProp.getUpdateDate();
            LocalDate updateLocalDate = updateDate.toLocalDate();
            if (updateLocalDate.compareTo(yesterDayDate) == 0) {
                return extendPropMapper.increaseSignInTimes(currentPartyId) == 1;
            } else {
                return extendPropMapper.resetSignInTimes(currentPartyId) == 1;
            }
        }
    }
}
