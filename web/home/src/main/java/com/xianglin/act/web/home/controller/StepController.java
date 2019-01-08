package com.xianglin.act.web.home.controller;

import com.xianglin.act.biz.shared.StepSharedService;
import com.xianglin.act.common.dal.model.ActStepDetail;
import com.xianglin.act.common.dal.model.redpacket.Sharer;
import com.xianglin.act.common.dal.model.redpacket.SharerInfo;
import com.xianglin.act.common.service.facade.StepService;
import com.xianglin.act.common.service.facade.model.ActStepDetailDTO;
import com.xianglin.act.common.service.facade.model.ActStepDetailShareInfo;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.act.web.home.intercepter.SessionInterceptor;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.act.web.home.util.SessionHelper;
import com.xianglin.appserv.common.service.facade.app.PersonalService;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import com.xianglin.fala.session.Session;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Describe :
 * Created by xingyali on 2018/7/20 17:24.
 * Update reason :
 */
@RestController
@RequestMapping("/act/api/activity/step")
@Api(value = "/act/api/activity/step", tags = "步步生金接口")
@ApiResponses({@ApiResponse(code = 2000, message = "调用成功"),
        @ApiResponse(code = 3000, message = "业务异常"),
        @ApiResponse(code = 4000, message = "未知错误，系统异常")})
public class StepController {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(StepController.class);

    @Resource
    private StepSharedService stepSharedService;

    @SessionInterceptor.IntercepterIngore
    @RequestMapping("/selectSharer")
    @ApiOperation(value = "进入分享页面后调用, 查询分享者信息")
    public Response<List<ActStepDetailDTO>> queryActStepDetailShare(){
        Response<List<ActStepDetailDTO>> response = new Response<>();
        List<ActStepDetailDTO> actStepDetails = stepSharedService.queryActStepDetailShare();
        response.setResult(actStepDetails);
        return response;
    }

    @SessionInterceptor.IntercepterIngore
    @RequestMapping("/queryContentShare")
    @ApiOperation(value = "查询分享文案内容")
    public Response<ActStepDetailShareInfo> queryContentShare(){
        //Session session = GlobalRequestContext.getSession();
        //Long partyId = session.getAttribute("partyId",Long.class);
        Response<ActStepDetailShareInfo> response = new Response<>();
        ActStepDetailShareInfo actStepDetails = stepSharedService.queryContentShare(null);
        response.setResult(actStepDetails);
        return response;
    }
    
    

    

}
