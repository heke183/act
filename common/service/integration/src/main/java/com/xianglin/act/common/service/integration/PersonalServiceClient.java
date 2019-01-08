package com.xianglin.act.common.service.integration;

import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;

import java.util.Map;

/**
 * @author yefei
 * @date 2018-01-25 18:03
 */
public interface PersonalServiceClient {

    /**
     * 查询app用户信息
     * @param partyId
     * @return
     */
    Response<UserVo> queryUser(Long partyId);

    /**
     * 查用户的关注数是否超过10人、是否发过微博、是否签到、是否晒收入
     * @return
     */
    Response<Map<String,Object>> queryUserSignAndSubjectAndFollow(Long partyId);
}
