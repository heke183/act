package com.xianglin.core.service.impl;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.xianglin.act.common.service.integration.GoldcoinServiceClient;
import com.xianglin.act.common.util.GoldSequenceUtil;
import com.xianglin.cif.common.service.facade.model.Response;
import com.xianglin.cif.common.service.facade.vo.GoldcoinAccountVo;
import com.xianglin.cif.common.service.facade.vo.GoldcoinRecordVo;
import com.xianglin.core.model.enums.ActivityEnum;
import com.xianglin.core.model.exception.attendance.AccountBalanceException;
import com.xianglin.core.service.SystemGoldCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/19 10:55.
 */
@Service
public class SystemGoldCoinServiceImpl implements SystemGoldCoinService {

    public static final long GOLD_SYS_ACCOUNT = 10000L;

    @Autowired
    private GoldcoinServiceClient goldcoinServiceClient;

    @Autowired
    private LongIdGeneratorImpl longIdGenerator;


    @Override
    public Optional<Boolean> dispathCoin2People(Long toPartyId, Integer amount) {

        Preconditions.checkArgument(amount != null, "金币操作金额不能为空");
        Preconditions.checkArgument(toPartyId != null, "金币发送目标人partyId不能为空");
        GoldcoinRecordVo goldcoinRecordVo = new GoldcoinRecordVo();
        goldcoinRecordVo.setSystem("act");
        goldcoinRecordVo.setAmount(amount);
        goldcoinRecordVo.setFronPartyId(GOLD_SYS_ACCOUNT);
        goldcoinRecordVo.setType(ActivityEnum.ATTENDANCE_AWARD.name());
        goldcoinRecordVo.setRemark(ActivityEnum.ATTENDANCE_AWARD.getRemark());
        goldcoinRecordVo.setToPartyId(toPartyId);
        goldcoinRecordVo.setRemark("打卡奖励");
        String sequence = GoldSequenceUtil.getSequence(toPartyId, longIdGenerator.generateId());
        goldcoinRecordVo.setRequestId(sequence);
        Response<GoldcoinRecordVo> goldcoinRecordVoResponse = goldcoinServiceClient.doRecord(goldcoinRecordVo);
        boolean isSuccess = Objects.equal(goldcoinRecordVoResponse.getCode(), 1000);
        if (!isSuccess) {
            throw new RuntimeException("发放奖励异常:" + toPartyId);
        }
        return Optional.of(isSuccess);
    }

    @Override
    public Optional<Boolean> chargeCoin2System(Long fromPartyId, Integer amount) {

        Preconditions.checkArgument(amount != null, "金币操作金额不能为空");
        Preconditions.checkArgument(fromPartyId != null, "金币收取目标人partyId不能为空");
        GoldcoinRecordVo goldcoinRecordVo = new GoldcoinRecordVo();
        goldcoinRecordVo.setSystem("act");
        goldcoinRecordVo.setAmount(-amount);
        goldcoinRecordVo.setFronPartyId(GOLD_SYS_ACCOUNT);
        goldcoinRecordVo.setType(ActivityEnum.ATTENDANCE_AWARD.name());
        goldcoinRecordVo.setRemark(ActivityEnum.ATTENDANCE_AWARD.getRemark());
        goldcoinRecordVo.setToPartyId(fromPartyId);
        goldcoinRecordVo.setRemark("早起打卡");
        String sequence = GoldSequenceUtil.getSequence(fromPartyId, longIdGenerator.generateId());
        goldcoinRecordVo.setRequestId(sequence);
        Response<GoldcoinRecordVo> goldcoinRecordVoResponse = goldcoinServiceClient.doRecordWithBalanceCheck(goldcoinRecordVo, 0);
        boolean isSuccess = Objects.equal(goldcoinRecordVoResponse.getCode(), 1000);
        return Optional.of(isSuccess);
    }

    /**
     * 这种先检查后操作其实没用，因为不是原子的读写操作
     *
     * @param partyId
     * @param balance
     * @return
     */
    @Override
    public Optional<Boolean> checkBalance(Long partyId, Integer balance) {

        Preconditions.checkArgument(partyId != null, "金币操作金额不能为空");
        Preconditions.checkArgument(balance != null, "金币收取目标人partyId不能为空");
        Response<GoldcoinAccountVo> rpcResponse = goldcoinServiceClient.queryAccount(partyId);
        if (Objects.equal(rpcResponse.getCode(), 1000)) {
            GoldcoinAccountVo result = rpcResponse.getResult();
            if (result == null || result.getAmount() < balance) {
                throw new AccountBalanceException("账户余额不足");
            }
        }
        return Optional.of(true);
    }
}
