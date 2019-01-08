package com.xianglin.act.common.service.facade.model;

import lombok.*;

import java.util.Date;

/**
 * @author jiang yong tao
 * @date 2018/10/29  10:29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ActConfigDTO {


    private Long id;

    private String activityCode;

    private String key;

    private String value;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;
}
