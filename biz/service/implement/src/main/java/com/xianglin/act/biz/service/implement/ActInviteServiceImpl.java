package com.xianglin.act.biz.service.implement;

import com.alibaba.dubbo.config.annotation.Service;
import com.xianglin.act.biz.shared.ActInviteSharedService;
import com.xianglin.act.common.dal.model.ActInvite;
import com.xianglin.act.common.service.facade.ActInviteService;
import com.xianglin.act.common.service.facade.model.ActInviteDTO;
import com.xianglin.act.common.service.facade.model.PageParam;
import com.xianglin.act.common.service.facade.model.PageResult;
import com.xianglin.act.common.service.facade.model.Response;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.BizException;
import com.xianglin.act.common.util.DTOUtils;

import com.xianglin.gateway.common.service.spi.annotation.ServiceInterface;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;



/**
 * Describe :
 * Created by xingyali on 2018/8/23 14:01.
 * Update reason :
 */
@Service
@org.springframework.stereotype.Service
@ServiceInterface(ActInviteService.class)
public class ActInviteServiceImpl implements ActInviteService{

    private static final Logger logger = LoggerFactory.getLogger(ActInviteService.class);
    
    @Autowired
    private ActInviteSharedService actInviteSharedService;
    
    
    /**
     *top端查询地推用户 
     * @param pageParam
     * @return
     */
    @Override
    public Response<PageResult<ActInviteDTO>> queryInviteList(PageParam<ActInviteDTO> pageParam) {
        PageResult<ActInviteDTO> actInvites = actInviteSharedService.queryInviteListByParam(pageParam);
        return  Response.ofSuccess(actInvites);
    }

    @Override
    public Response<Boolean> updateInvite(ActInviteDTO actInviteDTO) {
        Boolean flag = null;
        try {
            flag = actInviteSharedService.updateInvite(DTOUtils.map(actInviteDTO,ActInvite.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  Response.ofSuccess(flag);
    }

    @Override
    public Response<Boolean> syncInviteList() {
        Boolean flag = actInviteSharedService.syncInviteList();
        return Response.ofSuccess(flag);
    }
}
