package com.xianglin.act.web.home.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xianglin.act.biz.shared.GamePlaneSharedService;
import com.xianglin.act.common.dal.model.ActGamePlane;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.act.web.home.model.Response;
import com.xianglin.appserv.common.service.facade.app.PersonalService;
import com.xianglin.core.model.vo.ActInviteVo;
import com.xianglin.core.model.vo.ActShareVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author wanglei
 * @date
 */
@RestController
@RequestMapping("/act/api/game/plane")
@Api(value = "/act/api/game/plane", tags = "飞机大战")
@ApiResponses({@ApiResponse(code = 2000, message = "调用成功"),
        @ApiResponse(code = 3000, message = "业务异常"),
        @ApiResponse(code = 4000, message = "未知错误，系统异常")})
public class ActGamePlaneController {


    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GamePlaneSharedService gamePlaneSharedService;

    @Autowired
    private PersonalService personalService;

    @PostMapping("/start")
    @ApiOperation("开始游戏")
    public Response<JSONObject> start() throws Exception {
        return doResponse(() -> {
            ActGamePlane plane = gamePlaneSharedService.start(getCurrentPartyId());
            JSONObject result = new JSONObject();
            result.fluentPut("id",plane.getId())
                    .fluentPut("status",plane.getStatus());
            return result;
        });
    }

    @PostMapping("/ranking")
    @ApiOperation("排行榜")
    public Response<List<JSONObject>> actInviteShare() throws Exception {
        return doResponse(() -> {
            Long partyId = getCurrentPartyId();
            AtomicInteger rank = new AtomicInteger(0);
            AtomicReference<JSONObject> self = new AtomicReference<>();
            List<JSONObject> rankList = gamePlaneSharedService.queryWeekRanking().stream().map(v ->{
                JSONObject result = new JSONObject();
                result.fluentPut("rank",rank.getAndAdd(1))
                        .fluentPut("score",v.getScore());
                Optional.ofNullable(personalService.queryUser(v.getPartyId()).getResult()).ifPresent(user ->{
                    result.fluentPut("headImg",user.getHeadImg())
                            .fluentPut("showName",user.getShowName());
                });
                if(partyId.equals(v.getPartyId())){
                    self.set((JSONObject) result.clone());
                }
                return result;
            }).limit(10).collect(Collectors.toList());
            rankList.add(0,self.get());
            return rankList;
        });
    }

    @PostMapping("/reward")
    @ApiOperation("发放游戏奖励")
    public Response<JSONObject> reward(Long id,int score,int shotCount,int stage,int coinReward) throws Exception {
        return doResponse(() -> {
            ActGamePlane reward = gamePlaneSharedService.reward(ActGamePlane.builder().id(id).score(score).shotCount(shotCount).stage(stage).coinReward(coinReward).build());
            return new JSONObject().fluentPut("score",reward.getScore())
                    .fluentPut("shotCount",reward.getShotCount())
                    .fluentPut("stageReward",reward.getStageReward())
                    .fluentPut("randomReward",reward.getRandomReward());
        });
    }

    @PostMapping("/share")
    @ApiOperation("分享")
    public Response<ActShareVo> share() throws Exception {
        return doResponse(() -> {
            return ActShareVo.builder().title("飞机大战，邀你一起赚金币！").image("")
                    .content("乡邻太空站开始了，和我一起来赚金币。").shareUrl("").build();
        });
    }

    private Long getCurrentPartyId() {
        return GlobalRequestContext.currentPartyId();
    }

    /**
     * 工具方法，用于做统一处理
     *
     * @param func
     * @param <T>
     * @return
     */
    private <T> Response<T> doResponse(Supplier<T> func) {
        T result = func.get();
        logger.info("======== result:{}", JSON.toJSON(result));
        return Response.ofSuccess(result);
    }


}
