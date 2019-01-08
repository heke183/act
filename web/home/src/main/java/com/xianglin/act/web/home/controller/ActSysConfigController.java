package com.xianglin.act.web.home.controller;

import com.xianglin.act.biz.shared.ActPlantSharedService;
import com.xianglin.act.common.dal.mappers.ConfigMapper;
import com.xianglin.act.common.dal.model.ActSystemConfig;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.BizException;
import com.xianglin.act.web.home.intercepter.SessionInterceptor;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.core.model.vo.ActSystemConfigVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiang yong tao
 * @date 2018/8/9  15:47
 */
@RestController
@RequestMapping("/act/api/sysConfig")
@Api(value = "/act/api/sysConfig", tags = "活动配置")
@ApiResponses({@ApiResponse(code = 2000, message = "调用成功"),
        @ApiResponse(code = 3000, message = "业务异常"),
        @ApiResponse(code = 4000, message = "未知错误，系统异常")})
public class ActSysConfigController {

    @Autowired
    private ActPlantSharedService actPlantSharedService;

    @SessionInterceptor.IntercepterIngore
    @PostMapping("/selectActSysConfigValue")
    @ApiOperation(value = "根据配置活动的Code,查询配置的值")
    public Response<String> selectActSysConfigValue(String actSysConfigCode)
    {
        if (StringUtils.isBlank(actSysConfigCode)){
            throw new BizException(ActPreconditions.ResponseEnum.ACTCONFIGCODE_NOTNULL);
        }
        String actSystemConfigValue = actPlantSharedService.selectActConfigValue(actSysConfigCode);
        return Response.ofSuccess(actSystemConfigValue);
    }

}
