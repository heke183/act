package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/23 11:00.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("打卡之星")
public class AttendanceStarVO {

    /**
     * 参与者总数
     */
    @ApiModelProperty("用户头像")
    private String headImg;

    /**
     * 参与者总数
     */
    @ApiModelProperty("用户名")
    private String name;

    /**
     * 参与者总数
     */
    @ApiModelProperty("获奖提示消息")
    private String message;
}
