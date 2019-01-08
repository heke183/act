package com.xianglin.act.web.home.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.xianglin.act.biz.shared.ActPlantSharedService;
import com.xianglin.act.common.dal.mappers.ConfigMapper;
import com.xianglin.act.common.dal.model.ActPlant;
import com.xianglin.act.common.dal.model.ActPlantNotice;
import com.xianglin.act.common.dal.model.ActPlantPrize;
import com.xianglin.act.common.dal.model.PageReq;
import com.xianglin.act.common.service.facade.model.ActPlantNoticeDTO;
import com.xianglin.act.common.service.integration.CustomersInfoServiceClient;
import com.xianglin.act.common.util.*;
import com.xianglin.act.web.home.intercepter.RepeatSubmitCheckInterceptor;
import com.xianglin.act.web.home.intercepter.SessionInterceptor;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import com.xianglin.cif.common.service.facade.model.CustomersDTO;
import com.xianglin.core.model.vo.*;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author ex-jiangyongtao
 * @date 2018/8/2  17:51
 */
@RestController
@RequestMapping("/act/api/actPlant")
@Api(value = "/act/api/actPlant", tags = "种树活动")
@ApiResponses({@ApiResponse(code = 2000, message = "调用成功"),
        @ApiResponse(code = 3000, message = "业务异常"),
        @ApiResponse(code = 4000, message = "未知错误，系统异常")})
