package com.xianglin.act.web.home.controller;

import com.xianglin.act.biz.shared.ActService;
import com.xianglin.act.biz.shared.LuckyWheelService;
import com.xianglin.act.common.dal.model.Activity;
import com.xianglin.act.common.dal.model.CustomerAcquire;
import com.xianglin.act.common.dal.model.Prize;
import com.xianglin.act.common.service.integration.CustomersInfoServiceClient;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.act.web.home.intercepter.SessionInterceptor;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.cif.common.service.facade.model.CustomersDTO;
import com.xianglin.core.model.CheckMessageVO;
import com.xianglin.core.model.CustomerDetail;
import com.xianglin.core.model.Player;
import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.enums.Constants;
import com.xianglin.core.model.enums.CustomerTypeEnum;
import com.xianglin.core.model.enums.UserEnv;
import com.xianglin.core.service.ActivityContext;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.cache.Cache;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.xianglin.act.common.util.ActPreconditions.ResponseEnum.ACTIVITY_END;
import static com.xianglin.act.common.util.ActPreconditions.ResponseEnum.CUSTOMER_INFO_MISS;

/**
 * The type ActivityDTO controller.
 *
 * @author yefei
 * @date 2018 -01-18 13:33
 */
@RestController
@RequestMapping("/act/api/activity/luckywheel")
@Api(value = "/act/api/activity/luckywheel", tags = "幸运大转盘活动接口")
@ApiResponses({@ApiResponse(code = 2000, message = "调用成功"),
        @ApiResponse(code = 3000, message = "业务异常"),
        @ApiResponse(code = 4000, message = "未知错误，系统异常")})
public class LuckyWheelController {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(LuckyWheelController.class);

    @Resource
    private LuckyWheelService luckyWheelService;

    @Resource
    private ActService actService;

    @Resource
    private CustomersInfoServiceClient customersInfoServiceClient;

    private volatile Set<CustomerAcquire> cache = null;

    private volatile LocalDateTime currentTime = LocalDateTime.now();

