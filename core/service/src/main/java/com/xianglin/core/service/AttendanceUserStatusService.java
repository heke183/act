package com.xianglin.core.service;

import com.xianglin.act.common.dal.model.ActivityPartake;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yungyu
 */

public interface AttendanceUserStatusService {

    /**
     * 是否已经报名参与
     *
     * @param partyId
     * @return
     */
    boolean hasSignUp(Long partyId, LocalDate from, LocalDate to);

    /**
     * 是否已经打过卡
     *
     * @param partyId
     * @return
     */
    boolean hasSignInCondition(Long partyId);

    /**
     * 打卡
     *
     * @param currentPartyId
     * @param now
     * @return
     */
    boolean signIn(Long currentPartyId, LocalDateTime now);

    /**
     * 报名参与
     *
     * @return
     */
    boolean signUp(UserVo appUser, Integer signUpFee);

    /**
     * 维护用户连续打卡天数的状态
     *
     * @param currentPartyId
     * @param now
     * @return
     */
    boolean keepContinuousSignIn(Long currentPartyId, LocalDateTime now);

    /**
     * 获取用户头像
     *
     * @param appUser
     * @return
     */
    String getHeadImg(UserVo appUser);

    /**
     * 获取用户名
     *
     * @param appUser
     * @return
     */
    String getUserName(UserVo appUser);

    /** 查询签到记录
     * @param partyId
     * @param from
     * @param to
     * @return
     */
    List<ActivityPartake> querysignUp(Long partyId, LocalDate from, LocalDate to);

}
