package com.xianglin.act.biz.service.implement;

import com.xianglin.act.biz.shared.SysConfigSharedService;
import com.xianglin.act.common.service.facade.SysConfigService;
import com.xianglin.act.common.service.facade.model.Response;
import com.xianglin.gateway.common.service.spi.annotation.ServiceInterface;
import com.xianglin.gateway.common.service.spi.annotation.ServiceMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Describe :
 * Created by xingyali on 2018/8/14 11:21.
 * Update reason :
 */
@com.alibaba.dubbo.config.annotation.Service
@org.springframework.stereotype.Service
@ServiceInterface(SysConfigService.class)
public class SysConfigServiceImpl implements SysConfigService {
    
    @Autowired
    private SysConfigSharedService  sysConfigSharedService;


    /**
     * 根据code查询参数配置
     * @param code
     * @return
     */
    @ServiceMethod(description = "查询参数配置")
    @Override
    public Response<String> querySysConfigVaule(String code) {
        return  Response.ofSuccess(sysConfigSharedService.querySysConfigVaule(code)); 
    }
}
