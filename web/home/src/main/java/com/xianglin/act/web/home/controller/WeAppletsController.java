package com.xianglin.act.web.home.controller;

import com.xianglin.act.biz.shared.ActGroupSharedService;
import com.xianglin.act.common.util.WxAppletApiUtils;
import com.xianglin.act.web.home.intercepter.SessionInterceptor;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.core.model.vo.UserAddressVo;
import com.xianglin.fala.session.Session;
import com.xianglin.fala.session.SessionRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 微信小程序公共服务
 *
 * @author wanglei
 * @date
 */
@RestController
@RequestMapping("/act/api/weixin/applet")
@Api(value = "/act/api/weixin/applet", tags = "微信小程序")
@ApiResponses({@ApiResponse(code = 2000, message = "调用成功"),
        @ApiResponse(code = 3000, message = "业务异常"),
        @ApiResponse(code = 4000, message = "未知错误，系统异常")})
@Controller
public class WeAppletsController {

    @Autowired
    private WxAppletApiUtils wxAppletApiUtils;

    /**
     * sessionRepository
     */

    @Autowired
    private ActGroupSharedService actGroupSharedService;

    /**
     * sessionRepository
     */
    @Resource
    private SessionRepository<Session> sessionRepository;


    @PostMapping("/code")
    @ApiOperation(value = "通过code换取sessionId")
    @SessionInterceptor.IntercepterIngore
    public Response<String> code(String code,@RequestHeader("sessionid") String sessionid) {
        Response<String> response = new Response<>();
        response.setResult(wxAppletApiUtils.getSessionId(code,sessionid));
        return response;
    }

    @PostMapping("/userInfo")
    @ApiOperation(value = "获取用户头像，名字信息")
    @SessionInterceptor.IntercepterIngore
    public Response<String> userInfo(@RequestParam Map<String, String> paraMap) {
        Response<String> response = new Response<>();
        response.setResult(wxAppletApiUtils.getUserInfo(paraMap));
        return response;
    }

    @ApiOperation(value = "获取微信手机号并注册")
    @PostMapping("/mobileInfo")
    @SessionInterceptor.IntercepterIngore
    public Response<Long> mobileInfo(String sessionId, String encryptedData, String iv) {
        Map<String, String> resultMap = wxAppletApiUtils.getAllInfo(sessionId, encryptedData, iv);
        Long partyId = actGroupSharedService.wxAppletLogin(resultMap);
        Session session = sessionRepository.createSession(sessionId);
        session.setAttribute("partyId",partyId);
        sessionRepository.save(session);
        return Response.ofSuccess(partyId);
    }
}
