package com.xianglin.act.common.service.facade.model;

import lombok.*;

import java.io.Serializable;
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
public class GamePlaneDTO implements Serializable{

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

    private int rank;

    private String headImg;

    private String showName;
}
