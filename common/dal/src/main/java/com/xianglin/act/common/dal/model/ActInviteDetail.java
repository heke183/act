package com.xianglin.act.common.dal.model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "act_invite_detail")
public class ActInviteDetail {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    private Long partyId;

    private Long recPartyId;

    private String status;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;

}
