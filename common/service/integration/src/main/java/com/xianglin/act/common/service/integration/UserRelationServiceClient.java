package com.xianglin.act.common.service.integration;

import com.xianglin.appserv.common.service.facade.model.vo.AppUserRelationVo;

import java.util.List;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/4 16:19.
 */

public interface UserRelationServiceClient {

    List<AppUserRelationVo> getUserFellowList(Long partyId);
}
