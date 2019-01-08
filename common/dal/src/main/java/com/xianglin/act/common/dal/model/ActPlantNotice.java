package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author jiang yong tao
 * @date 2018/10/29  14:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_plant_notice")
public class ActPlantNotice {

    @Id
    @GeneratedValue(generator = "JDBC")
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
