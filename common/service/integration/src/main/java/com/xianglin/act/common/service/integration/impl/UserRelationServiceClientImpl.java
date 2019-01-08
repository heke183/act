package com.xianglin.act.common.service.integration.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.xianglin.act.common.service.integration.AppgwService;
import com.xianglin.act.common.service.integration.UserRelationServiceClient;
import com.xianglin.appserv.common.service.facade.UserRelationService;
import com.xianglin.appserv.common.service.facade.model.enums.Constant;
import com.xianglin.appserv.common.service.facade.model.vo.AppUserRelationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/4 16:19.
 */
@Service
public class UserRelationServiceClientImpl implements UserRelationServiceClient {

    @Autowired
    private AppgwService appgwService;

    public List<AppUserRelationVo> getUserFellowList(Long partyId) {

        AppUserRelationVo buildReq = AppUserRelationVo.builder()
                .fromPartyId(partyId)
                .bothStatus(Constant.RelationStatus.FOLLOW.name())
                .build();
        buildReq.setStartPage(1);
        buildReq.setPageSize(1000);
        JSONArray listResponse = appgwService.service(
                UserRelationService.class,
                "queryFollowsOrFans",
                JSONArray.class,
                buildReq);
        if (listResponse == null) {
            listResponse = new JSONArray();
        }
        TypeReference<List<AppUserRelationVo>> typeReference = new TypeReference<List<AppUserRelationVo>>() {

        };
        byte[] tempBytes = JSON.toJSONBytes(listResponse);
        Object o = JSON.parseObject(tempBytes, typeReference.getType());
        return (List<AppUserRelationVo>) o;
    }
}
