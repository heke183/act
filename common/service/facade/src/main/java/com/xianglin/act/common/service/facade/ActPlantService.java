package com.xianglin.act.common.service.facade;

import com.xianglin.act.common.service.facade.model.*;

import java.util.List;


/**
 * Describe :
 * Created by xingyali on 2018/8/6 17:03.
 * Update reason :
 */

public interface ActPlantService {
    /**
     * 添加用户的明细记录
     * @param actPlantTaskDetailDTO
     * @return
     */
    Response<Boolean> insertActPlantTaskDetail(ActPlantTaskDetailDTO actPlantTaskDetailDTO);

    /**
     * 兑换信息查询
     * @param actPlantLvTranPageDTO
     * @return
     */
    Response<List<ActPlantLvTranDTO>> queryPlantExchange(ActPlantLvTranPageDTO actPlantLvTranPageDTO);

    /**
     * 兑换信息查询
     * @param actPlantLvTranPageDTO
     * @return
     */
    Response<Integer> queryPlantExchangeCount(ActPlantLvTranPageDTO actPlantLvTranPageDTO);

    /**
     * 更新兑换信息
     * @param actPlantLvTranDTO
     * @return
     */
    Response<Boolean> updatePlantExchange(ActPlantLvTranDTO actPlantLvTranDTO);

    /**
     * 查询兑换的礼品
     * @return
     */
    Response<List<ActPlantPrizeDTO>>  queryActPlantPrize();

    /**
     * 查询公告消息列表
     * @return
     */
    Response<PageResult<ActPlantNoticeDTO>>  queryActPlantNotice(PageParam pageParam);

    /**
     * 更新公告信息
     * @param actPlantNoticeDTO
     * @return
     */
    Response<Boolean> updateActPlantNotice(ActPlantNoticeDTO actPlantNoticeDTO);

    /**
     * 新增公告信息
     * @param actPlantNoticeDTO
     * @return
     */
    Response<Boolean> inserActPlantNotice(ActPlantNoticeDTO actPlantNoticeDTO);

    /**
     * 分享信息
     * @param 
     * @return
     */
    Response<ActPlantShareDTO>  share();


    /**
     * 更新兑换信息状态 发放奖品
     * @param id
     * @return
     */
    Response<Boolean> updateExchangeStatus(Long id,String status);
}
