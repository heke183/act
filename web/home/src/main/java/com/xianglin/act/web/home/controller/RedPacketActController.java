package com.xianglin.act.web.home.controller;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.biz.shared.RedPacketActService;
import com.xianglin.act.biz.shared.SharerQrCode;
import com.xianglin.act.common.dal.model.redpacket.*;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.WxApiUtils;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.act.web.home.util.WebConstants;
import com.xianglin.core.model.CheckMessageVO;
import com.xianglin.core.model.enums.UserType;
import com.xianglin.act.common.util.BizException;
import com.xianglin.fala.session.Session;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;

/**
 * @author yefei
 * @date 2018-03-30 10:39
 */
@RestController
@RequestMapping("/act/api/activity/redpacket")
@Api(value = "/act/api/activity/redpacket", tags = "现金红包（51活动）接口")
@ApiResponses({@ApiResponse(code = 2000, message = "调用成功"),
        @ApiResponse(code = 3000, message = "业务异常"),
        @ApiResponse(code = 4000, message = "未知错误，系统异常")})
public class RedPacketActController {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(RedPacketActController.class);

    @Resource
    private RedPacketActService redPacketActService;

    @Resource
    private WxApiUtils wxApiUtils;

    /**
     * 查询分享者信息，是否绑定微信（不提示绑定微信），是否有未完成得团（跳到为完成团页面）
     * <p>
     * partyId mobilePhone deviceId
     *
     * @param sharer
     * @return
     */
    @PostMapping("/selectSharer")
    @ApiOperation(value = "进入分享页面后调用, 查询分享者信息")
    public Response<SharerInfo> selectSharer(Sharer sharer) {
        Session session = GlobalRequestContext.getSession();
        if (session == null) {
            throw new BizException(ActPreconditions.ResponseEnum.NO_LOGIN);
        }
        User user = JSON.parseObject(session.getAttribute(WebConstants.XL_QY_USER), Sharer.class);
        if (user == null) {
            throw new BizException(ActPreconditions.ResponseEnum.NO_LOGIN);
        }
        sharer.setMobilePhone(session.getAttribute("loginName"));
        sharer.setDeviceId(session.getAttribute("did"));
        sharer.setPartyId(user.getPartyId());

        SharerInfo sharerInfo = redPacketActService.selectSharerInfo(sharer);
        Response<SharerInfo> response = new Response<>();
        response.setResult(sharerInfo);
        return response;
    }


    /**
     * 分享前调用
     *
     * @param sharerInfo
     * @return
     */
    @PostMapping("/precondition")
    @ApiOperation(value = "创建红包，先决条件判断")
    public Response<SharerInfo> precondition(SharerInfo sharerInfo) {
        Session session = GlobalRequestContext.getSession();
        User user = JSON.parseObject(session.getAttribute(WebConstants.XL_QY_USER), Sharer.class);
        sharerInfo.setPartyId(user.getPartyId());
        sharerInfo.setMobilePhone(session.getAttribute("loginName"));

        SharerInfo sharer = redPacketActService.precondition(sharerInfo);
        Response<SharerInfo> response = new Response<>();
        response.setResult(sharer);
        return response;
    }

    /**
     * 分享前调用
     *
     * @param sharerInfo
     * @return
     */
    @PostMapping("/create")
    @ApiOperation(value = "分享前调用, 获取红包序列和分享页面信息")
    public Response<RedPacket> create(SharerInfo sharerInfo) {
        Session session = GlobalRequestContext.getSession();
        User user = JSON.parseObject(session.getAttribute(WebConstants.XL_QY_USER), Sharer.class);
        sharerInfo.setPartyId(user.getPartyId());
        sharerInfo.setMobilePhone(session.getAttribute("loginName"));

        RedPacket redPacket = redPacketActService.create(sharerInfo);
        Response<RedPacket> response = new Response<>();
        response.setResult(redPacket);
        return response;
    }