public class ActPlantController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ActPlantController.class);


    @Autowired
    private ConfigMapper configMapper;
    @Autowired
    private ActPlantSharedService actPlantSharedService;
    @Resource
    private WxApiUtils wxApiUtils2;
    @Autowired
    private CustomersInfoServiceClient customersInfoServiceClient;

    @PostMapping("/isJoinAct")
    @ApiOperation(value = "判断用户是否参与活动,同时返回可以展示的所有爱心值和当前活动的cdoe")
    public Response<ActPlantHomePageVo> isJoinAct() throws Exception {
        return doResponse(() -> {
            ActPlantHomePageVo actPlantHomePageVo = actPlantSharedService.userHomePageInfo(getCurrentPartyId());
            return actPlantHomePageVo;
        });
    }

    @PostMapping("/rankingList")
    @ApiOperation(value = "排行榜")
    public Response<ActPlantRankingVo> rankingList() throws Exception {
        return doResponse(() -> {
            Long partyId = getCurrentPartyId();
            ActPlantRankingVo actPlantRankingVo = actPlantSharedService.queryRankingList(partyId);
            return actPlantRankingVo;
        });

    }

    @PostMapping("/messageDetailsList")
    @ApiOperation(value = "查近三天的消息明细列表")
    public Response<List<ActPlantMessageDetailVo>> messageDetailsList() throws Exception {
        return doResponse(() -> {
            Long partyId = getCurrentPartyId();
            List<ActPlantMessageDetailVo> actPlantMessageDetailVoList = actPlantSharedService.messageDetailsList(partyId);
            return actPlantMessageDetailVoList;
        });
    }

    @PostMapping("/task")
    @ApiOperation(value = "查用户的任务表")
    public Response<List<ActPlantTaskVo>> task() throws Exception {
        return doResponse(() -> {
            Long partyId = getCurrentPartyId();
            List<ActPlantTaskVo> actPlantTaskVos = actPlantSharedService.task(partyId);
            return actPlantTaskVos;
        });
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/share")
    @ApiOperation(value = "分享信息")
    public Response<ActPlantShareVo> share(final Long partyId) throws Exception {
        return doResponse(() -> {
            ActPlantShareVo actPlantShareVo = new ActPlantShareVo();
            try {
                Long currPartyId = partyId;
                if (currPartyId == null) {
                    currPartyId = getCurrentPartyId();
                }
                actPlantShareVo.setContent("每天都来收爱心，树苗长大你获利，点击前往》");
                actPlantShareVo.setTitle ("快来帮我领爱心，我要免费得888元");
                actPlantShareVo.setImage(configMapper.selectConfig("PLANT_SHARE_IMG_URL"));
                if (currPartyId != null) {
                    actPlantShareVo.setUrlWX(wxApiUtils2.getAuthUrl(currPartyId));
                }
                actPlantShareVo.setUrlWB(configMapper.selectConfig("ACT_PLANT_SHARE_WB"));
                actPlantShareVo.setUrlQQ(configMapper.selectConfig("ACT_PLANT_SHARE") + "?partyId=" + currPartyId);
            } catch (UnsupportedEncodingException e) {
                logger.warn("", e);
            }
            return actPlantShareVo;
        });
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/isRegister")
    @ApiOperation(value = "判断用户是否注册App或领取了树苗")
    public Response<Map<String, Boolean>> isRegister(String phone) throws Exception {
        return doResponse(() -> {
           /* Map<String,Object> map = new HashMap<>();
            UserVo userVo = actPlantSharedService.queryUserByPhone(phone);
            if (userVo == null) { //未注册
                return false;
            }*/
            Map<String, Boolean> map = actPlantSharedService.isRegisterOrisReceiveTree(phone);
            return map;
        });
    }


    @SessionInterceptor.IntercepterIngore
    @PostMapping("/register")
    @ApiOperation(value = "新用户注册,并领取奖励")
    public Response<Long> register(PlantRegisterVo plantRegisterVo) throws Exception {
        return doResponse(() -> {
            Long partyId = actPlantSharedService.register(plantRegisterVo);
            return partyId;
        });
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/receiveState")
    @ApiOperation(value = "领取状态 status 今天领取:today;明天领取:yesterday;")
    public Response<Map<String, Object>> receiveState(String openId, Long partyId) throws Exception {
        return doResponse(() -> {
            Map<String, Object> status = actPlantSharedService.queryNewUserReciveState(openId, partyId);
            return status;
        });
    }


    @SessionInterceptor.IntercepterIngore
    @PostMapping("/prizeList")
    @ApiOperation(value = "奖品列表")
    public Response<List<ActPlantPrizeVo>> prizeList() throws Exception {
        return doResponse(() -> {
            List<ActPlantPrize> actPlantPrizes = actPlantSharedService.queryPrizeList();
            List<ActPlantPrizeVo> prizeVoList = null;
            try {
                prizeVoList = DTOUtils.map(actPlantPrizes, ActPlantPrizeVo.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return prizeVoList;
        });
    }


    @PostMapping("/joinAct")
    @ApiOperation(value = "用户开始参与活动")
    public Response<Boolean> joinAct() throws Exception {
        return doResponse(() -> {
            ActPlant actPlant = actPlantSharedService.joinAct(getCurrentPartyId(), null);
            if (actPlant == null) {
                return false;
            }
            return true;
        });
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/obtainLv")
    @ApiOperation(value = "收取爱心")
    public Response<ActPlantLvObtainVo> obtainLv(ActPlantLvVo actPlantLvVo) throws Exception {
        return doResponse(() -> {
            ActPlantLvObtainVo actPlantLvObtainVo = null;
            if (actPlantLvVo.getPartyId() != null) {
                actPlantLvObtainVo = actPlantSharedService.obtainLv(actPlantLvVo, actPlantLvVo.getPartyId());
            } else if (getCurrentPartyId() != null) {
                actPlantLvObtainVo = actPlantSharedService.obtainLv(actPlantLvVo, getCurrentPartyId());
            } else {
                throw new BizException(ActPreconditions.ResponseEnum.NO_LOGIN);
            }
            return actPlantLvObtainVo;
        });
    }


    @PostMapping("/exchangePrize")
    @ApiOperation(value = "礼品兑换,返回爱心交易明细的主键id")
    public Response<Long> exchangePrize(String actPrizeCode) throws Exception {
        return doResponse(() -> {
            Long id = actPlantSharedService.exchangePrize(getCurrentPartyId(), actPrizeCode);
            return id;
        });
    }


    @ApiOperation(value = "用户实名认证查询)")
    @PostMapping("/userCertification")
    public Response<Boolean> userCertification() throws Exception {
        return doResponse(() -> {
            Boolean flag1 = actPlantSharedService.userCertification(getCurrentPartyId());
            return flag1;
        });
    }

    @PostMapping("/addressCommit")
    @ApiOperation(value = "提交地址信息")
    public Response<Boolean> addressCommit(ActPlantLvTranVo actPlantLvTranVo) throws Exception {
        return doResponse(() -> {
            actPlantLvTranVo.setPartyId(getCurrentPartyId());
            Long row = actPlantSharedService.addressCommit(actPlantLvTranVo);
            if (row == null) {
                return false;
            }
            return true;
        });
    }

    private Long getCurrentPartyId() {

        return GlobalRequestContext.currentPartyId();
    }

    @PostMapping("/selectUserInfo")
    @ApiOperation(value = "查询用户信息")
    public Response<UserAddressVo> selectUserInfo() {
        return doResponse(() -> {
            CustomersDTO customersDTO = customersInfoServiceClient.selectByPartyId(getCurrentPartyId()).getResult();
            UserAddressVo adressVo = UserAddressVo.builder().userName(customersDTO.getCustomerName()).mobile(customersDTO.getMobilePhone()).build();
            return adressVo;
        });
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/posterInfo")
    @ApiOperation(value = "海报信息查询")
    public Response<UserVo> posterInfo(Long partyId) {
        return doResponse(() -> {
            UserVo vo = actPlantSharedService.selectUserInfo(partyId);
            String showName = vo.getTrueName();
            if(StringUtils.isEmpty(showName)){
                showName = vo.getShowName();
            }
            vo.setShowName(showName);
            vo.setComments(actPlantSharedService.queryQrCode(partyId));
            return vo;
        });
    }

    @PostMapping("/follow/list")
    @ApiOperation(value = "关注列表")
    public Response<List<ActPlantFollow>> followList() {
        //TODO wanglei
        return doResponse(() -> {
            return actPlantSharedService.queryFollows(getCurrentPartyId());
        });
    }

    @PostMapping("/follow/invite")
    @ApiOperation(value = "邀请关注用户")
    public Response<ActPlantShareVo> followInvite(Long partyId) {
        return doResponse(() -> {
            actPlantSharedService.followInvite(getCurrentPartyId(), partyId);
            ActPlantShareVo vo = ActPlantShareVo.builder().image(configMapper.selectConfig("PLANT_FOLLOW_INVITE_IMG"))
                    .title("快来帮我领爱心，我要免费得888元")
                    .content("每天都来收爱心，树苗长大你获利，点击前往》")
                    .content("每天都来收爱心，树苗长大你获利，点击前往》")
                    .urlWB(configMapper.selectConfig("ACT_PLANT_SHARE_WB")).build();
//            actPlantSharedService.sendRankMsg();
            return vo;
        });
    }

    @PostMapping("/follow/portail")
    @ApiOperation(value = "光柱用户主页")
    public Response<ActPlantHomePageVo> followPortail(Long partyId) {
        return doResponse(() -> {
            String actCode = actPlantSharedService.findActCode();
            UserVo user = actPlantSharedService.selectUserInfo(partyId);
            ActPlant plant = actPlantSharedService.queryPlant(partyId);
            ActPlantHomePageVo vo = ActPlantHomePageVo.builder().userHeadImg(user.getHeadImg()).actCode(actCode).userLv(plant.getLv()).build();
            vo.setActPlantLvVos(actPlantSharedService.queryPlantLvs(getCurrentPartyId(), partyId));
            try {
                vo.setActPlantNoticeVoList(DTOUtils.map(actPlantSharedService.queryActPlantNotices(null,true),ActPlantNoticeVo.class));
            } catch (Exception e) {
                logger.warn("notice",e);
            }
            return vo;
        });
    }

    @RepeatSubmitCheckInterceptor.RepeatSubmitCheck()
    @PostMapping("/follow/collect")
    @ApiOperation(value = "收取光柱用户爱心值")
    public Response<Integer> followCollect(Long lvId, Long partyId) {
        return doResponse(() -> {
            ActPlantLvVo lv = actPlantSharedService.selectActPlantLv(lvId);
            if (lv == null || !lv.getPartyId().equals(partyId) || lv.getMatureTime().after(new Date()) || lv.getExpireTime().before(new Date())) {
                throw new BizException("当前爱心不可收！");
            }
            return actPlantSharedService.collectFollowLv(getCurrentPartyId(), lvId);
        });
    }


    @PostMapping("/getActPlantLvInfo")
    @ApiOperation(value = "查询用户所有的爱心值")
    public Response<List<ActPlantLvVo>> getActPlantLvInfo(Long partyId) {
        return doResponse(() -> {
            List<ActPlantLvVo> actPlantLvVos = null;
            if (partyId == null) {
                actPlantLvVos = actPlantSharedService.showLv(getCurrentPartyId());
                if (actPlantLvVos.size() > 0) {
                    actPlantLvVos.forEach(actPlantLvVo -> {
                        Long time = (actPlantLvVo.getMatureTime().getTime() - System.currentTimeMillis()) / 1000;
                        if (time > 0) {
                            actPlantLvVo.setCanCollect(false);
                            actPlantLvVo.setRencentTime(time);
                        } else {
                            actPlantLvVo.setCanCollect(true);
                            actPlantLvVo.setRencentTime(0L);
                        }
                    });
                }
            } else {
                actPlantLvVos = actPlantSharedService.queryPlantLvs(getCurrentPartyId(), partyId);
            }
            if (actPlantLvVos.size()>0){
                return actPlantLvVos;
            }else {
                return null;
            }
        });
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/sendMsg")
    @ApiOperation(value = "发送验证码")
    public Response<Boolean> sendMsg(String phone) {
        return doResponse(() -> {
            Boolean flag = actPlantSharedService.sendMsg(phone);
            return flag;
        });
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/queryUserOpenId")
    @ApiOperation(value = "根据code查询openID和partyID")
    public Response<Map<String, Object>> queryUserOpenId(String code, Long fromPartyId) {
        return doResponse(() -> {
            Map<String, Object> map = actPlantSharedService.queryUserOpenId(code, fromPartyId);
            return map;
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
        actPlantSharedService.activityIsStop();
        T result = func.get();
        logger.info("======== result:{}", StrUtil.sub(JSON.toJSONString(result),0,1000));
        return Response.ofSuccess(result);
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/shareLv")
    @ApiOperation(value = "分享爱心")
    public Response<Boolean> shareLv() {
        return doResponse(() -> {
            Boolean flag = actPlantSharedService.shareLv(getCurrentPartyId());
            return flag;
        });
    }

    @PostMapping("/getRandomPrize")
    @ApiOperation(value = "获取随机奖励")
    public Response<Map<String, Object>> getRandomPrize() {
        return doResponse(() -> {
            Map<String, Object> resultMap = actPlantSharedService.getRandomPrize(getCurrentPartyId());
            return resultMap;
        });
    }

    @PostMapping("/queryActPlantNotices")
    @ApiOperation(value = "获取公告列表")
    public Response<List<ActPlantNoticeVo>> queryActPlantNotices() {
        return doResponse(() -> {
            List<ActPlantNoticeVo> actPlantNoticeVos = null;
            try {
                List<ActPlantNotice>  actPlantNoticeList = actPlantSharedService.queryActPlantNotices(null,true);
                actPlantNoticeVos = DTOUtils.map(actPlantNoticeList,ActPlantNoticeVo.class);
            }catch (Exception e){
                logger.warn("queryActPlantNotices",e);
            }
            return actPlantNoticeVos;
        });
    }
}
