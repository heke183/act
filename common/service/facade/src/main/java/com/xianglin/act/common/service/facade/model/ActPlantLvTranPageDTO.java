package com.xianglin.act.common.service.facade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Describe :
 * Created by xingyali on 2018/8/13 10:19.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActPlantLvTranPageDTO implements Serializable {

    private static final long serialVersionUID = 2261670098381435399L;

    /**
     * 用户的partyID
     */
    private Long partyId;

    /**
     * 礼品code
     */
    private String code;

    /**
     * 收货人
     */
    private String likeUserName;

    /**
     * 手机号
     */
    private String moblie;

    private int startPage;

    private int pageSize;

    private String status;
}
