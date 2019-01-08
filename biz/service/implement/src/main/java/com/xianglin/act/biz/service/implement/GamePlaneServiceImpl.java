package com.xianglin.act.biz.service.implement;

import com.alibaba.dubbo.config.annotation.Service;
import com.xianglin.act.biz.shared.GamePlaneSharedService;
import com.xianglin.act.common.dal.mappers.ConfigMapper;
import com.xianglin.act.common.dal.model.ActGamePlane;
import com.xianglin.act.common.service.facade.GamePlaneService;
import com.xianglin.act.common.service.facade.model.GamePlaneDTO;
import com.xianglin.act.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.app.PersonalService;
import com.xianglin.core.model.vo.ActShareVo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@org.springframework.stereotype.Service
public class GamePlaneServiceImpl implements GamePlaneService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GamePlaneSharedService gamePlaneSharedService;

    @Autowired
    private PersonalService personalService;

    @Autowired
    private ConfigMapper configMapper;

    @Override
    public Response<GamePlaneDTO> start(Long partyId) {
        ActGamePlane plane = gamePlaneSharedService.start(partyId);
        return Response.ofSuccess(GamePlaneDTO.builder().id(plane.getId()).status(plane.getStatus()).build());
    }

    @Override
    public Response<List<GamePlaneDTO>> weekRanking(Long partyId) {
        AtomicInteger rank = new AtomicInteger(1);
        AtomicReference<GamePlaneDTO> self = new AtomicReference<>();
        List<GamePlaneDTO> rankList = gamePlaneSharedService.queryWeekRanking().stream().map(v -> {
            GamePlaneDTO result = new GamePlaneDTO();
            result.setRank(rank.getAndAdd(1));
            result.setScore(v.getScore());
            result.setPartyId(v.getPartyId());
            if (partyId.equals(v.getPartyId())) {
                self.set(GamePlaneDTO.builder().rank(result.getRank()).partyId(result.getPartyId()).score(result.getScore()).build());
            }
            return result;
        }).collect(Collectors.toList());

        rankList.add(0,Optional.ofNullable(self.get()).orElse(GamePlaneDTO.builder().partyId(partyId).score(0).rank(rank.getAndAdd(1)).build()));
        return Response.ofSuccess(rankList.stream().limit(11).parallel().peek(v -> {
            Optional.ofNullable(personalService.queryUser(v.getPartyId()).getResult()).ifPresent(user -> {
                v.setHeadImg(user.getHeadImg());
                v.setShowName(user.getShowName());
            });
        }).collect(Collectors.toList()));
    }

    @Override
    public Response<GamePlaneDTO> reward(GamePlaneDTO req) {
        ActGamePlane reward = gamePlaneSharedService.reward(ActGamePlane.builder().id(req.getId()).partyId(req.getPartyId()).score(req.getScore()).shotCount(req.getShotCount()).stage(req.getStage()).coinReward(req.getCoinReward()).build());
        return Response.ofSuccess(GamePlaneDTO.builder().score(reward.getScore()).shotCount(reward.getShotCount())
                .stageReward(Optional.ofNullable(reward.getStageReward()).orElse(0) + Optional.ofNullable(reward.getCoinReward()).orElse(0))
                .randomReward(reward.getRandomReward()).build());
    }

    @Override
    public Response<Object> share(Long partyId) {
        return Response.ofSuccess(ActShareVo.builder().title("飞机大战，邀你一起赚金币！").image(configMapper.selectConfig("GAME_PLANE_SHARE_IMG"))
                .content("乡邻太空站开始了，和我一起来赚金币。").shareUrl(configMapper.selectConfig("GAME_PLANE_SHARE_URL")).build());
    }
}
