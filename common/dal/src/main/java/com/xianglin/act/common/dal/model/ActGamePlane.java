package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


/**
 * 飞机大战主表
 * @author wanglei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_game_plane")
public class ActGamePlane {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    private Long partyId;

    private String day;

    private Integer score;

    private Integer shotCount;

    private Integer stage;

    private Integer coinReward;

    private Integer stageReward;

    private Integer randomReward;

    private String status;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;
}
