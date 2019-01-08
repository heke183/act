package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author yefei
 * @date 2018-06-19 15:18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("投票记录")
public class VoteRecord {

    private long partId;

    private long toPartyId;

    @ApiModelProperty("给谁投票")
    private String name;

    @ApiModelProperty("已投金币")
    private BigDecimal amount;

    @ApiModelProperty("投票时间")
    private LocalDateTime dateTime;

    @ApiModelProperty("获得的奖励")
    private BigDecimal award;

}
