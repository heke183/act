package com.xianglin.act.common.service.facade;

import com.xianglin.act.common.service.facade.model.*;


/**
 * Describe :
 * Created by xingyali on 2018/8/23 14:02.
 * Update reason :
 */
public interface ActInviteService {

    /**
     *top端查询地推用户
     * @param pageParam
     * @return
     */
    Response<PageResult<ActInviteDTO>> queryInviteList(PageParam<ActInviteDTO> pageParam);

    /**
     * 更新地推用户的数据
     * @param actInviteDTO
     * @return
     */
    Response<Boolean> updateInvite(ActInviteDTO actInviteDTO);

    /**
     * 同步地推用户数据
     * @return
     */
    Response<Boolean> syncInviteList();
    
    
    
    
}
