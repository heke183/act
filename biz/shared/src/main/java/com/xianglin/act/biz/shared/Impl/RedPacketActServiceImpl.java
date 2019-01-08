package com.xianglin.act.biz.shared.Impl;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.common.util.*;
import com.xianglin.core.service.PrizeAwardUtils;
import com.xianglin.act.biz.shared.RedPacketActService;
import com.xianglin.act.biz.shared.SharerQrCode;
import com.xianglin.act.common.dal.enums.PrizeEnum;
import com.xianglin.act.common.dal.mappers.*;
import com.xianglin.act.common.dal.model.Activity;
import com.xianglin.act.common.dal.model.CustomerAcquire;
import com.xianglin.act.common.dal.model.CustomerPrize;
import com.xianglin.act.common.dal.model.Prize;
import com.xianglin.act.common.dal.model.redpacket.*;
import com.xianglin.act.common.service.integration.CustomersInfoServiceClient;
import com.xianglin.act.common.service.integration.MessageServiceClient;
import com.xianglin.act.common.service.integration.PersonalServiceClient;
import com.xianglin.appserv.common.service.facade.model.Request;
import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.enums.Constant;
import com.xianglin.appserv.common.service.facade.model.vo.MsgVo;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import com.xianglin.cif.common.service.facade.model.CustomersDTO;
import com.xianglin.cif.common.service.facade.model.RoleDTO;
import com.xianglin.core.model.CheckMessageVO;
import com.xianglin.core.model.MessagePair;
import com.xianglin.core.model.enums.ActivityEnum;
import com.xianglin.core.model.enums.Constants;
import com.xianglin.core.model.enums.UserType;
import com.xianglin.xlStation.base.enums.XLStationEnums;
import com.xianglin.xlStation.base.model.SmsResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

import static com.xianglin.core.model.enums.UserType.RP_PARTAKER;
import static com.xianglin.core.model.enums.UserType.RP_PARTAKER_OLD;

/**
 * @author yefei
 * @date 2018-04-02 15:47
 */
@Service
public class RedPacketActServiceImpl implements RedPacketActService, InitializingBean {

    private final static String MESSAGE_CONTENT = "你的验证码是#{XXX}，如非本人操作，请忽略本短信";

    private final static String UN_CPL_MESSAGE = "你的红包好友没领完，红包发放失败，去APP继续发红包，发到微信群或好友效果会更好";

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(RedPacketActServiceImpl.class);

