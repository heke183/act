package com.xianglin.act.common.service.facade;

import com.xianglin.act.common.service.facade.model.ActivityDTO;
import com.xianglin.act.common.service.facade.model.Response;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yefei
 * @date 2018-04-02 12:54
 */
public interface ActService {

    /**
     * 查询正在进行的活动，取一个
     *
     * @return
     */
    Response<List<ActivityDTO>> selectAct();

    /**
     * 新用户开, 更新成已经弹窗
     *
     * @return
     */
    Response<?> open(Integer popTipType, Long id);

    /** 查询参数配置信息
     * @param activityCode 活动code
     * @return
     */
    Response<Map<String,String>> queryActConfig(String activityCode);

    /**更新活动参数配置信息
     * @param activityCode
     * @param config
     * @return
     */
    Response<Boolean> updateActConfig(String activityCode,Map<String,String> config);

    /**
     * 新增参数配置信息
     * @param activityCode 活动code
     * @param config 参数 配置
     * @return
     */
    Response<Boolean> insertActivityConfig(String activityCode,Map<String,String> config);

}
