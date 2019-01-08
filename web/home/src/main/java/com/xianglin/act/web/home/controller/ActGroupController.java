package com.xianglin.act.web.home.controller;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.biz.shared.ActGroupSharedService;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.act.web.home.intercepter.RepeatSubmitCheckInterceptor;
import com.xianglin.act.web.home.intercepter.SessionInterceptor;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.core.model.enums.GroupEnum;
import com.xianglin.core.model.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Describe :
 * Created by xingyali on 2018/12/19 9:15.
 * Update reason :
 */
@RestController
@RequestMapping("/act/api/actGroup")
@Api(value = "/act/api/actGroup", tags = "春节成团活动")
@ApiResponses({@ApiResponse(code = 2000, message = "调用成功"),
        @ApiResponse(code = 3000, message = "业务异常"),
        @ApiResponse(code = 4000, message = "未知错误，系统异常")})
public class ActGroupController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ActInviteController.class);

    private static final String ACTIVITY_CODE = "ACT_GROUP";
    
    @Autowired
    private ActGroupSharedService actGroupSharedService;

    private volatile List<ActGroupTipsVo> cache = null;

    private volatile LocalDateTime currentTime = LocalDateTime.now();

    @RepeatSubmitCheckInterceptor.RepeatSubmitCheck()
    @PostMapping("/queryActGroupDetail")
    @ApiOperation(value = "活动详情页")
    public Response<ActGroupInfoVo> queryGroupDetail() {
        return doResponse(() ->{
            //getCurrentPartyId();
            ActGroupInfoVo actGroupInfoVo = actGroupSharedService.queryGroupDetail(getCurrentPartyId());
            return actGroupInfoVo;
        });
    }

    @PostMapping("/queryScrollMessage")
    @ApiOperation(value = "滚动消息查询")
    public Response<List<ActGroupTipsVo>> queryScrollMessage() {
        return doResponse(() ->{
            List<ActGroupTipsVo> actGroupTipsVos = null;
            if(CollectionUtils.isNotEmpty(cache)){
                actGroupTipsVos = cache ;  
            } else {
                actGroupTipsVos = actGroupSharedService.queryScrollMessage();
                cache = actGroupTipsVos;
            }
            if(Duration.between(currentTime,LocalDateTime.now()).toMinutes() > 2){
                actGroupTipsVos = actGroupSharedService.queryScrollMessage();
                currentTime = LocalDateTime.now();
            }
            return actGroupTipsVos;
        });
    }


    @PostMapping("/joinGroup")
    @ApiOperation(value = "加入团")
    public Response<ActGroupInfoVo> joinGroup(Long partyId) {
        return doResponse(() ->{
            ActGroupInfoVo actGroupInfoVo = actGroupSharedService.joinGroup(partyId,getCurrentPartyId());
            return actGroupInfoVo;
        });
    }

    @RepeatSubmitCheckInterceptor.RepeatSubmitCheck()
    @PostMapping("/createGroup")
    @ApiOperation(value = "开团")
    public Response<ActGroupInfoVo> createGroup() {
        return doResponse(() ->{
            ActGroupInfoVo actGroupInfo = actGroupSharedService.createGroup(getCurrentPartyId());
            return actGroupInfo;
        });
    }

    @PostMapping("/updateGroupStyle")
    @ApiOperation(value = "修改团主题")
    public Response<Boolean> updateGroupStyle(Long id,String style) {
        return doResponse(() ->{
            Boolean flag = actGroupSharedService.updateGroupStyle(getCurrentPartyId(),id,style);
            return flag;
        });
    }
    
    

    @PostMapping("/groupListByPartyId")
    @ApiOperation(value = "我参与的团，只查进行中的团")
    public Response<List<ActGroupInfoVo>> groupListByPartyId() {
        return doResponse(() ->{
            List<ActGroupInfoVo> map = actGroupSharedService.groupListByPartyId(getCurrentPartyId());
            return map;
        });
    }

    @PostMapping("/groupStyleByPartyId")
    @ApiOperation(value = "我参与的团，只查进行中的团")
    public Response<String> groupStyleByPartyId(Long partyId) {
        return doResponse(() ->{
            String style = actGroupSharedService.groupStyleByPartyId(partyId);
            return style;
        });
    }

    @PostMapping("/groupDetailShare")
    @ApiOperation(value = "个人团信息查询")
    public Response<ActGroupInfoVo> groupDetailShare(Long partyId) {
        return doResponse(() ->{
            ActGroupInfoVo map = actGroupSharedService.groupDetailShare(partyId);
            return map;
        });
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/share")
    @ApiOperation(value = "分享信息查询")
    public Response<ActGroupShareVo> share(@Validated Long partyId) {
        return doResponse(() ->{
            //查询当前用户的信息
            ActGroupShareVo map = actGroupSharedService.share(partyId);
            return map;
        });
    }


    @SessionInterceptor.IntercepterIngore
    @PostMapping("/prizeList")
    @ApiOperation(value = "礼品列表")
    public Response<List<PrizeVo>> queryPrizeListByActivityCode() {
        return doResponse(() ->{
            List<PrizeVo> prizeVoList = actGroupSharedService.queryPrizeListByActivityCode(ACTIVITY_CODE);
            return prizeVoList;
        });
    }


    @PostMapping("/exchange")
    @ApiOperation(value = "兑换礼品")
    public Response<String> exchange(String prizeCode) {
        return doResponse(() ->{
            String result = actGroupSharedService.exchangePrize(getCurrentPartyId(),prizeCode,ACTIVITY_CODE);
            return result;
        });
    }


    @PostMapping("/withDraw")
    @ApiOperation(value = "提现")
    public Response<Boolean> withDraw() {
        return doResponse(() ->{
            Boolean flag = actGroupSharedService.withDraw(getCurrentPartyId());
            return flag;
        });
    }


    @PostMapping("/queryExchangeDetail")
    @ApiOperation(value = "兑换明细")
    public Response<List<CustomerAcquireRecordVO>> queryExchangeDetail(String type) {
        return doResponse(() ->{
            List<CustomerAcquireRecordVO> result = actGroupSharedService.queryExchangeDetail(getCurrentPartyId(),ACTIVITY_CODE,type);
            return result;
        });
    }


    @PostMapping("/queryRedPack")
    @ApiOperation(value = "我的红包")
    public Response<RedPackageVo> queryRedPack(String type) {
        return doResponse(() ->{
            RedPackageVo  result = actGroupSharedService.queryRedPack(getCurrentPartyId(),type,ACTIVITY_CODE);
            return result;
        });
    }


    @PostMapping("/commitAddress")
    @ApiOperation(value = "提交地址信息")
    public Response<Boolean> commitAddress(ContactInfoVO contactInfoVO) {
        return doResponse(() ->{
            contactInfoVO.setActivityCode(ACTIVITY_CODE);
            contactInfoVO.setPartyId(getCurrentPartyId());
            Boolean flag = actGroupSharedService.commitAddress(contactInfoVO);
            return flag;
        });
    }


    @PostMapping("/queryGroupTipsByPartyId")
    @ApiOperation(value = "红包明细")
    public Response<List<ActGroupTipsVo>> queryGroupTipsByPartyId() {
        return doResponse(() ->{
            List<ActGroupTipsVo>  result = actGroupSharedService.queryGroupTipsByPartyId(getCurrentPartyId(),GroupEnum.GroupExchangeType.R.name()).stream().peek(v -> {
                if (Double.valueOf(v.getChangeValue())>=0) {
                    v.setChangeValue("+"+v.getChangeValue());
                }
            }).collect(Collectors.toList());
            return result;
        });
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/queryPeopleNum")
    @ApiOperation(value = "拆红包和兑换人数")
    public Response<Map<String,String>> queryPeopleNum() {
        return doResponse(() ->{
            Map<String,String>  result = actGroupSharedService.queryPeopleNum();
            return result;
        });
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/queryPoster")
    @ApiOperation(value = "海报信息查询")
    public Response<Map<String,String>> queryPoster(final Long partyId){
        return doResponse(() ->{
            return new HashMap<String,String>(){{
                Optional.ofNullable(actGroupSharedService.queryGroupUser(partyId)).ifPresent(v -> {
                    put("name",v.getName());
                    put("headImg",v.getHeadImg());
                    put("qrCode",v.getQrCode());
                });
            }};
        });
    }

    @PostMapping("/dismantle/balance")
    @ApiOperation(value = "拆红包余额查询，返回当前余额，已拆得余额")
    public Response<Map<String,BigDecimal>> queryDismantleBalance() {
        return doResponse(() ->{
            return actGroupSharedService.queryDismantleBalance(getCurrentPartyId());
        });
    }

    @PostMapping("/dismantle/dismantle")
    @ApiOperation(value = "拆红包(用户点击拆红包)")
    public Response<List<BigDecimal>> dismantlePacket(String code) {
        return doResponse(() ->{
            return new ArrayList<BigDecimal>(){{
                add(actGroupSharedService.dismantlePacket(getCurrentPartyId(),code));
                AtomicInteger atom = new AtomicInteger(1);
                GroupEnum.DismantlePacketType.valueOf(code).randomPacket().forEach(v -> {
                    add(v);
                });
            }};
        });
    }


    @SessionInterceptor.IntercepterIngore
    @PostMapping("/queryGroupRule")
    @ApiOperation(value = "活动规则")
    public Response<Map<String,String>> queryGroupRule() {
        return doResponse(() ->{
            Map<String,String> result = actGroupSharedService.queryGroupRule();
            return result;
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

    private Long getCurrentPartyId() {

        return GlobalRequestContext.currentPartyId();
    }

}
