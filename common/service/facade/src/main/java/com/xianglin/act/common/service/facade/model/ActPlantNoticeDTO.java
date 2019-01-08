package com.xianglin.act.common.service.facade.model;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jiang yong tao
 * @date 2018/10/29  14:10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ActPlantNoticeDTO implements Serializable {

    private static final long serialVersionUID = 2261670098381435399L;

    private Long id;

    /**
     * 公告内容
     */
    private String notice;

    /**
     * 链接地址
     */
    private String link;

    /**
     * 开始时间 显示
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    private String creator;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;
}
