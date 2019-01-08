package com.xianglin.act.common.service.integration;

import com.xianglin.cif.common.service.facade.model.Response;
import com.xianglin.cif.common.service.facade.vo.GoldcoinAccountVo;
import com.xianglin.cif.common.service.facade.vo.GoldcoinRecordVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yefei
 * @date 2018-01-22 19:22
 */
public interface GoldcoinServiceClient {

    /**
     * 根据partyId查询金币账户
     * 如果不存在则创建一个0账户兵返回
     *
     * @param partyId
     * @return
     */
    Response<GoldcoinAccountVo> queryAccount(Long partyId);

    /**
     * 查询当前也账户
     *
     * @param type      账户类型
     * @param startPage 起始页号
     * @param pageSize  当前页数量
     * @return
     */
    Response<List<GoldcoinAccountVo>> queryAccountList(String type, int startPage, int pageSize);

    /**
     * 查询用户金币兑换收入
     *
     * @param partyId 用户
     * @return
     */
    Response<BigDecimal> queryIncome(Long partyId);

    /**
     * 按交易类型查询总交易额
     * type为空时查询总的交易金币数
     *
     * @param partyId
     * @param type
     * @return
     */
    Response<Integer> queryTotalAmount(Long partyId, String type);

    /**
     * 金币交易
     *
     * @param req
     * @return 返回处理结果
     */
    Response<GoldcoinRecordVo> doRecord(GoldcoinRecordVo req);

    /**
     * 金币交易
     * 带有余额检查
     *
     * @param req
     * @return 返回处理结果
     */
    Response<GoldcoinRecordVo> doRecordWithBalanceCheck(GoldcoinRecordVo req, int limitValue);


    /**
     * 金币交易记录查询
     *
     * @param req
     * @return
     */
    Response<List<GoldcoinRecordVo>> queryRecord(GoldcoinRecordVo req);

}
