package com.xianglin.act.biz.shared;

import com.xianglin.core.model.enums.ActivityEnum;
import com.xianglin.core.model.vo.AttendanceActivityDetailVO;
import com.xianglin.core.model.vo.UserAttendanceDetailVO;

import java.util.Optional;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/18 10:32.
 */
public interface UserAttendanceActivityService {

    void isRunning(ActivityEnum actity);

    Optional<AttendanceActivityDetailVO> getActivityDetail(Long currentPartyId, Long shareShowPartyId);

    Optional<Boolean> signUpActivity(Long currentPartyId, Integer signUpFee);

    Optional<Boolean> finishActivity(Long currentPartyId);

    Optional<UserAttendanceDetailVO> getUserAttendanceHistory(Long currentPartyId);
}
