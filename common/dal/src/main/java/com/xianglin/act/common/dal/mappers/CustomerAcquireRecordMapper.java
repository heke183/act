package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.CustomerAcquire;
import com.xianglin.act.common.dal.model.Prize;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

/**
 * The interface Customer acquire record mapper.
 *
 * @author yefei
 * @date 2018 -01-22 9:27
 */
public interface CustomerAcquireRecordMapper extends Mapper<CustomerAcquire> {

    /**
     * @param customerAcquire
     * @return
     */
    int insertCustomerAcquireRecord(CustomerAcquire customerAcquire);

    /**
     * 查询新用户抽奖记录
     *
     * @param partyId the party id
     * @return long
     */
    long selectNewCustomerRecordCount(
            @Param("partyId") Long partyId,
            @Param("activityCode") String activityCode);

    /**
     * 查询老用户每日抽奖记录
     *
     * @param partyId the party id
     * @return long
     */
    long selectRegularCustomerRecordCount(
            @Param("partyId") Long partyId,
            @Param("activityCode") String activityCode);

    /**
     * 查询用户获奖记录
     *
     * @param activityCode
     * @return
     */
    Set<CustomerAcquire> selectCustomerAcquireRecord(String activityCode);

    /**
     * 查询已经参与的总人数
     *
     * @param activityCode
     * @return
     */
    long selectCustomerCount(String activityCode);

    /**
     * 查询用户是否获得改奖品
     *
     * @param activityCode
     * @param partyId
     * @param prizeCode
     * @param userType
     * @return
     */
    List<CustomerAcquire> selectCustomerAcquired(
            @Param("activityCode") String activityCode,
            @Param("partyId") Long partyId,
            @Param("prizeCode") String prizeCode,
            @Param("userType") String userType);

    /**
     * 查询级别大于
     *
     * @param prizeCode 的奖品
     * @param partyId
     * @return
     */
    List<CustomerAcquire> selectCustomerPrizeGreaterThan(
            @Param("activityCode") String activityCode,
            @Param("partyId") Long partyId,
            @Param("prizeCode") String prizeCode,
            @Param("userType") String userType);

    /**
     * @param memcCode
     * @return
     */
    CustomerAcquire selectByMemcCode(String memcCode);

    /**
     * 查询获得的价值总额
     *
     * @param partyId
     * @return
     */
    CustomerAcquire selectAcquireAmount(long partyId);

    /**
     * 30 分钟发送的红包个数是否超过限制
     *
     * @return
     */
    boolean isAlarm();

    /**
     * 查询投票奖励的记录
     *
     * @return
     */
    CustomerAcquire selectVoteRecord(@Param("partyId") long partyId, @Param("status") String status,@Param("activityCode")String activityCode);

    /**
     * @param customerAcquire
     * @return
     */
    int updateAcquireRecord(CustomerAcquire customerAcquire);

    /**
     * 更新 奖品记录订单id
     *
     * @param customerAcquire
     * @return
     */
    int updateCustomerPrizeMemo(CustomerAcquire customerAcquire);

}