    /**
     * 消息
     */
    private final static DelayQueue<MessagePair<RedPacket, String>> DELAY_QUEUE_MESSAGE = new DelayQueue<>();
    private final static int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors() << 1;
    private final static ExecutorService EXECUTOR = new ThreadPoolExecutor(DEFAULT_THREADS, DEFAULT_THREADS << 10,
            128L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private final ActivityEnum redPacket = ActivityEnum.RED_PACKET_V2;

    @Resource
    private SequenceMapper sequenceMapper;
    @Resource
    private RedPacketMapper redPacketMapper;
    @Resource
    private SharerMapper sharerMapper;
    @Resource
    private CustomerPrizeMapper customerPrizeMapper;
    @Resource
    private RedPacketPartakerMapper redPacketPartakerMapper;
    @Resource
    private CustomerAcquireRecordMapper customerAcquireRecordMapper;
    @Resource
    private PersonalServiceClient personalServiceClient;
    @Resource
    private MessageServiceClient messageServiceClient;
    @Resource
    private CustomersInfoServiceClient customersInfoServiceClient;
    @Resource
    private RedPacketImages redPacketImages;
    @Resource
    private PrizeAwardUtils prizeAwardUtils;
    @Resource
    private WxApiUtils wxApiUtils;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<RedPacket> redPackets = redPacketMapper.selectAllUnExpireRedPacketOfDay();
        for (RedPacket redPacket : redPackets) {
            MessagePair<RedPacket, String> messagePair = new MessagePair<>(
                    redPacket,
                    UN_CPL_MESSAGE,
                    redPacket.getExpireDate(),
                    2000);
            DELAY_QUEUE_MESSAGE.put(messagePair);
        }
        EXECUTOR.submit(() -> {
            for (; ; ) {
                MessagePair<RedPacket, String> pair = DELAY_QUEUE_MESSAGE.take();
                Request<MsgVo> param = new Request<>();
                RedPacket redPacket = pair.getA();
                RedPacket redPacket1 = redPacketMapper.selectRedPacket(redPacket.getPacketId());
                if ("Y".equals(redPacket1.getIsComplete())) {
                    continue;
                }
                logger.info("红包活动通知：{}, redPacket:{}", pair.getB(), redPacket);
                param.setReq(MsgVo.builder()
                        .partyId(redPacket.getPartyId())
                        .msgTitle("红包活动通知")
                        .isSave(Constant.YESNO.YES)
                        .message(pair.getB())
                        .msgType(Constant.MsgType.CASHBONUS_TIP.name())
                        .loginCheck(Constant.YESNO.NO.code)
                        .passCheck(Constant.YESNO.NO.code).expiryTime(0)
                        .isDeleted("N")
                        .msgSource(Constant.MsgType.CASHBONUS_TIP.name()).build());

                com.xianglin.appserv.common.service.facade.model.Response<Boolean> booleanResponse =
                        messageServiceClient.sendMsg(param, Arrays.asList(new Long[]{redPacket.getPartyId()}));
                com.google.common.base.Optional<Boolean> aBoolean = com.xianglin.appserv.common.service.facade.model.Response.checkResponse(booleanResponse);
                if (!(aBoolean.get())) {
                    logger.error("给分享者发送红包失败消息失败：{}", JSON.toJSONString(booleanResponse));
                }
            }
        });
    }