    /**
     * 不需要登陆状态
     * <p>
     * 判断openId是否分享者，走分享者逻辑
     * <p>
     * 判断红包是否超过24小时，如超过
     *  分享者在微信中打开：提示去APP签到、晒收入。分享者在APP中打初始状态页。
     *
     * @param sharer
     * @return
     */
    @PostMapping("/isSharer")
    @ApiOperation(value = "验证是否是分享者打开,或者已经存在openId的参与者打开")
    public Response<User> isSharerOrPartaker(Sharer sharer) throws Exception {
        final String openId = wxApiUtils.getOpenId(sharer.getWxOpenId());
        if (openId == null) {
            User user = new User();
            String authUrl = wxApiUtils.getAuthUrl(sharer.getPartyId());
            user.setUrl(authUrl);
            logger.warn("未拿到openId,重新跳转: {}", authUrl);
            throw new BizException(ActPreconditions.ResponseEnum.RP_WX_REDIRECT, user);
        }
        sharer.setWxOpenId(openId);
        User user = redPacketActService.isSharer(sharer);
        Response<User> response = new Response<>();
        response.setResult(user);
        if (logger.isDebugEnabled()) {
            logger.debug("isSharer : {}", JSON.toJSONString(response, WriteMapNullValue));
        }
        return response;
    }


    /**
     * 不需要登陆状态
     * <p>
     * partyId 一天只能帮忙领取一次
     * <p>
     * 1、未注册用户已帮好友领取3次红包，提示：已领好友3次现金红包。达到上限，去APP试试发红包，得微信现金
     * 2、未注册用户成为已注册后，无法享受已注册用户的福利，提示：你已领取新人福利，去APP试试发红包，得微信现金
     * 3、当天已领取，再次领红包时，提示：你今天已经领过好友红包了，去APP试试发红包吧
     *
     * @param partaker mobilePhone wxOpenId partyId
     * @return
     */
    @PostMapping("/openRedPacket")
    @ApiOperation(value = "参与者开红包")
    public Response<Partaker> openRedPacket(Partaker partaker) {
        partaker.setUserType(UserType.RP_PARTAKER.name());
        Partaker result = redPacketActService.openRedPacket(partaker);
        Response<Partaker> response = new Response<>();
        response.setResult(result);
        return response;
    }

    /**
     * 分享者领取微信红包，有可能获得金币
     *
     * @param sharer
     * @return
     */
    @PostMapping("/wxRedPacket")
    @ApiOperation(value = "分享着领取微信红包")
    public Response<SharerInfo> wxRedPacket(Sharer sharer) {
        // session 取partyId
        Session session = GlobalRequestContext.getSession();
        User user = JSON.parseObject(session.getAttribute(WebConstants.XL_QY_USER), Sharer.class);
        sharer.setDeviceId(session.getAttribute("did"));
        sharer.setPartyId(user.getPartyId());

        Response<SharerInfo> response = new Response<>();
        SharerInfo sharerInfo = redPacketActService.wxRedPacket(sharer);
        response.setResult(sharerInfo);
        return response;
    }

    /**
     * 短信验证码发送
     */
    @PostMapping("/send/message")
    @ApiOperation(value = "短信验证码发送")
    public Response<CheckMessageVO> sendMessage(Partaker partaker) {
        Response<CheckMessageVO> response = new Response<>();
        CheckMessageVO checkMessageResult = redPacketActService.sendMessage(partaker);
        response.setResult(checkMessageResult);
        return response;
    }

    /**
     * 校验验证码 (开红包)
     *
     * @param checkMessageVO the check message vo
     */
    @PostMapping("/check/message")
    @ApiOperation(value = "校验验证码")
    public Response<User> checkMessage(CheckMessageVO checkMessageVO) {
        Response<User> response = new Response<>();
        User user = redPacketActService.checkMessage(checkMessageVO);
        response.setResult(user);
        return response;
    }

    /**
     * 二维码页数据
     *
     * @param partyId
     * @return
     */
    @PostMapping("/qr/code")
    @ApiOperation(value = "二维码页数据")
    public Response<SharerQrCode> sharerQrCode(long partyId) {
        Response<SharerQrCode> response = new Response<>();
        SharerQrCode sharerQrCode = redPacketActService.getSharerQrCode(partyId);
        sharerQrCode.setPartyId(partyId);
        response.setResult(sharerQrCode);
        return response;
    }

    @PostMapping("/tipsRecord")
    public void tipsRecord(long partyId) {
        redPacketActService.tipsRecord(partyId);
    }
}
