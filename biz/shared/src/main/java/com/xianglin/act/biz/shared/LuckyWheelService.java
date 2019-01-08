package com.xianglin.act.biz.shared;

import com.xianglin.act.common.dal.model.CustomerAcquire;
import com.xianglin.act.common.dal.model.Prize;
import com.xianglin.core.model.CheckMessageVO;
import com.xianglin.core.model.CustomerDetail;
import com.xianglin.core.model.Player;
import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.enums.UserEnv;

import java.util.Set;

/**
 *
 * @author yefei
 * @date 2018 -01-18 13:45
 */
public interface LuckyWheelService {

    /**
     * Start prize.
     *
     * @param request the request
     * @return the prize
     */
    Prize start(ActivityRequest<Player> request);

    /**
     * Customer acquire record list.
     *
     * @return the list
     */
    Set<CustomerAcquire> customerAcquireRecord();


    /**
     * 抽奖页用户详情
     *
     * @param partyId the party id
     * @return the customer detail
     */
    CustomerDetail customerCount(Long partyId);

    /**
     * 短信验证码发送和开户
     *
     * @return the check message vo
     */
    void sendMessage(Player player);

    /**
     * 验证短信验证码
     *
     * @param checkMessageVO the check message vo
     */
    CheckMessageVO checkMessage(CheckMessageVO checkMessageVO);

    /**
     * 活动规则
     *
     * @param userEnv
     */
    String activityRule(UserEnv userEnv);

    /**
     * 查询当前用户的爱心值
     * @param partyId
     * @return
     */
    Integer queryLv(Long partyId);
}