    @Override
    public SharerInfo selectSharerInfo(Sharer sharer) {
        checkActExpire();
        // 分享者
        SharerInfo sharerInfo = sharerMapper.selectSharerByPartyId(sharer.getPartyId());
        if (sharerInfo != null) {
            if (StringUtils.isBlank(sharerInfo.getSharerQrCode())) {
                try {
                    String shareImage = redPacketImages.generateShareImage(sharer.getPartyId());
                    logger.info("cdn image again: {}", shareImage);
                    sharer.setSharerQrCode(shareImage);
                    sharerMapper.updateSharer(sharer);
                } catch (Exception e) {
                    logger.error("生成图片异常", e);
                }
            }

            RedPacketInfo redPacketInfo;
            // 满五次直接到最终页面
            List<RedPacketInfo> redPackets = redPacketMapper.selectCompleteRedPacketOfDay(sharer.getPartyId());
            if (redPackets.size() >= 5) {
                redPacketInfo = redPackets.get(0);
            } else {
                // 分享的红包
                redPacketInfo = redPacketMapper.selectUnExpireRedPacketOfDay(sharer.getPartyId());
            }

            // 成团已经领取到分享页面
            if (redPacketInfo != null) {
                if (!(redPacketInfo.isComplete() && StringUtils.isNotBlank(redPacketInfo.getMemcCode())) || redPackets.size() >= 5) {
                    sharerInfo.setRedPacket(redPacketInfo);
                    // 红包的参与者
                    List<PartakerInfo> partakers = redPacketPartakerMapper.selectRedPacketPartakerInfo(redPacketInfo.getPacketId());
                    redPacketInfo.setPartakers(partakers);
                    if (StringUtils.isNotBlank(redPacketInfo.getMemcCode())) {
                        sharerInfo.setCustomerAcquire(customerAcquireRecordMapper.selectByMemcCode(redPacketInfo.getMemcCode()));
                    } else if (redPacketInfo.isComplete()) {
                        sharerInfo = wxRedPacket(sharer);
                        sharerInfo.setRedPacket(redPacketInfo);
                    }
                }
            }

        } else {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            EXECUTOR.submit(() -> {
                try {
                    String shareImage = redPacketImages.generateShareImage(sharer.getPartyId());
                    logger.info("cdn image: {}", shareImage);
                    sharer.setSharerQrCode(shareImage);
                    countDownLatch.await();
                    sharerMapper.updateSharer(sharer);
                } catch (Exception e) {
                    logger.error("生成图片异常", e);
                }
            });
            sharerMapper.insertSharer(sharer);
            countDownLatch.countDown();
            BeanUtils.copyProperties(sharer, sharerInfo = new SharerInfo());
        }
        try {
            sharerInfo.setUrl(wxApiUtils.getAuthUrl(sharer.getPartyId()));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        return sharerInfo;
    }

    @Override
    public RedPacket create(SharerInfo sharerInfo) {
        checkActExpire();
        RedPacketInfo redPacketDO = redPacketMapper.selectUnExpireRedPacketOfDay(sharerInfo.getPartyId());
        if (redPacketDO != null && !redPacketDO.isComplete()) {
            logger.warn("存在未过期的团!");
            return redPacketDO;
        }
        RLock lock = redissonClient.getLock("ACT:CREATE_RP:" + sharerInfo.getPartyId());
        if (!lock.tryLock()) {
            throw new BizException(ActPreconditions.ResponseEnum.REPEAT);
        }
        String packetId = sequenceMapper.getSequence() + "";
        try {
            redPacketDO = new RedPacketInfo();
            if (sharerInfo.isChecked()) {
                // 通过先决条件
                redPacketDO.setIsChecked("Y");
            } else {
                redPacketDO.setIsChecked("N");
            }
            // 创建团
            redPacketDO.setPacketId(packetId);
            redPacketDO.setPartyId(sharerInfo.getPartyId());
            redPacketDO.setPacketInfo(PacketInfoList.getInstance().get());

            redPacketMapper.createRedPacket(redPacketDO);
            if (StringUtils.isNotBlank(sharerInfo.getWxOpenId())) {
                sharerMapper.updateSharer(sharerInfo);
            }
            sharerMapper.updateSharerOpenIdUnion();
        } finally {
            lock.unlock();
        }

        RedPacketInfo redPacketInfo = new RedPacketInfo();
        BeanUtils.copyProperties(redPacketDO, redPacketInfo);
        SharerInfo sharerInfoInternal = sharerMapper.selectSharerByPartyId(sharerInfo.getPartyId());
        redPacketInfo.setSharer(sharerInfoInternal);

        RedPacket redPacket = redPacketMapper.selectRedPacket(packetId);
        MessagePair<RedPacket, String> messagePair = new MessagePair<>(
                redPacket,
                UN_CPL_MESSAGE,
                redPacket.getExpireDate(),
                2000);
        DELAY_QUEUE_MESSAGE.put(messagePair);

        return redPacketInfo;
    }

    @Override
    public SharerInfo precondition(SharerInfo sharerInfo) {
        checkActExpire();
        // 验证 好友数、是否发过微博、是否签到晒收入
        Response<Map<String, Object>> mapResponse = personalServiceClient.queryUserSignAndSubjectAndFollow(sharerInfo.getPartyId());
        Map<String, Object> stringObjectMap = Response.checkResponse(mapResponse).get();

        if (stringObjectMap == null) {
            logger.error("查询app签到信息异常.");
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        } else {
            List<RedPacket> redPackets = redPacketMapper.selectCompleteRedPacketUnchecked(sharerInfo.getPartyId());
            if (redPackets.size() >= 2) {
                sharerInfo.setOutOf(true);
            }
            // 你好友未超过10人，去关注好友再来
            boolean isFollow = (boolean) stringObjectMap.get("isFollow");
            // 你从未发过微博，去发布微博再来
            boolean isPublishArticle = (boolean) stringObjectMap.get("isPublishArticle");
            // 你今天还没签到晒收入
            boolean isSign = ((boolean) stringObjectMap.get("isSign"))
                    && ((boolean) stringObjectMap.get("isShareIncome"));

            if (!isFollow && isPublishArticle && isSign) {
                throw new BizException(ActPreconditions.ResponseEnum.RP_UNFOLLOW, sharerInfo);
            }
            if (isFollow && !isPublishArticle && isSign) {
                throw new BizException(ActPreconditions.ResponseEnum.RP_NO_PUBLISH_ARTICLE, sharerInfo);
            }
            if (isFollow && isPublishArticle && !isSign) {
                throw new BizException(ActPreconditions.ResponseEnum.RP_NO_SIGN, sharerInfo);
            }

            if (!isFollow && !isPublishArticle && isSign) {
                throw new BizException(ActPreconditions.ResponseEnum.RP_CREATE_CHECKED0, sharerInfo);
            }

            if (isFollow && !isPublishArticle && !isSign) {
                throw new BizException(ActPreconditions.ResponseEnum.RP_CREATE_CHECKED1, sharerInfo);
            }

            if (!isFollow && isPublishArticle && !isSign) {
                throw new BizException(ActPreconditions.ResponseEnum.RP_CREATE_CHECKED2, sharerInfo);
            }

            if (!isFollow && !isPublishArticle && !isSign) {
                throw new BizException(ActPreconditions.ResponseEnum.RP_CREATE_CHECKED3, sharerInfo);
            }
        }
        return sharerInfo;
    }

    @Override
    public Partaker openRedPacket(Partaker partaker) {
        checkActExpire();
        // 生成随机红包或者优惠卷，插入记录
        RedPacket redPacket = redPacketMapper.selectLastRedPacket(partaker.getSharerPartyId());

        if (redPacket == null) {
            // 正常逻辑走不到这里, 防止刷接口的
            logger.error("无效的团: " + partaker.getPacketId());
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
        partaker.setPacketId(redPacket.getPacketId());
        // 开红包记录 重复打开更新打开时间
        PartakerInfo partakerInfo = redPacketPartakerMapper.selectLastOpenedRedPacket(partaker.getPacketId(), partaker.getPartyId());
        UserType userType = UserType.parse(partaker.getUserType());
        if (partakerInfo != null && userType == RP_PARTAKER) {
            redPacketPartakerMapper.updateRedPacketOpenDate(partaker.getPartyId(), partaker.getPacketId());
        } else {
            switch (userType) {
                case RP_PARTAKER: {
                    // partyId一天只能帮忙领取一次
                    checkPartakerCount(partaker);
                    List<CustomerPrize> customerPrizes = customerPrizeMapper.selectCustomerPrize(
                            this.redPacket.name(),
                            RP_PARTAKER.name());

                    if (customerPrizes.isEmpty()) {
                        logger.error("无效的奖品配置！");
                        throw new BizException(ActPreconditions.ResponseEnum.ERROR);
                    }
                    BigDecimal offset = new BigDecimal(ThreadLocalRandom.current().nextDouble());
                    Prize prize = null;
                    // 按加权随机算 概率可以大于1
                    for (int i = 0; i < customerPrizes.size(); i++) {
                        CustomerPrize customerPrize = customerPrizes.get(i);
                        if ((offset = offset.subtract(customerPrize.getProbability())).compareTo(BigDecimal.ZERO) < 0) {
                            BigDecimal amount = null;
                            if (customerPrize.getMaxValue().compareTo(customerPrize.getMinValue()) == 0) {
                                amount = customerPrize.getMaxValue();
                            } else {
                                // 随机生成奖品的值
                                BigDecimal add = new BigDecimal(Math.random()).multiply(customerPrize.getMaxValue().subtract(customerPrize.getMinValue()));
                                amount = customerPrize.getMinValue().add(add);
                            }
                            prize = new Prize();
                            PrizeEnum parse = PrizeEnum.parse(customerPrize.getPrizeCode());
                            prize.setPrizeEnum(parse);
                            prize.setAmount(amount.divide(new BigDecimal(parse.getUnit())).setScale(customerPrize.getRemainValue(), BigDecimal.ROUND_HALF_UP));
                            break;
                        }
                    }
                    partaker.setPrizeCode(prize.getPrizeCode());
                    partaker.setPrizeValue(prize.getAmount());
                }
                case RP_PARTAKER_OLD: {
                    redPacketPartakerMapper.insert(partaker);
                    partakerInfo = new PartakerInfo();
                    BeanUtils.copyProperties(partaker, partakerInfo);
                    partakerInfo.setMockFootList(MockFootList.getInstance());
                    partakerInfo.setMockTitleList(MockTitleList.getInstance());
                    MockFootList.MockFoot mockFoot = getMockFoot(partaker.getSharerPartyId());
                    partakerInfo.getMockFootList().add(0, mockFoot);
                    if (userType == RP_PARTAKER_OLD) {
                        throw new BizException(ActPreconditions.ResponseEnum.RP_OLD_USER, partakerInfo);
                    }
                }
                break;
                default:
                    throw new UnsupportedOperationException(userType.name());

            }
        }
        return partakerInfo;
    }

    @Override
    public SharerInfo wxRedPacket(Sharer sharer) {
        checkActExpire();
        RLock lock = redissonClient.getLock("ACT:RED_PACKET:" + sharer.getPacketId());
        if (!lock.tryLock()) {
            throw new BizException(ActPreconditions.ResponseEnum.REPEAT);
        }
        try {
            // 验证团是否完成
            RedPacketInfo redPacket = redPacketMapper.selectLastRedPacket(sharer.getPartyId());
            if (redPacket == null || redPacket.getPartyId() != sharer.getPartyId()
                    || !redPacket.isComplete() || StringUtils.isNotBlank(redPacket.getMemcCode())) {
                logger.error("wxRedPacket error!");
                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
            }

            SharerInfo sharerInfo = sharerMapper.selectSharerByPartyId(sharer.getPartyId());
            if (sharerInfo == null) {
                logger.error("数据异常,不存在的分享者.");
                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
            }

            // 奖励金币
            CustomerPrize customerPrize = customerPrizeMapper.selectCustomerPrizeUnique(
                    this.redPacket.name(),
                    UserType.RP_SHARER.name(),
                    PrizeEnum.XL_GOLD_COIN.name()
            );

            // 随机生成奖品的值
            BigDecimal add = new BigDecimal(Math.random()).multiply(customerPrize.getMaxValue().subtract(customerPrize.getMinValue()));
            BigDecimal amount = customerPrize.getMinValue().add(add);
            Prize prize = new Prize();
            PrizeEnum parse = PrizeEnum.parse(customerPrize.getPrizeCode());
            prize.setPrizeEnum(parse);
            prize.setAmount(amount.divide(new BigDecimal(parse.getUnit())).setScale(customerPrize.getRemainValue(), BigDecimal.ROUND_HALF_UP));
            // 发放红包或者金币
            prizeAwardUtils.award(sharerInfo, prize);

            if (StringUtils.isBlank(prize.getMemcCode())) {
                throw new BizException("奖品方法失败, prize: " + prize.getPrizeEnum().name());
            }
            CustomerAcquire customerAcquire = new CustomerAcquire();
            customerAcquire.setMobilePhone(sharer.getMobilePhone());
            customerAcquire.setPartyId(sharer.getPartyId());
            customerAcquire.setPrizeCode(prize.getPrizeCode());
            customerAcquire.setActivityCode(this.redPacket.name());
            customerAcquire.setMemcCode(prize.getMemcCode());
            customerAcquire.setPrizeValue(prize.getAmount());
            customerAcquire.setUserType(UserType.RP_SHARER.name());

            customerAcquireRecordMapper.insertCustomerAcquireRecord(customerAcquire);
            // 更新 红包分享者的奖励
            redPacketMapper.updateRedpacketMemcCode(prize.getMemcCode(), redPacket.getPacketId());

            sharerInfo.setCustomerAcquire(customerAcquire);
            return sharerInfo;
        } finally {
            lock.unlock();
            if (customerAcquireRecordMapper.isAlarm()) {
                logger.error("单位时间内红包个数过多。");
                messageServiceClient.sendSmsByTemplate(
                        "17612163703",
                        "单位时间内红包个数过多。",
                        new String[0]);
            }
        }
    }

    private void checkActExpire() {
        Activity activity = activityMapper.selectActivity(redPacket.name());
        if (activity.getExpireDate().before(new Date())) {
            throw new BizException(ActPreconditions.ResponseEnum.ACTIVITY_END);
        }
    }

    @Override
    public CheckMessageVO sendMessage(Partaker partaker) {
        checkActExpire();
        com.xianglin.xlStation.base.model.Response response =
                messageServiceClient.sendSmsCode(
                        partaker.getMobilePhone(),
                        MESSAGE_CONTENT,
                        String.valueOf(60 * 30));

        if (!(XLStationEnums.ResultSuccess.getCode() == response.getBussinessCode())) {
            logger.error("验证码发送失败：{}", JSON.toJSONString(response));
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
        // 验证码发送成功
        CheckMessageVO checkMessageResult = new CheckMessageVO();
        checkMessageResult.setMobilePhone(partaker.getMobilePhone());

        return checkMessageResult;
    }

    @Override
    public User checkMessage(CheckMessageVO checkMessageVO) {
        checkActExpire();
        if (StringUtils.isBlank(checkMessageVO.getOpenId())
                || StringUtils.isBlank(checkMessageVO.getMobilePhone())) {
            logger.error("openId or mobile phone is null!");
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
        // 开户
        CustomersDTO customersDTO = new CustomersDTO();
        customersDTO.setMobilePhone(checkMessageVO.getMobilePhone());
        customersDTO.setCreator(checkMessageVO.getMobilePhone());
        com.xianglin.cif.common.service.facade.model.Response<CustomersDTO> openAccountResponse =
                customersInfoServiceClient.openAccount(customersDTO, Constants.SYSTEM_NAME);
        if (!openAccountResponse.isSuccess()) {
            logger.error("用户开户失败: {}", JSON.toJSONString(openAccountResponse));
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }

        // 查詢角色
        com.xianglin.cif.common.service.facade.model.Response<CustomersDTO> customerResponse =
                customersInfoServiceClient.selectByPartyId(openAccountResponse.getResult().getPartyId());
        if (!customerResponse.isSuccess()) {
            logger.error("根据partyId查询用户信息失败: {}", JSON.toJSONString(customerResponse));
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
        CustomersDTO customer = customerResponse.getResult();

        SmsResponse smsResponse = messageServiceClient.checkSmsCode(checkMessageVO.getMobilePhone(), checkMessageVO.getCode(), Boolean.TRUE);
        if (!(XLStationEnums.ResultSuccess.getCode() == smsResponse.getBussinessCode())) {
            throw new BizException(ActPreconditions.ResponseEnum.CHECK_MESSAGE_FAIL);
        }

        // 校验是否是分享者自己打开链接
        RedPacketInfo redPacket = redPacketMapper.selectLastRedPacket(customer.getPartyId());

        if (redPacket != null) {
            // 分享者
            SharerInfo sharerInfo = sharerMapper.selectSharerByPartyId(redPacket.getPartyId());
            if (StringUtils.isBlank(sharerInfo.getWxOpenId())) {
                sharerInfo.setWxOpenId(checkMessageVO.getOpenId());
                sharerMapper.updateSharer(sharerInfo);
            }
            // 分享的红包
            sharerInfo.setRedPacket(redPacket);
            // 红包的参与者
            List<PartakerInfo> partakers = redPacketPartakerMapper.selectRedPacketPartakerInfo(redPacket.getPacketId());
            redPacket.setPartakers(partakers);
            // 滚动信息
            sharerInfo.setMockFootList(MockFootList.getInstance());
            sharerInfo.setMockTitleList(MockTitleList.getInstance());
            MockFootList.MockFoot mockFoot = getMockFoot(redPacket.getPartyId());
            sharerInfo.getMockFootList().add(0, mockFoot);
            return sharerInfo;
        } else {
            Partaker partaker = new Partaker();
            partaker.setUserType(RP_PARTAKER.name());
            try {
                isRegistered(checkMessageVO.getFromPartyId(), customer);
            } catch (BizException e) {
                partaker.setUserType(RP_PARTAKER_OLD.name());
            }
            // 用户打开
            partaker.setPartyId(customer.getPartyId());
            partaker.setMobilePhone(checkMessageVO.getMobilePhone());
            partaker.setWxOpenId(checkMessageVO.getOpenId());
            partaker.setPacketId(checkMessageVO.getPacketId());
            partaker.setSharerPartyId(checkMessageVO.getFromPartyId());
            return openRedPacket(partaker);
        }
    }

    private void isRegistered(long partyId, CustomersDTO customersDTO) {
        if (CollectionUtils.isNotEmpty(customersDTO.getRoleDTOs())) {
            for (RoleDTO roleDTO : customersDTO.getRoleDTOs()) {
                if (Constants.APP_USER.equals(roleDTO.getRoleCode())) {
                    // 已经注册用户打开
                    PartakerInfo result = new PartakerInfo();
                    result.setMockFootList(MockFootList.getInstance());
                    result.setMockTitleList(MockTitleList.getInstance());
                    result.setPartyId(customersDTO.getPartyId());

                    MockFootList.MockFoot mockFoot = getMockFoot(partyId);
                    result.getMockFootList().add(0, mockFoot);

                    throw new BizException(ActPreconditions.ResponseEnum.RP_OLD_USER, result);
                }
            }
        }
    }

    private MockFootList.MockFoot getMockFoot(long partyId) {
        CustomerAcquire customerAcquire = customerAcquireRecordMapper.selectAcquireAmount(partyId);
        Response<UserVo> userVoResponse = personalServiceClient.queryUser(partyId);
        if (customerAcquire.getPrizeValue().compareTo(BigDecimal.ZERO) == 0) {
            customerAcquire.setPrizeValue(new BigDecimal(10));
        }
        UserVo result = userVoResponse.getResult();
        return new MockFootList.MockFoot(
                result.getHeadImg(),
                result.getShowName(),
                "13:20",
                customerAcquire.getPrizeValue().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
        );
    }

    @Override
    public User isSharer(Sharer sharer) {
        checkActExpire();
        MockFootList.MockFoot mockFoot = getMockFoot(sharer.getPartyId());
        // 分享者
        User user = sharerMapper.selectSharerByOpenId(sharer.getWxOpenId());
        RedPacketInfo userRedPacket = null;
        if (user != null) {
            // 分享的红包
            userRedPacket = redPacketMapper.selectLastRedPacket(user.getPartyId());
        }

        if (user != null && userRedPacket != null) {
            SharerInfo sharerInfo = (SharerInfo) user;
            sharerInfo.setRedPacket(userRedPacket);
            sharerInfo.setMockFootList(MockFootList.getInstance());
            sharerInfo.setMockTitleList(MockTitleList.getInstance());
            sharerInfo.getMockFootList().add(0, mockFoot);
            // 红包的参与者
            List<PartakerInfo> partakers = redPacketPartakerMapper.selectRedPacketPartakerInfo(userRedPacket.getPacketId());
            if (!partakers.isEmpty()) {
                userRedPacket.setPartakers(partakers);
            }
        } else {

            PartakerInfo partaker = redPacketPartakerMapper.selectRedPacketPartakerUnique(sharer.getWxOpenId());
            if (partaker == null) {
                logger.info("跳转到手机验证页面！");
                user = new User();
                user.setWxOpenId(sharer.getWxOpenId());
                try {
                    user.setUrl(wxApiUtils.getAuthUrl(sharer.getPartyId()));
                } catch (UnsupportedEncodingException e) {
                    logger.error(e.getMessage(), e);
                }
                return user;
            }

            com.xianglin.cif.common.service.facade.model.Response<CustomersDTO> customer =
                    customersInfoServiceClient.selectByPartyId(partaker.getPartyId());
            if (!customer.isSuccess()) {
                logger.error("根据partyId查询用户信息失败: {}", JSON.toJSONString(customer));
                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
            }
            CustomersDTO customersDTO = customer.getResult();
            // 判断是否是老用户
            isRegistered(sharer.getPartyId(), customersDTO);

            // 判断是否已经开过红包
            RedPacket redPacket = redPacketMapper.selectLastRedPacket(sharer.getPartyId());
            if (redPacket != null) {
                PartakerInfo partakerInfo = redPacketPartakerMapper
                        .selectLastOpenedRedPacket(redPacket.getPacketId(), partaker.getPartyId());
                if (partakerInfo != null) {
                    // 更新重复打开
                    redPacketPartakerMapper.updateRedPacketOpenDate(partaker.getPartyId(), partaker.getPacketId());
                    partaker = partakerInfo;
                } else {
                    // 参与者打开别的红包 验证条件
                    partaker.setSharerPartyId(sharer.getPartyId());
                    checkPartakerCount(partaker);
                }
            }
            logger.info("直接到开页面。");
            SharerInfo sharerInfo = sharerMapper.selectSharerByPartyId(sharer.getPartyId());

            partaker.setSharerQrCode(sharerInfo.getSharerQrCode());
            partaker.setMockFootList(MockFootList.getInstance());
            partaker.setMockTitleList(MockTitleList.getInstance());
            partaker.getMockFootList().add(0, mockFoot);

            user = partaker;
        }
        try {
            user.setUrl(wxApiUtils.getAuthUrl(sharer.getPartyId()));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        user.setWxOpenId(sharer.getWxOpenId());
        return user;
    }

    private void checkPartakerCount(Partaker partaker) {
        MockFootList.MockFoot mockFoot = getMockFoot(partaker.getSharerPartyId());
        // 新用户只能开一个团
        List<Partaker> partakers = redPacketPartakerMapper.selectRedPacketPartaker(partaker);
        if (!partakers.isEmpty()) {
            PartakerInfo result = new PartakerInfo();
            result.setMockFootList(MockFootList.getInstance());
            result.setMockTitleList(MockTitleList.getInstance());
            result.getMockFootList().add(0, mockFoot);
            throw new BizException(ActPreconditions.ResponseEnum.RP_ALREADY_OPEN, result);
        }
    }

    @Override
    public SharerQrCode getSharerQrCode(long partyId) {
        checkActExpire();
        SharerQrCode sharerQrCode = new SharerQrCode();
        Response<UserVo> userVoResponse = personalServiceClient.queryUser(partyId);
        com.google.common.base.Optional<UserVo> userVo = Response.checkResponse(userVoResponse);
        if (!Objects.equals(userVo.get(), null)) {
            sharerQrCode.setHeadImage(userVo.get().getHeadImg());
            sharerQrCode.setNickName(userVo.get().getShowName());
            try {
                String s = QRUtils.qrCreate(wxApiUtils.getAuthUrl(partyId));
                logger.info("二维码：{}", s);
                sharerQrCode.setUrl(s);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
            }
        }
        return sharerQrCode;
    }


    @Override
    public void tipsRecord(long partyId) {
        RSet<Object> set = redissonClient.getSet("ACT:SET:TIPS");
        set.expire(24, TimeUnit.HOURS);
        set.add(partyId);
    }

}
