package com.xianglin.act.web.home.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.xianglin.act.biz.shared.VoteActService;
import com.xianglin.act.common.dal.model.*;
import com.xianglin.act.common.service.facade.SysConfigService;
import com.xianglin.act.common.service.facade.constant.ActivityConfig;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.act.web.home.intercepter.RepeatSubmitCheckInterceptor;
import com.xianglin.act.web.home.intercepter.SessionInterceptor;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.core.model.enums.OrderTypeEnum;
import com.xianglin.act.common.util.BizException;
import com.xianglin.core.model.vo.VoteActivityBaseInfoVO;
import com.xianglin.core.model.vo.VoteItemVO;
import com.xianglin.core.model.vo.VoteShareVO;
import com.xianglin.core.service.VoteActivityContextV2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

import static com.xianglin.act.common.util.ActPreconditions.ResponseEnum.*;
import static com.xianglin.core.service.ActivityContext.getActivityCode;
import static com.xianglin.core.service.VoteActivityContextV2.getCurrentVoteActivity;

/**
 * 投票活动相关
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/31 13:16.
 */
@RestController
@RequestMapping("/act/api/activity/vote")
@Api(value = "/act/api/activity/vote", tags = "投票活动接口")
@ApiResponses({@ApiResponse(code = 2000, message = "调用成功"),
        @ApiResponse(code = 3000, message = "业务异常"),
        @ApiResponse(code = 4000, message = "未知错误，系统异常")})
public class VoteActController {

    private static final Logger logger = LoggerFactory.getLogger(VoteActController.class);

    private static final String ACTIVITY_CODE = "activityCode";

    private static final String DATE_FORMATTER = "yyyy年MM月dd日 HH:mm";

    private static final int SERIAL_NUMBER_LENGTH = 4;

    private static final String VOTE_CODE = "HD001";

    @Autowired
    private VoteActService voteActService;

    @Autowired
    private SysConfigService sysConfigService;
    /**
     * 业务入口处就将当前活动绑定到线程中，便于代码中获取当前活动并根据活动参数控制业务流程
     * 请求是要区分活动的，如果没有用参数注明当前请求所属的活动，则请求失败
     *
     * @param request
     * @return
     */
    @ModelAttribute("param-pre-check")
    public String checkActivityCode(HttpServletRequest request) {
        String parameter = request.getParameter(ACTIVITY_CODE);
        if (StringUtils.isBlank(parameter)) {
            throw new BizException(ACTIVITY_CODE_NOT_EXIST);
        }
        Activity currentActivity = voteActService.getVoteActContext(parameter);
        if (currentActivity != null) {
            VoteActivityContextV2.setCurrentVoteActivity(currentActivity);
        } else {
            throw new BizException(ACT_NOT_EXIST);
        }
        logger.info("===========请求投票活动：activityCode -> [[ {} ]]===========", parameter);
        return parameter;
    }

