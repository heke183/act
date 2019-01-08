package com.xianglin.core.service;

import java.util.Optional;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/19 10:55.
 */

public interface SystemGoldCoinService {

    /**
     * 发放金币奖励
     *
     * @param toPartyId
     * @param amout
     * @return
     */
    Optional<Boolean> dispathCoin2People(Long toPartyId, Integer amout);

    /**
     * 收取金币
     *
     * @param fromPartyId
     * @param amout
     * @return
     */
    Optional<Boolean> chargeCoin2System(Long fromPartyId, Integer amout);

    /**
     * 检查账户余额
     *
     * @param partyId
     * @param balance
     * @return
     */
    Optional<Boolean> checkBalance(Long partyId, Integer balance);
}
