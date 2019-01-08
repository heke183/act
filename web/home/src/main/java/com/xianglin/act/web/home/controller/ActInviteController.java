package com.xianglin.act.web.home.controller;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.biz.shared.ActInviteSharedService;
import com.xianglin.act.biz.shared.ActPlantSharedService;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.act.web.home.intercepter.SessionInterceptor;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.core.model.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author jiang yong tao
 * @date 2018/8/23  15:25
 */
@RestController
@RequestMapping("/act/api/actInvite")
@Api(value = "/act/api/actInvite", tags = "好友争霸活动")
@ApiResponses({@ApiResponse(code = 2000, message = "调用成功"),
        @ApiResponse(code = 3000, message = "业务异常"),
        @ApiResponse(code = 4000, message = "未知错误，系统异常")})
public class ActInviteController {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ActInviteController.class);

    @Autowired
    private ActInviteSharedService actInviteSharedService;
    @Autowired
    private ActPlantSharedService actPlantSharedService;

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/homePageInfo")
    @ApiOperation("好友争霸主页信息(一期)")
    public Response<ActInviteHomePageVo> homePageInfo() throws Exception{
        return doResponse(()->{
            ActInviteHomePageVo actInviteHomePageVo = actInviteSharedService.homePageInfo(GlobalRequestContext.currentPartyId());
            return actInviteHomePageVo;
        });
    }

    @PostMapping("/userApply")
    @ApiOperation("好友争霸用户报名")
    public Response<Void> userApply(ActInviteVo actInviteVo) throws Exception{
        return doResponse(()->{
            actInviteSharedService.userApply(actInviteVo,GlobalRequestContext.currentPartyId());
            return null;
        });
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/actInviteShare")
    @ApiOperation("好友争霸主页分享")
    public Response<ActShareVo> actInviteShare() throws Exception{
        return doResponse(()->{
            ActShareVo actShareVo = actInviteSharedService.actShareInfo();
            return actShareVo;
        });
    }

    @PostMapping("/selectByPartyId")
    @ApiOperation("好友争霸用户确认报名信息")
    public Response<ActInviteVo> selectByPartyId() throws Exception{
        return doResponse(()->{
            ActInviteVo actInviteVo = actInviteSharedService.selectByPartyId(GlobalRequestContext.currentPartyId());
            return actInviteVo;
        });
    }

    @PostMapping("/userCertification")
    @ApiOperation("好友争霸用户实名认证查询")
    public Response<Boolean> userCertification() throws Exception{
        return doResponse(()->{
            Boolean userCertification = actPlantSharedService.userCertification(GlobalRequestContext.currentPartyId());
            return userCertification;
        });
    }

    @PostMapping("/myApplyInfo")
    @ApiOperation("好友争霸用户报名详情")
    public Response<ActInviteVo> myApplyInfo() throws Exception{
        return doResponse(()->{
            ActInviteVo actInviteVo = actInviteSharedService.selectApplyInfo(GlobalRequestContext.currentPartyId());
            return actInviteVo;
        });
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/homePageInfoTwo")
    @ApiOperation("好友争霸主页信息(二期)")
    public Response<ActInviteHomePageVo> homePageInfoTwo() throws Exception{
        return doResponse(()->{
            ActInviteHomePageVo actInviteHomePageVo = actInviteSharedService.homePageInfoTwo(GlobalRequestContext.currentPartyId());
            return actInviteHomePageVo;
        });
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/actShareInfoTwo")
    @ApiOperation("好友争霸分享信息(二期)")
    public Response<ActShareVo> actShareInfoTwo() throws Exception{
        return doResponse(()->{
            ActShareVo actShareVo = actInviteSharedService.actShareInfoTwo();
            return actShareVo;
        });
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/selectRule")
    @ApiOperation("好友争霸活动规则和活动时间")
    public Response<Map<String,Object>> selectRule() throws Exception{
        return doResponse(() ->{
            Map<String,Object> result = actInviteSharedService.selectRule();
            return result;
        });
    }


    @SessionInterceptor.IntercepterIngore
    @PostMapping("/sendMsg")
    @ApiOperation(value = "发送验证码")
    public Response<Boolean> sendMsg(String phone) {
        return doResponse(() ->{
            Boolean flag = actInviteSharedService.sendMsg(phone);
            return flag;
        });
    }


    @SessionInterceptor.IntercepterIngore
    @PostMapping("/userRegister")
    @ApiOperation(value = "用户注册")
    public Response<Void> userRegister(PlantRegisterVo plantRegisterVo) {
        return doResponse(() ->{
            actInviteSharedService.userRegister(plantRegisterVo);
            return null;
        });
    }


    @SessionInterceptor.IntercepterIngore
    @PostMapping("/referralCode")
    @ApiOperation(value = "获取推荐码及个人信息")
    public Response<ReferralVo> referralCode(Long partyId) {
        return doResponse(()->{
            ReferralVo flag = actInviteSharedService.referralCode(partyId);
            return  flag;
        });
    }

    @PostMapping("/myApplyInfoTwo")
    @ApiOperation(value = "用户报名信息(二期)")
    public Response<ActInviteVo> myApplyInfoTwo() {
        return doResponse(() ->{
            ActInviteVo actInviteVo = actInviteSharedService.queryApplyInfoTwo(GlobalRequestContext.currentPartyId());
            return actInviteVo;
        });
    }

    /**
     * 工具方法，用于做统一处理
     *
     * @param func
     * @param <T>
     * @return
     */
    private <T> Response<T> doResponse(Supplier<T> func) {
        T result = func.get();
        logger.info("======== result:{}", JSON.toJSON(result));
        return Response.ofSuccess(result);
    }


}
