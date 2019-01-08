package com.xianglin.act.common.dal.enums;

import com.xianglin.act.common.dal.mappers.CustomerPrizeMapper;
import com.xianglin.act.common.dal.mappers.PrizeMapper;

/**
 * @author yefei
 * @date 2018-01-22 10:48
 *
 * 之前顺序写死，枚举不应含有等级 ，单位，数据库已配置
 *
 * 现在查询数据库 完全可以实现
 *
 * @see CustomerPrizeMapper#selectCustomerPrize(java.lang.String, java.lang.String)
 * @see PrizeMapper#selectActivityPrize(java.lang.String, java.lang.String)
 */
@Deprecated
public enum PrizeEnum {

    FIRST_PRIZE("一等奖", 1),
    SECOND_PRIZE("二等奖", 2),
    THIRD_PRIZE("三等奖", 3),
    FOURTH_PRIZE("四等奖", 4),
    FIFTH_PRIZE("五等奖", 5),
    SIXTH_PRIZE("六等奖", 6),
    GRAND_PRIZE("特等奖", 0),
    WX_RED_PACKET("微信红包", 0),
    XL_GOLD_COIN("金币", 1, 1000),
    EC_COUPON("新人好礼优惠券", 2),
    LUCKY_PRIZE
    ;

    public static PrizeEnum parse(String name) {
        for (PrizeEnum prizeEnum : PrizeEnum.values()) {
            if (prizeEnum.name().equals(name)) {
                return prizeEnum;
            }
        }
        return null;
    }

    @Deprecated
    private String prizeDesc;

    private int prizeLevel;

    @Deprecated
    private float unit;

    PrizeEnum() {

    }

    @Deprecated
    PrizeEnum(String prizeDesc, int prizeLevel) {
        this(prizeDesc, prizeLevel, 1);
    }

    @Deprecated
    PrizeEnum(String prizeDesc, int prizeLevel, float unit) {
        this.prizeDesc = prizeDesc;
        this.prizeLevel = prizeLevel;
        this.unit = unit;
    }

    public int getPrizeLevel() {
        return prizeLevel;
    }

    @Deprecated
    public String getPrizeDesc() {
        return prizeDesc;
    }

    @Deprecated
    public float getUnit() {
        return unit;
    }
}
