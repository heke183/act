package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/18 11:42.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("打卡记录")
public class AttendanceRecordVO {

    /**
     * 日期
     */
    @ApiModelProperty("日期")
    private String date;

    /**
     * 打卡状态
     */
    @ApiModelProperty("打卡状态")
    private String statusMessage;

    /**
     * 是否展示金币icon
     */
    @ApiModelProperty("是否展示金币icon")
    private boolean showIcon;


}
