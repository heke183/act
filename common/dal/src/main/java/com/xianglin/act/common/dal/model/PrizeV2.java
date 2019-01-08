package com.xianglin.act.common.dal.model;

import com.xianglin.act.common.dal.enums.PrizeEnum;
import com.xianglin.act.common.dal.enums.PrizeType;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author jiang yong tao
 * @date 2018/12/20  11:02
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "act_prize")
public class PrizeV2 {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    private String prizeName;

    private String couponName;

    private String prizeCode;

    private String prizeType;

    private String prizeDesc;

    private int prizeLevel;

    private String activityCode;

    private BigDecimal unitRmb;

    private String isDeleted;

    private PrizeType prizeTypeEnum;

    private String prizeImage;

}
