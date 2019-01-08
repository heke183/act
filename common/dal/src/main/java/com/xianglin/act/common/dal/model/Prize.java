package com.xianglin.act.common.dal.model;

import com.xianglin.act.common.dal.enums.PrizeEnum;
import com.xianglin.act.common.dal.enums.PrizeType;
import lombok.*;

import java.math.BigDecimal;

/**
 * The type Prize.
 *
 * @author yefei
 * @date 2018 -01-18 14:02
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prize {

    private String prizeName;

    private String couponName;

    @Deprecated
    private PrizeEnum prizeEnum;

    private String prizeCode;

    private String prizeType;

    private String prizeDesc;

    private int prizeLevel;

    private String memcCode;

    private BigDecimal amount;

    private String activityCode;

    private BigDecimal unitRmb;

    private String isDeleted;

    private PrizeType prizeTypeEnum;

    public void setPrizeType(String prizeType) {
        this.prizeType = prizeType;
        this.prizeTypeEnum = PrizeType.parse(prizeType);
    }
}
