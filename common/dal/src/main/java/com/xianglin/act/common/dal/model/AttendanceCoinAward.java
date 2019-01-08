package com.xianglin.act.common.dal.model;

import com.xianglin.act.common.dal.enums.AttendanceAwardTypeEnum;
import com.xianglin.act.common.dal.enums.TargetAttendanceCustomerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "act_attendance_coin_award")
public class AttendanceCoinAward implements Serializable {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 奖励名称
     */
    private String awardName;

    /**
     * 奖励code 单表唯一
     */
    private String awardCode;

    /**
     * 用户类型 连续登录、非连续登录：CONTINUOUS/DISCONTINUOUS
     */
    private TargetAttendanceCustomerTypeEnum targetCustomerType;

    /**
     * 奖励类型 翻倍、不翻倍:MULTIPLY/UNMULTIPLY
     */
    private AttendanceAwardTypeEnum awardType;

    /**
     * 翻倍奖励类型的倍数
     */
    private Integer awardTimes;

    /**
     * 打卡时间排序的百分比区间下限
     */
    private BigDecimal signInOrderFloorPercent;

    /**
     * 打卡时间排序的百分比区间上限
     */
    private BigDecimal signInOrderCeilPercent;

    /**
     * 不翻倍奖励的奖励最小值
     */
    private BigDecimal minValue;

    /**
     * 不翻倍奖励的奖励最大值
     */
    private BigDecimal maxValue;

    /**
     * 统一删除标记
     */
    private boolean isDeleted;

    private String creator;

    private String updater;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String comments;

    private static final long serialVersionUID = 1L;


}