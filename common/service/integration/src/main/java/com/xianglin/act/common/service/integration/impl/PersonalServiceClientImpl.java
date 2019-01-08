package com.xianglin.act.common.service.integration.impl;

import com.xianglin.act.common.service.integration.PersonalServiceClient;
import com.xianglin.appserv.common.service.facade.app.PersonalService;
import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author yefei
 * @date 2018-01-25 18:04
 */
@Service
public class PersonalServiceClientImpl implements PersonalServiceClient {

    @Resource
    private PersonalService personalService;

    @Override
    public Response<UserVo> queryUser(Long partyId) {
        return personalService.queryUser(partyId);
    }

    @Override
    public Response<Map<String, Object>> queryUserSignAndSubjectAndFollow(Long partyId) {
        return personalService.queryUserSignAndSubjectAndFollow(partyId);
    }
}
