package com.xianglin.core.service.filter;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.common.dal.enums.lucky.wheel.RegularCustomPrize;
import com.xianglin.act.common.dal.model.Prize;
import com.xianglin.act.common.service.integration.MessageServiceClient;
import com.xianglin.appserv.common.service.facade.model.Request;
import com.xianglin.appserv.common.service.facade.model.enums.Constant;
import com.xianglin.appserv.common.service.facade.model.vo.MsgVo;
import com.xianglin.core.model.MessagePair;
import com.xianglin.core.model.Player;
import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;
import com.xianglin.core.model.enums.CustomerTypeEnum;
import com.xianglin.xlStation.base.enums.XLStationEnums;
import com.xianglin.xlStation.base.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * 后置处理 中奖后短信发送，站内信提醒
 *
 * @author yefei
 * @date 2018-02-02 10:35
 */
public class AfterProcessFilter implements Filter {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(AfterProcessFilter.class);

    private final static String NODE_MESSAGE_TITLE = "七夕活动奖励";

    private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 消息
     */
    private final static DelayQueue<MessagePair<Player, Prize>> DELAY_QUEUE_MESSAGE = new DelayQueue<>();

    /**
     * cpu * 2 个核心线程，无界柱塞队列
     */
    private final static ExecutorService EXECTOR = new ThreadPoolExecutor(DEFAULT_THREADS, DEFAULT_THREADS,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Resource
    private MessageServiceClient messageServiceClient;

    @Override
    public void doFilter(ActivityRequest<?> request, ActivityResponse response, FilterChain filterChain) {
        filterChain.doFilter(request, response);
        logger.info("---- >AfterProcessFilter 后置处理，用户抽中将：{}", JSON.toJSONString(response));
        // send message
        Player player = (Player) request.getRequest();
        Prize prize = response.getPrize();
        DELAY_QUEUE_MESSAGE.put(new MessagePair<>(player, prize));
        EXECTOR.execute(new Message());
    }

    class Message implements Runnable {

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                MessagePair<Player, Prize> messagePair = null;
                try {
                    messagePair = DELAY_QUEUE_MESSAGE.take();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
                Player player = messagePair.getA();
                Prize prize = messagePair.getB();

                // 新用户只发短信，不管奖品等级
                if (player.getCustomerType() == CustomerTypeEnum.NEW_CUSTOMER) {
                    // 短信
                    String message = "恭喜你，获得#{XXX}，戳 http://suo.im/5d9tOA 领取! 回复TD退订";
                    sendMessage(player, prize, message);
                } else {
                    String message = null;
                    if (prize.getPrizeCode().equals(RegularCustomPrize.SECOND_PRIZE.name())) {
                        message = "恭喜你，获得七夕节二等奖 OPPO 手机一个，赶紧去我的优惠券中下单填写地址，0 元支付即可，如 5 日内未填写，视为作废!";
                    } else if (prize.getPrizeCode().equals(RegularCustomPrize.THIRD_PRIZE.name())){
                        message = "恭喜你，获得七夕节三等奖 300 元现金，以金币形式放到你账户，赶紧去看看，记得去广场上晒晒!";
                    }

                    if (message != null) {
                        //sendMessage(player, prize, message);
                        //sendNodeMessage(player, prize, message);
                    }
                }
            }
        }

        private void sendNodeMessage(Player player, Prize prize, String message) {
            Request<MsgVo> param = new Request<>();
            param.setReq(MsgVo.builder()
                    .partyId(player.getPartyId())
                    .msgTitle(NODE_MESSAGE_TITLE)
                    .isSave(Constant.YESNO.YES)
                    .message(message)
                    .msgType(Constant.MsgType.LUCKDRAW_TIP.name())
                    .loginCheck(Constant.YESNO.NO.code)
                    .passCheck(Constant.YESNO.NO.code).expiryTime(0)
                    .isDeleted("N")
                    .msgSource(Constant.MsgType.LUCKDRAW_TIP.name()).build());

            com.xianglin.appserv.common.service.facade.model.Response<Boolean> booleanResponse = messageServiceClient.sendMsg(param, Arrays.asList(new Long[]{player.getPartyId()}));
            if (!(booleanResponse.getResult())) {
                logger.error("中奖消息站内推送失败：{}", JSON.toJSONString(booleanResponse));
            }
        }

        private void sendMessage(Player player, Prize prize, String message) {
            Response sendSmsResponse = messageServiceClient.sendSmsByTemplate(player.getMobilePhone(), message,
                    new String[]{prize.getPrizeDesc()});
            if (!(XLStationEnums.ResultSuccess.getCode() == sendSmsResponse.getBussinessCode())) {
                logger.error("中奖消息短信发送失败：{}", JSON.toJSONString(sendSmsResponse));
            }
        }
    }


}
