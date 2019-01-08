package com.xianglin.act.biz.shared;

import com.xianglin.act.common.service.facade.model.Response;

/**
 * Describe :
 * Created by xingyali on 2018/8/14 11:25.
 * Update reason :
 */
public interface SysConfigSharedService {
    /**
     * 根据code查询参数配置
     * @param code
     * @return
     */
    String querySysConfigVaule(String code);
}
