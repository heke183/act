package com.xianglin.act.common.service.integration;

import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.vo.ActivityInviteDetailVo;

/**
 * The interface Activity invite service client.
 *
 * @author yefei
 * @date 2018 -01-25 19:20
 */
public interface ActivityInviteServiceClient {

    /**
     * Invite response.
     *
     * @param activityInviteDetailVo the activity invite detail vo
     * @return the response
     */
    Response<Boolean> invite(ActivityInviteDetailVo activityInviteDetailVo);
}
