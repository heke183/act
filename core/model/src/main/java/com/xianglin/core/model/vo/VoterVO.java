package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.math.BigDecimal;

/**
 * @author yefei
 * @date 2018-06-13 14:40
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("投票者信息")
public class VoterVO {

    private long partyId;

    /**
     * 金币
     */
    private BigDecimal goldCoin;
}
