package com.xianglin.act.common.service.facade;

import com.xianglin.act.common.service.facade.model.Response;

/**
 * Describe :
 * Created by xingyali on 2018/8/14 11:18.
 * Update reason :
 */
public interface SysConfigService {

    /**
     * 根据code查询参数配置
     * @param code
     * @return
     */
    Response<String>  querySysConfigVaule(String code);
}
