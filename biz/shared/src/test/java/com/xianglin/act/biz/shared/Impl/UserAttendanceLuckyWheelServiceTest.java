package com.xianglin.act.biz.shared.Impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.xianglin.act.biz.shared.UserAttendanceActivityService;
import com.xianglin.act.common.service.integration.PersonalServiceClient;
import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import com.xianglin.core.model.enums.ActivityEnum;
import com.xianglin.core.model.vo.AttendanceActivityDetailVO;
import com.xianglin.core.model.vo.UserAttendanceDetailVO;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/20 14:28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/*.xml")
public class UserAttendanceLuckyWheelServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserAttendanceLuckyWheelServiceTest.class);

    private static final long CURRENT_PARTY_ID = 1000000000002542L;

    private static final int SIGN_UP_FEE = 100;

    @Autowired
    private CuratorFramework curatorFramework;

    @Autowired
    private UserAttendanceActivityService attendanceActivityService;

    @Autowired
    private PersonalServiceClient personalServiceClient;

    @Test
    public void isRunning() {

        attendanceActivityService.isRunning(ActivityEnum.ATTENDANCE_AWARD);
    }

    @Test
    public void signUpActivity() {

        Arrays.stream(new Long[]{7000174L
                , 5190133L
                , 7005850L
                , 7000234L
                , 7003658L
                , 11000795L
                , 5500252L
                , 7001061L
                , 1000035501L
                , 5199881L
                , 5199889L
                , 1000000000001411L
                , 1000000000001905L
                , 1000000000001906L
                , 1000000000001911L
                , 1000000000001912L
                , 1000000000001913L
                , 11000963L
                , 1000000000001914L
                , 1000000000001916L
                , 1000000000001917L
                , 10003582L
                , 1000000000001960L
                , 1000000000001962L
                , 1000000000001963L
                , 1000000000001964L
                , 1000000000001987L
                , 1000035223L
                , 1000000000001999L
                , 1000000000002000L
                , 1000000000002001L
                , 1000000000002002L
                , 5199886L
                , 1000000000002047L
                , 666666671416L
                , 1000000000002058L
                , 1000000000002078L
                , 1000000000002085L
        })
                .forEach(input -> {
                            Response<UserVo> userVoResponse = personalServiceClient.queryUser(input);
                            if (userVoResponse.isSuccess()) {
                                UserVo result = userVoResponse.getResult();
                                try {
                                    Optional<Boolean> aBoolean = attendanceActivityService.signUpActivity(result.getPartyId(), SIGN_UP_FEE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }

    @Test
    public void finishActivity() {

        Arrays.stream(new Long[]{7000174L
                , 5190133L
                , 7005850L
                , 7000234L
                , 7003658L
                , 11000795L
                , 5500252L
                , 7001061L
                , 1000035501L
                , 5199881L
                , 5199889L
                , 1000000000001411L
                , 1000000000001905L
                , 1000000000001906L
                , 1000000000001911L
                , 1000000000001912L
                , 1000000000001913L
                , 11000963L
                , 1000000000001914L
                , 1000000000001916L
                , 1000000000001917L
                , 10003582L
                , 1000000000001960L
                , 1000000000001962L
                , 1000000000001963L
                , 1000000000001964L
                , 1000000000001987L
                , 1000035223L
                , 1000000000001999L
                , 1000000000002000L
                , 1000000000002001L
                , 1000000000002002L
                , 5199886L
                , 1000000000002047L
                , 666666671416L
                , 1000000000002058L
                , 1000000000002078L
                , 1000000000002085L
        })
                .forEach(input -> {
                            Response<UserVo> userVoResponse = personalServiceClient.queryUser(input);
                            if (userVoResponse.isSuccess()) {
                                UserVo result = userVoResponse.getResult();
                                try {
                                    Optional<Boolean> aBoolean = attendanceActivityService.finishActivity(result.getPartyId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }

    @Test
    public void getActivityDetail() {

        Optional<AttendanceActivityDetailVO> activityDetail = attendanceActivityService.getActivityDetail(null, null);
        logger.info("===========[[ {} ]]===========", JSON.toJSONString(activityDetail.get(), true));
        assertNotNull(activityDetail.get());
    }

    @Test
    public void getUserAttendanceHistory() {

        Optional<UserAttendanceDetailVO> userAttendanceHistory = attendanceActivityService.getUserAttendanceHistory(CURRENT_PARTY_ID);
        assertNotNull(userAttendanceHistory.get());
    }

    @Test
    public void testTime() throws Exception {

        List<String> leader_select = curatorFramework.getChildren().forPath("/LEADER_SELECT");
        logger.info("===========[[ {} ]]===========", leader_select);
    }


    @Test
    public void testUserName() {

        Response<UserVo> appUserResp = personalServiceClient.queryUser(1000076245L);
        com.google.common.base.Optional<UserVo> userVoOptional = Response.checkResponse(appUserResp);
        System.out.println(getUserName(userVoOptional.get()));
    }

    private String getUserName(UserVo appUser) {

        if (!Strings.isNullOrEmpty(appUser.getNikerName())) {
            return appUser.getNikerName();
        }
        if (!Strings.isNullOrEmpty(appUser.getTrueName())) {
            return appUser.getTrueName();
        }
        String loginName = appUser.getLoginName();
        if (!Strings.isNullOrEmpty(loginName) && loginName.length() >= 11) {
            return loginName.substring(0, 3).concat("* ***").concat(loginName.substring(loginName.length() - 4));
        }
        return loginName;
    }
}