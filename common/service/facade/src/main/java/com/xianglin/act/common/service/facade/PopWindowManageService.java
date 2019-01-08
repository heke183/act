package com.xianglin.act.common.service.facade;

import com.xianglin.act.common.service.facade.model.*;

/**
 * TOP 活动管理
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/10 10:54.
 */

public interface PopWindowManageService {

    /**
     * 列表
     *
     * @return
     */
    Response<PageResult<PopWindowManageOutputDTO>> queryActivities(PageParam<PopWindowManageInputDTO> input);

    /**
     * 详情/回显
     *
     * @return
     */
    Response<PopWindowManageOutputDTO> queryActivity(Long id);

    /**
     * 新增/修改/删除
     *
     * @return
     */
    Response<Boolean> updateActivity(PopWindowManageInputDTO input);
}
