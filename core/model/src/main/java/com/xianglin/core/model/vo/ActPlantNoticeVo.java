package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author jiang yong tao
 * @date 2018/10/29  15:50
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("公告列表")
public class ActPlantNoticeVo {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("公告内容")
    private String notice;

    @ApiModelProperty("链接地址")
    private String link;

    @ApiModelProperty("显示开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    private String creator;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;
}
