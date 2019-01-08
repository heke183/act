package com.xianglin.act.web.home.controller;

import com.xianglin.act.biz.shared.VoteOfWorldCupService;
import com.xianglin.act.common.dal.model.VoteActivity;
import com.xianglin.act.web.home.intercepter.SessionInterceptor;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.core.model.vo.VoteActivityBaseInfoVO;
import com.xianglin.core.model.vo.VoteRecord;
import com.xianglin.core.model.vo.VoterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import static com.xianglin.core.service.VoteActivityContext.checkExpire;
import static com.xianglin.core.service.VoteActivityContext.getCurrentVoteActivity;

/**
 * 世界杯投票活动 扩展投票接口等，其他可以用之前的
 *
 * @author yefei
 * @date 2018-06-13 13:53
 * @see VoteActController
 */
@RestController
@RequestMapping("/act/api/activity/vote_of_world_cup")
@Api(value = "/act/api/activity/vote_of_world_cup", tags = "投票活动世界杯接口")
@ApiResponses({@ApiResponse(code = 2000, message = "调用成功"),
        @ApiResponse(code = 3000, message = "业务异常"),
        @ApiResponse(code = 4000, message = "未知错误，系统异常")})
public class VoteOfWorldCupController {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(VoteOfWorldCupController.class);

    private static final String DATE_FORMATTER = "yyyy年MM月dd日 HH:mm";

    @Resource
    private VoteOfWorldCupService voteOfWorldCupService;

    @ModelAttribute
    public void checkActivityCode(String activityCode) {
        voteOfWorldCupService.getVoteActivity(activityCode);
        logger.info("===========请求投票活动：activityCode -> [[ {} ]]===========", activityCode);
    }

    @PostMapping("index")
    @SessionInterceptor.IntercepterIngore
    @ApiOperation(value = "首页", httpMethod = "POST")
    public Response<VoteActivityBaseInfoVO> index() {
        VoteActivityBaseInfoVO vo = voteOfWorldCupService.index();
        return Response.ofSuccess(vo);
    }

    @PostMapping("vote")
    @ApiOperation(value = "投票，查询金币", httpMethod = "POST")
    public Response<VoterVO> vote(long partyId) {
        checkExpire();
        VoterVO voterVO = voteOfWorldCupService.vote(partyId);
        return Response.ofSuccess(voterVO);
    }

    @PostMapping("vote_submit")
    @ApiOperation(value = "投票，提交", httpMethod = "POST")
    public Response<?> voteSubmit(long partyId, BigDecimal amount) {
        checkExpire();
        voteOfWorldCupService.voteSubmit(partyId, amount);
        return Response.ofSuccess();
    }

    @PostMapping("vote_record")
    @ApiOperation(value = "投票记录", httpMethod = "POST")
    public Response<List<VoteRecord>> voteRecord() {
        List<VoteRecord> voteRecords = voteOfWorldCupService.voteRecord();
        return Response.ofSuccess(voteRecords);
    }


    @SessionInterceptor.IntercepterIngore
    @PostMapping("activity_introduce")
    @ApiOperation(value = "活动说明", httpMethod = "POST")
    public Response<String> activityIntroduce() {
        VoteActivity currentVoteActivity = getCurrentVoteActivity();
        return Response.ofSuccess(currentVoteActivity.getActDesc());
    }

}
