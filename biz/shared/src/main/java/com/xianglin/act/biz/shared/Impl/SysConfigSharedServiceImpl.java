package com.xianglin.act.biz.shared.Impl;

import com.xianglin.act.biz.shared.StepSharedService;
import com.xianglin.act.biz.shared.SysConfigSharedService;
import com.xianglin.act.common.dal.mappers.ConfigMapper;
import com.xianglin.act.common.service.facade.SysConfigService;
import com.xianglin.act.common.service.facade.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Describe :
 * Created by xingyali on 2018/8/14 11:27.
 * Update reason :
 */
@Service
public class SysConfigSharedServiceImpl implements SysConfigSharedService {
    
    @Autowired
    private ConfigMapper configMapper;
    
    @Override
    public String querySysConfigVaule(String code) {
        return configMapper.selectConfig(code);
    }
}
