package com.xianglin.act.web.home.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.xianglin.act.biz.shared.UserAttendanceActivityService;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.act.web.home.base.BaseController;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.act.web.home.util.SessionCookieHelper;
import com.xianglin.core.model.enums.ActivityEnum;
import com.xianglin.act.common.util.BizException;
import com.xianglin.core.model.exception.attendance.ActivityNotExistException;
import com.xianglin.core.model.exception.attendance.ActivityOperationFailException;
import com.xianglin.core.model.exception.attendance.AttendanceBaseException;
import com.xianglin.core.model.vo.AttendanceActivityDetailVO;
import com.xianglin.core.model.vo.UserAttendanceDetailVO;
import com.xianglin.core.service.SystemGoldCoinService;
import com.xianglin.fala.session.RedisSessionRepository;
import com.xianglin.fala.session.Session;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/17 17:55.
 */
@RestController
@RequestMapping("/act/api/activity/attendance")
@Api(value = "/act/api/activity/attendance", tags = "打卡赢金币活动接口")
@ApiResponses({@ApiResponse(code = 2000, message = "调用成功"),
        @ApiResponse(code = 3000, message = "业务异常"),
        @ApiResponse(code = 4000, message = "未知错误，系统异常")})
public class UserAttendanceActivityController extends BaseController {

    private static final String PARTY_ID = "partyId";

    /**
     * sessionCookieHelper
     */
    @Autowired
    private SessionCookieHelper sessionCookieHelper;

    /*v
     * 活动类型
     */
    private static final ActivityEnum THIS_ACTITY = ActivityEnum.ATTENDANCE_AWARD;

    @Autowired
    private UserAttendanceActivityService attendanceActivityService;

    /**
     * sessionRepository
     */
    @Autowired
    @Qualifier("sessionRepository")
    private RedisSessionRepository sessionRepository;

    @Autowired
    private SystemGoldCoinService systemGoldCoinService;

    /**
     * 检查活动是否已过期
     */
    @ModelAttribute
    public void checkIfExpire() {

        attendanceActivityService.isRunning(THIS_ACTITY);
    }

    @PostMapping("detail")
    @ApiOperation(value = "活动详情", notes = "用户展示活动的实时详情", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<AttendanceActivityDetailVO> detail(HttpServletRequest request, @RequestParam(value = "partyId", required = false) Long partyId) {

        String[] sessionIds = sessionCookieHelper.getSessionIds(request);
        if (sessionIds == null) {
            sessionIds = new String[0];
        }
        Session session = null;
        for (String sessionId : sessionIds) {
            session = getSession(request, sessionId);
            if (session != null) {
                break;
            }
        }
        Long currentPartyId = null;
        if (session != null) {
            currentPartyId = session.getAttribute(PARTY_ID, Long.class);
        }
        Optional<AttendanceActivityDetailVO> result = attendanceActivityService.getActivityDetail(currentPartyId, partyId);
        logExecuteTime();
        return Response.ofSuccess(result.orElseThrow(() -> new ActivityNotExistException("活动详情查询出错")));

    }

    @PostMapping("sign-up")
    @ApiOperation(value = "报名打卡赢金币活动", notes = "参与活动按钮请求地址", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> signUp(@RequestParam(value = "fee", defaultValue = "100") Integer signUpFee) {

        Preconditions.checkArgument(signUpFee >= 0, "无效的参数：报名费参数不合法");
        Long currentPartyId = getCurrentPartyId();
        Optional<Boolean> resultOptional = attendanceActivityService.signUpActivity(currentPartyId, signUpFee);
        Boolean flag = resultOptional.orElse(false);
        if (!flag) {
            throw new ActivityOperationFailException("报名失败");
        }
        logExecuteTime();
        return Response.ofSuccess(true);
    }

    @PostMapping("check-balance")
    @ApiOperation(value = "每日打卡", notes = "检查账户金币余额", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> checkBalance(@RequestParam(value = "fee", defaultValue = "100") Integer signUpFee) {

        Long currentPartyId = getCurrentPartyId();
        Optional<Boolean> resultOptional = null;
        try {
            resultOptional = systemGoldCoinService.checkBalance(currentPartyId, signUpFee);
        } catch (Exception e) {
            String stackTrace = Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining(System.lineSeparator()));
            logger.warn("===========[[ 异常:{}, msg -> {} ]]===========\r\n {}", getControllerDescription(),  e.getMessage(), stackTrace);
            resultOptional = Optional.of(false);
        }        Boolean flag = resultOptional.orElse(false);
        logExecuteTime();
        return Response.ofSuccess(flag);
    }

    @PostMapping("sign-in")
    @ApiOperation(value = "每日打卡", notes = "早上打卡动作的请求地址", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> signIn() {

        Long currentPartyId = getCurrentPartyId();
        Optional<Boolean> resultOptional = attendanceActivityService.finishActivity(currentPartyId);
        Boolean flag = resultOptional.orElse(false);
        if (!flag) {
            throw new ActivityOperationFailException("签到失败");
        }
        logExecuteTime();
        return Response.ofSuccess(true);
    }

    @PostMapping("my-history")
    @ApiOperation(value = "我的战绩", notes = "展示我的战绩记录", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<UserAttendanceDetailVO> history() {

        Long currentPartyId = getCurrentPartyId();
        Optional<UserAttendanceDetailVO> resultOptional = attendanceActivityService.getUserAttendanceHistory(currentPartyId);
        logExecuteTime();
        return Response.ofSuccess(resultOptional.orElseThrow(() -> new ActivityNotExistException("战绩详情查询出错")));
    }

    @Override
    protected boolean shouldLogSession() {

        return true;
    }

    @Override
    protected String getControllerDescription() {

        return "打卡赢金币活动";
    }

    @Override
    protected Response handleCustomizeException(Exception e, long executeTime, String uri, Map<String, String[]>
            parameterMap) {

        String stackTrace = Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining(System.lineSeparator()));
        if (e instanceof AttendanceBaseException) {
            logger.warn("===========[[ 异常:{} 请求uri -> {}, 执行时间 -> {}ms, 参数 -> {}, msg -> {} ]]===========\r\n {}", getControllerDescription(), uri, executeTime, JSON.toJSONString(parameterMap, true), e.getMessage(), stackTrace);
            return Response.ofFail(e.getMessage());
        }
        if (e instanceof BizException) {
            logger.warn("===========[[ 异常:{} 请求uri -> {}, 执行时间 -> {}ms, 参数 -> {}, msg -> {} ]]===========\r\n {}", getControllerDescription(), uri, executeTime, JSON.toJSONString(parameterMap, true), e.getMessage(), stackTrace);
            Response response = new Response();
            ActPreconditions.ResponseEnum responseEnum = ((BizException) e).getResponseEnum();
            if (responseEnum == null) {
                response.setMessage(e.getMessage());
            } else {
                response.setResponseEnum(responseEnum);
            }
            return response;
        }
        return null;
    }

    private Long getCurrentPartyId() {

        Session session = GlobalRequestContext.getSession();
        return session.getAttribute(PARTY_ID, Long.class);
    }

    private Session getSession(HttpServletRequest request, String sessionId) {

        Session session = null;
        if (StringUtils.isEmpty(sessionId) || (session = sessionRepository.getSession(sessionId)) == null) {
            return null;
        }
        return session;
    }
}