    @RepeatSubmitCheckInterceptor.RepeatSubmitCheck()
    @PostMapping("vote-item/{type}")
    @ApiOperation(value = "投票", notes = "投票", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<CustomerAcquire> voteItem(@RequestParam("partyId") long partyId,@PathVariable String type) {
        checkPreviewUser(type);//检查是否是预览
        if (voteActService.isExpire()) {
            throw new BizException(ACT_EXPIRE);
        }
        CustomerAcquire acquire = voteActService.voteItem(getCurrentPartyId(), partyId);
        return Response.ofSuccess(acquire);
    }

    @PostMapping("my-item/{type}")
    @ApiOperation(value = "我的", notes = "我的", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<VoteItemVO> myItem(@PathVariable String type) {
        checkPreviewUser(type);//检查是否是预览
        VoteItemVO voteItem = voteActService.myItem(getCurrentPartyId());
        return Response.ofSuccess(voteItem);
    }

    @RepeatSubmitCheckInterceptor.RepeatSubmitCheck()
    @PostMapping("draw-award/{type}")
    @ApiOperation(value = "领取奖励", notes = "领取奖励", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<CustomerAcquire> drawAward(@PathVariable String type) {
        checkPreviewUser(type);//检查是否是预览
        CustomerAcquire acquire = voteActService.drawAward(getCurrentPartyId());
        return Response.ofSuccess(acquire);
    }

    @PostMapping("share-award/{type}")
    @ApiOperation(value = "晒单", notes = "晒单", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Object> shareAward(@PathVariable String type) {
        checkPreviewUser(type);//检查是否是预览
        voteActService.shareAward(getCurrentPartyId());
        return Response.ofSuccess();
    }

    @RepeatSubmitCheckInterceptor.RepeatSubmitCheck()
    @PostMapping("partake-in-vote-act/{type}")
    @ApiOperation(value = "参与活动", notes = "参与活动", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<?> partakeInVoteAct(@RequestParam("description") String description, @RequestParam("imgUrl") String imgUrl,@PathVariable String type) {
        checkPreviewUser(type);//检查是否是预览
        if (voteActService.isExpire()) {
            throw new BizException(ACT_EXPIRE);
        }
        description = description.trim();
        imgUrl = imgUrl.trim();
        if (StringUtils.isBlank(description)) {
            return Response.ofFail("描述不能为空");
        }
        if (description.length() > 512) {
            return Response.ofFail("描述长度超过512");
        }
        if (StringUtils.isBlank(imgUrl)) {
            return Response.ofFail("图片链接不能为空");
        }
        voteActService.addVoteItem(description, imgUrl);
        return Response.ofSuccess();
    }

    @PostMapping("partake-in-status/{type}")
    @ApiOperation(value = "参与活动状态", notes = "参与活动状态", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> checkPartakeInStatus(@PathVariable String type) {
        checkPreviewUser(type);//检查是否是预览
        boolean flag = voteActService.checkPartakeInStatus();
        return Response.ofSuccess(flag);
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("base-info")
    @ApiOperation(value = "活动基本信息", notes = "活动首页轮播图等基本信息", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<VoteActivityBaseInfoVO> queryActivityBaseInfo() {
        //当前活动
        Activity currentVoteActivity = getCurrentVoteActivity();
        LocalDateTime now = LocalDateTime.now();
        String activityName = currentVoteActivity.getActivityName();
        activityName = Strings.nullToEmpty(activityName);
        //轮播图
        com.xianglin.act.common.dal.model.ActivityConfig activityConfig = voteActService.queryActivityConfigByCode(currentVoteActivity.getActivityCode(),ActivityConfig.ActivityVote.EDIT_STYLE_BANNER.name());
        if (activityConfig == null){
            throw new BizException(ActPreconditions.ResponseEnum.VOTE_KEY_EMPTY);
        }
        //List<String> imageList = null;
        //if (StringUtils.isNotBlank(carouselImgs)) {
        //    imageList = Splitter.on(";")
        //            .splitToList(carouselImgs);
        //}
        //是否结束
        boolean hasExpire = voteActService.isExpire();
        long timestamp = Duration.between(now, voteActService.getEndTime()).getSeconds();
        //当前参与人数
        int count = voteActService.countPartakePeople();
        boolean hasPartakeIn;
        Long currentPartyId = getCurrentPartyId();
        Long itemId = null;
        if (currentPartyId == null) {
            hasPartakeIn = false;
        } else {
            ActVoteItem voteItem = voteActService.getVoteItemByPartyId(currentPartyId);
            hasPartakeIn = voteItem != null;
            if (hasPartakeIn) {
                itemId = voteItem.getId();
            }
        }

        Boolean isStopRegister = false;
        //获取报名开关
        com.xianglin.act.common.dal.model.ActivityConfig pushOnRegister = voteActService.queryActivityConfigByCode(getActivityCode(),
                ActivityConfig.ActivityVote.PUSH_REGISTER.name());
        if (pushOnRegister == null){
            throw new BizException(ActPreconditions.ResponseEnum.VOTE_KEY_EMPTY);
        }else if (StringUtils.equals(pushOnRegister.getConfigValue(),ActivityConfig.PushRegister.on.name())){
            isStopRegister = voteActService.isStopRegister(ActivityConfig.ActivityVote.REGISTER_START_TIME.name(),ActivityConfig.ActivityVote.REGISTER_END_TIME.name());
        }


        VoteActivityBaseInfoVO buildVO = VoteActivityBaseInfoVO.builder()
                .title(activityName)
                .carouselImgs(activityConfig.getConfigValue())
                .hasExpire(hasExpire)
                .hasPartakeIn(hasPartakeIn)
                .partakePeoples(count)
                .timestamp(timestamp)
                .id(itemId)
                .pushOnRegister(pushOnRegister.getConfigValue())
                .isStopRegister(isStopRegister)
                .build();
        return Response.ofSuccess(buildVO);
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("vote-item-list")
    @ApiOperation(value = "参与排行或搜索", notes = "参与排行或搜索", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<VoteItemVO>> queryVoteItemList(@RequestParam(value = "orderType", required = false) OrderTypeEnum orderType,
                                                        @RequestParam(value = "curPage", defaultValue = "1") Integer curPage,
                                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                        @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                                        @RequestParam(value = "lastId", required = false) Long lastId) {
        List<VoteItemVO> itemList = null;
        if (StringUtils.isBlank(serialNumber)) {
            if (curPage <= 0) {
                curPage = 1;
            }
            if (pageSize <= 0) {
                pageSize = 1;
            }
            itemList = voteActService.queryItemList(orderType, curPage, pageSize,lastId);
        } else {
            if (serialNumber.length() != SERIAL_NUMBER_LENGTH) {
                return Response.ofFail("序列号参数不合法");
            }
            if (!StringUtils.isNumeric(serialNumber)) {
                return Response.ofFail("只能输入数字");
            }
            if (curPage == 1) {
                VoteItemVO itemVO = voteActService.searchVoteItem(serialNumber);
                if (itemVO != null) {
                    itemList = Lists.newArrayList(itemVO);
                }
            }
        }
        if (itemList == null) {
            return Response.ofSuccess(Lists.newArrayList());
        }
        return Response.ofSuccess(itemList);
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("vote-item-popularity-list")
    @ApiOperation(value = "人气排行", notes = "人气排行", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<VoteItemVO>> queryVoteItemPopularityList() {
        List<VoteItemVO> itemList = voteActService.queryPopularityItemList();
        if (itemList == null) {
            return Response.ofSuccess(Lists.newArrayList());
        }
        return Response.ofSuccess(itemList);
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("share-item")
    @ApiOperation(value = "分享", notes = "分享", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<VoteShareVO> itemShareInfo() {
        VoteShareVO shareVO = voteActService.itemShareInfo();
        return Response.ofSuccess(shareVO);
    }


    @SessionInterceptor.IntercepterIngore
    @PostMapping("item-detail")
    @ApiOperation(value = "分享页面详情-我的参与图片", notes = "分享页面详情-我的参与图片", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<VoteItemVO> itemDetail(@RequestParam(value = "id") Long id) {
        VoteItemVO voteItemVO = voteActService.getVoteItem(id);
        if (voteItemVO == null) {
            throw new BizException(VOTE_IMG_NE);
        }
        return Response.ofSuccess(voteItemVO);
    }

    private Long getCurrentPartyId() {

        return GlobalRequestContext.currentPartyId();
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("send_message")
    @ApiOperation(value = "短信发送", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<?> sendMessage(String mobilePhone) {

        if (voteActService.isExpire()) {
            throw new BizException(ACT_EXPIRE);
        }
        voteActService.sendMessage(mobilePhone);
        return Response.ofSuccess();
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("check_message")
    @ApiOperation(value = "短信校验", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<?> checkMessage(String mobilePhone, String code, long toPartyId) {
        if (voteActService.isExpire()) {
            throw new BizException(ACT_EXPIRE);
        }
        voteActService.checkMessage(mobilePhone, code, toPartyId);
        return Response.ofSuccess();
    }

    @PostMapping("contact_info/{type}")
    @ApiOperation(value = "联系信息提交", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<?> contactInfo(ContactInfo contactInfo,@PathVariable String type) {
        checkPreviewUser(type);//检查是否是预览
        contactInfo.setPartyId(getCurrentPartyId());
        contactInfo.setCreator(String.valueOf(contactInfo.getPartyId()));
        contactInfo.setUpdater(String.valueOf(contactInfo.getPartyId()));
        voteActService.contactInfo(contactInfo);
        return Response.ofSuccess();
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("queryActivityConfig")
    @ApiOperation(value = "查询活动参数", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Map<String,String>> queryActivityConfig(@RequestParam("keys") String keys) {
        List<String> list = JSON.parseArray(keys,String.class);
        Map<String,String> result = voteActService.queryActivityConfigByCode(VOTE_CODE,list);
        return Response.ofSuccess(result);
    }
    
    private void checkPreviewUser(String type){
        if(StringUtils.equals(type,"pre")){
            Long partyId = getCurrentPartyId();
            voteActService.checkPreviewUserByPartyId(partyId); 
        }
    }


}
