package com.xianglin.act.biz.shared;

import com.xianglin.act.common.service.facade.model.ActivityDTO;

import java.util.List;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/13 1:03.
 */
public interface PopTipLogService {

    void batchLog(List<ActivityDTO> tipList);
    void log(  ActivityDTO activityDTO);
}
