package com.xianglin.act.common.dal.enums;

/**
 * @author yefei
 * @date 2018-06-04 17:11
 */
public enum  PrizeType {

    XL_GOLD_COIN,
    EC_COUPON,
    EC_PHONE_COUPON,
    WX_RED_PACKET,
    ACT_PLANT_LV,
    ENTITY
    ;

    public static PrizeType parse(String name) {
        for (PrizeType prizeType : PrizeType.values()) {
            if (prizeType.name().equals(name)) {
                return prizeType;
            }
        }
        return null;
    }
}
