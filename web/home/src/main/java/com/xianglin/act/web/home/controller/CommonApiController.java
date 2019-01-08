package com.xianglin.act.web.home.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.xianglin.act.common.service.integration.UserRelationServiceClient;
import com.xianglin.act.common.util.AliyunUtil;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.act.common.util.config.db.AttendanceConfiguration;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.appserv.common.service.facade.model.vo.AppUserRelationVo;
import com.xianglin.core.model.vo.AppUserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传相关
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/1 14:36.
 */
@RestController
@RequestMapping("/act/api/activity/common-api")
@Api(value = "/act/api/activity/common-api", tags = "文件上传等通用接口")
@ApiResponses({@ApiResponse(code = 2000, message = "调用成功"),
        @ApiResponse(code = 3000, message = "业务异常"),
        @ApiResponse(code = 4000, message = "未知错误，系统异常")})
public class CommonApiController {

    @Autowired
    private UserRelationServiceClient userRelationServiceClient;

    @Autowired
    private AttendanceConfiguration attendanceConfiguration;

    @PostMapping("upload/image")
    @ApiOperation(value = "图片上传", notes = "图片上传", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Object> partakeInVoteAct(String file, String fileName) {

        String imgUrl = AliyunUtil.uploadStream2Oss(Base64.getDecoder().decode(file), fileName);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("imgUrl", imgUrl);
        return Response.ofSuccess(jsonObject);
    }

    @PostMapping("friend-list")
    @ApiOperation(value = "粉丝列表", notes = "粉丝列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<AppUserVO>> friendList() {

        List<AppUserRelationVo> userFansList = userRelationServiceClient.getUserFellowList(GlobalRequestContext.currentPartyId());
        if (userFansList == null) {
            userFansList = Lists.newArrayList();
        }
        List<AppUserVO> collectList = userFansList.stream()
                .map(appUserRelationVo -> {
                    String headImg = appUserRelationVo.getHeadImg();
                    if (StringUtils.isBlank(headImg)) {
                        headImg = attendanceConfiguration.getDefaultUserHeadimg();
                    }
                    String district = appUserRelationVo.getDistrict();
                    if (StringUtils.isBlank(district)) {
                        district = "";
                    }
                    String introduce = appUserRelationVo.getIntroduce();
                    if (StringUtils.isBlank(introduce)) {
                        introduce = "";
                    }
                    return AppUserVO.builder()
                            .partyId(appUserRelationVo.getPartyId())
                            .headImg(headImg)
                            .introduce(introduce)
                            .isAuth(appUserRelationVo.getIsAuth())
                            .userName(appUserRelationVo.getShowName())
                            .district(district)
                            .build();
                }).collect(Collectors.toList());
        return Response.ofSuccess(collectList);
    }
}
