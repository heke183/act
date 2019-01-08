package com.xianglin.act.web.home.controller;

import com.xianglin.act.biz.shared.ActService;
import com.xianglin.act.common.dal.model.Activity;
import com.xianglin.act.common.service.facade.constant.ActivityConfig;
import com.xianglin.act.common.util.DateUtils;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.core.model.vo.UserAddressVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局活动相关
 *
 * @author yefei
 * @date 2018-04-10 9:57
 */
@RestController
@RequestMapping("/act/api/act")
@Api(value = "/act/api/act", tags = "全局活动")
@ApiResponses({@ApiResponse(code = 2000, message = "调用成功"),
        @ApiResponse(code = 3000, message = "业务异常"),
        @ApiResponse(code = 4000, message = "未知错误，系统异常")})
@Controller
public class ActController {

    @Resource
    private ActService actService;

    @PostMapping("/list")
    @ApiOperation(value = "精彩活动")
    public Response<List<Activity>> actList() {
        Response<List<Activity>> response = new Response<>();
        List<Activity> activities = actService.actList().stream().peek(v -> {
            if (v.getActivityCode().equals(ActivityConfig.ActivityCode.HD001.name())){
                Date endDate = v.getExpireDate();
                if(new Date().compareTo(endDate)>0 && new Date().compareTo(DateUtils.skipDateTime(endDate,5))<=0){
                    v.setType(3);
                }
            }
        }).collect(Collectors.toList());
        response.setResult(activities);
        return response;
    }


    @ApiOperation(value = "用户实名认证查询")
    @PostMapping("/userCertification")
    public Response<UserAddressVo> userCertification() {
        UserAddressVo userAddressVo = actService.userCertification(GlobalRequestContext.currentPartyId());
        return Response.ofSuccess(userAddressVo);
    }
}
