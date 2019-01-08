package com.xianglin.act.common.service.integration.impl;

import com.xianglin.act.common.service.integration.ActivityInviteServiceClient;
import com.xianglin.appserv.common.service.facade.app.ActivityInviteService;
import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.vo.ActivityInviteDetailVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yefei
 * @date 2018-01-25 19:21
 */
@Service
public class ActivityInviteServiceClientImpl implements ActivityInviteServiceClient {

    @Resource
    private ActivityInviteService activityInviteService;

    @Override
    public Response<Boolean> invite(ActivityInviteDetailVo activityInviteDetailVo) {
        return activityInviteService.invite(activityInviteDetailVo);
    }
}
