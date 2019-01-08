package com.xianglin.core.model.vo;

import com.xianglin.act.common.dal.model.PageReq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Describe :
 * Created by xingyali on 2018/8/13 9:30.
 * Update reason :
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActPlantLvTranPageVo{

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
    
    
    
}