    /**
     * 取得客户端ip地址
     *
     * @param request
     * @return
     */
    private static String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("x-forwarded-for");
        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
            if (clientIp.equals("127.0.0.1")) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    logger.error("UnknownHostException", e);
                }
                clientIp = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (clientIp != null && clientIp.length() > 15) { // "***.***.***.***".length()
            // = 15
            if (clientIp.indexOf(",") > 0) {
                clientIp = clientIp.substring(0, clientIp.indexOf(","));
            }
        }
        logger.debug("获取客户端IP地址：{}", clientIp);
        return clientIp;
    }

    @ModelAttribute
    public void checkActivityCode(String activityCode) {
        final Activity activity = actService.selectAct(activityCode);
        ActivityContext.set(activity);
        logger.info("===========大转盘活动：activityCode -> [[ {} ]]===========", activityCode);
    }

    /**
     * 抽奖
     *
     * @param player the player
     * @return prize prize
     */
    @SessionInterceptor.IntercepterIngore
    @PostMapping("/play")
    @ApiOperation(value = "抽奖")
    @ApiImplicitParam(value = "活动编号", name = "activityCode", required = true, paramType = "query")
    public Response<Prize> play(Player player, HttpServletRequest request, HttpServletResponse response) {

        ActPreconditions.checkCondition(
                System.currentTimeMillis() > ActivityContext.get().getExpireDate().getTime(),
                ACTIVITY_END);

        if (player.getPartyId() == null) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return null;
        }/* else {
            com.xianglin.cif.common.service.facade.model.Response<CustomersDTO> customer = customersInfoServiceClient.selectByPartyId(player.getPartyId());
            final CustomersDTO result = ActPreconditions.checkNotNull(customer.getResult(), CUSTOMER_INFO_MISS);
            long count = result.getRoleDTOs().stream().filter(cus -> Constants.APP_USER.equals(cus.getRoleCode())).count();
            // 老用户验证是否登录
            if(count > 0) {
                player.setCustomerType(CustomerTypeEnum.REGULAR_CUSTOMER);
                if (GlobalRequestContext.currentPartyId() == null || !GlobalRequestContext.currentPartyId().equals(player.getPartyId())) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    return null;
                }
            } else {
                player.setCustomerType(CustomerTypeEnum.NEW_CUSTOMER);
            }
            player.setMobilePhone(result.getMobilePhone());
        }*/
        player.setCustomerType(CustomerTypeEnum.REGULAR_CUSTOMER);
        ActivityRequest<Player> activityRequest = new ActivityRequest<>();
        activityRequest.setIp(getClientIp(request));
        activityRequest.setRequest(player);
        activityRequest.setSignature(player.getSignature());
        activityRequest.setSecurityKey(player.getSecurityKey());

        Prize prize = luckyWheelService.start(activityRequest);

        return Response.ofSuccess(prize);
    }

    /**
     * 短信验证码发送（开发）
     */
    @PostMapping("/send/message")
    @ApiOperation(value = "短信验证码发送")
    @ApiImplicitParam(value = "活动编号", name = "activityCode", required = true, paramType = "query")
    public Response<Void> sendMessage(Player player) {
        ActPreconditions.checkCondition(
                System.currentTimeMillis() > ActivityContext.get().getExpireDate().getTime(),
                ACTIVITY_END);

        luckyWheelService.sendMessage(player);
        return Response.ofSuccess();
    }

    /**
     * 校验验证码
     *
     * @param checkMessageVO the check message vo
     */
    @PostMapping("/check/message")
    @ApiOperation(value = "校验验证码")
    @ApiImplicitParam(value = "活动编号", name = "activityCode", required = true, paramType = "query")
    public Response<CheckMessageVO> checkMessage(CheckMessageVO checkMessageVO) {
        ActPreconditions.checkCondition(
                System.currentTimeMillis() > ActivityContext.get().getExpireDate().getTime(),
                ACTIVITY_END);

        CheckMessageVO message = luckyWheelService.checkMessage(checkMessageVO);
        return Response.ofSuccess(message);
    }

    /**
     * 中将记录
     *
     * @return list list
     */
    @PostMapping("/customer/acquire")
    @ApiOperation(value = "中将记录")
    @ApiImplicitParam(value = "活动编号", name = "activityCode", required = true, paramType = "query")
    public Response<Set<CustomerAcquire>> customerAcquireRecord() {
        Set<CustomerAcquire> customerAcquires = null;
        if(CollectionUtils.isNotEmpty(cache)){
            customerAcquires = cache;
        }else {
            customerAcquires = luckyWheelService.customerAcquireRecord();
            cache = customerAcquires;
        }
        CompletableFuture.runAsync(() -> {
            if(Duration.between(currentTime,LocalDateTime.now()).toHours() > 2){
                cache = luckyWheelService.customerAcquireRecord();
                currentTime = LocalDateTime.now();
            }
        });
        return Response.ofSuccess(customerAcquires);
    }

    /**
     * 抽奖页详情信息
     *
     * @return customer detail
     */
    @SessionInterceptor.IntercepterIngore
    @PostMapping("/customer/detail")
    @ApiOperation(value = "抽奖页详细信息")
    @ApiImplicitParam(value = "活动编号", name = "activityCode", required = true, paramType = "query")
    public Response<CustomerDetail> customerDetail() {
        CustomerDetail customerDetail = luckyWheelService.customerCount(GlobalRequestContext.currentPartyId());
        return Response.ofSuccess(customerDetail);
    }

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/rule")
    @ApiOperation(value = "活动说明", httpMethod = "POST")
    @ApiImplicitParam(value = "活动编号", name = "activityCode", required = true, paramType = "query")
    public Response<String> activityRule(@RequestParam(name = "position") UserEnv userEnv) {
        String s = luckyWheelService.activityRule(userEnv);
        return Response.ofSuccess(s);
    }


    /**
     * 查询当前用户的爱心值
     *
     * @return customer detail
     */
    @PostMapping("/customer/queryLv")
    @ApiOperation(value = "查询当前用户的爱心值")
    @ApiImplicitParam(value = "活动编号", name = "activityCode", required = true, paramType = "query")
    public Response<Integer> queryLv() {
        Integer lv = luckyWheelService.queryLv(GlobalRequestContext.currentPartyId());
        return Response.ofSuccess(lv);
    }

}
