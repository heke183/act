package com.xianglin.act.common.service.integration.impl;

import com.xianglin.act.common.service.integration.GoldcoinServiceClient;
import com.xianglin.cif.common.service.facade.GoldcoinService;
import com.xianglin.cif.common.service.facade.model.Response;
import com.xianglin.cif.common.service.facade.vo.GoldcoinAccountVo;
import com.xianglin.cif.common.service.facade.vo.GoldcoinRecordVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author yefei
 * @date 2018-01-22 19:23
 */
@Service
public class GoldcoinServiceClientImpl implements GoldcoinServiceClient {

    @Resource
    private GoldcoinService goldcoinService;

    @Override
    public Response<GoldcoinAccountVo> queryAccount(Long partyId) {

        return goldcoinService.queryAccount(partyId);
    }

    @Override
    public Response<List<GoldcoinAccountVo>> queryAccountList(String type, int startPage, int pageSize) {

        return goldcoinService.queryAccountList(type, startPage, pageSize);
    }

    @Override
    public Response<BigDecimal> queryIncome(Long partyId) {

        return goldcoinService.queryIncome(partyId);
    }

    @Override
    public Response<Integer> queryTotalAmount(Long partyId, String type) {

        return goldcoinService.queryTotalAmount(partyId, type);
    }

    @Override
    public Response<GoldcoinRecordVo> doRecord(GoldcoinRecordVo req) {

        return goldcoinService.doRecord(req);
    }

    @Override
    public Response<GoldcoinRecordVo> doRecordWithBalanceCheck(GoldcoinRecordVo req, int limitValue) {

        return goldcoinService.doRecordWithBalanceCheck(req, limitValue);
    }

    @Override
    public Response<List<GoldcoinRecordVo>> queryRecord(GoldcoinRecordVo req) {

        return goldcoinService.queryRecord(req);
    }
}
