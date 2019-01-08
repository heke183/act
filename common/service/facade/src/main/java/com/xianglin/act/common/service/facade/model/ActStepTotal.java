package com.xianglin.act.common.service.facade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActStepTotal implements Serializable{

    /**
     * 累计参与天数
     */
    private int days;

    /**
     * 累计兑换次数
     */
    private int conversions;

    /**
     * 累计金币数量
     */
    private int goldCoins;


}
