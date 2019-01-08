package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.CustomerPrize;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Customer prize mapper.
 *
 * @author yefei
 * @date 2018 -01-22 9:25
 */
public interface CustomerPrizeMapper {

    /**
     * 更新可用奖品数量
     *
     * @param customerPrize the customer prize
     * @return the customer prize
     */
    int updateCustomerPrizeAvailableAmount(CustomerPrize customerPrize);

    /**
     * Select customer prize list.
     */
    List<CustomerPrize> selectCustomerPrize(
            @Param("activityCode") String activityCode,
            @Param("userType") String userType);

    /**
     * 查询唯一的奖品
     *
     * @param activityCode
     * @param userType
     * @param prizeCode
     * @return
     */
    CustomerPrize selectCustomerPrizeUnique(
            @Param("activityCode") String activityCode,
            @Param("userType") String userType,
            @Param("prizeCode") String prizeCode
    );

    /**
     * 更新 微信 红包个数
     *
     * @return
     */
    int updateWxRedPacket();

    /**
     * 根据code 更新奖品的区间数
     * @param customerType
     * @param maxValue
     * @param minValue
     * @param prizeCode
     * @return
     */
    boolean updateCustomerPrizeMaxAndMinValue(@Param("customerType")String customerType,@Param("maxValue")Integer maxValue,@Param("minValue")Integer minValue,@Param("prizeCode")String prizeCode);
}
