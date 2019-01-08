package com.xianglin.act.biz.shared.Impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Objects;
import com.xianglin.act.biz.shared.GamePlaneSharedService;
import com.xianglin.act.common.dal.mappers.ActGamePlaneMapper;
import com.xianglin.act.common.dal.mappers.ConfigMapper;
import com.xianglin.act.common.dal.mappers.SequenceMapper;
import com.xianglin.act.common.dal.model.ActGamePlane;
import com.xianglin.act.common.service.integration.GoldcoinServiceClient;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.BizException;
import com.xianglin.act.common.util.GoldSequenceUtil;
import com.xianglin.cif.common.service.facade.model.Response;
import com.xianglin.cif.common.service.facade.vo.GoldcoinRecordVo;
import com.xianglin.core.model.enums.ActPlantEnum;
import com.xianglin.core.model.enums.Constants;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GamePlaneSharedServiceImpl implements GamePlaneSharedService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ActGamePlaneMapper actGamePlaneMapper;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private GoldcoinServiceClient goldcoinServiceClient;

    @Autowired
    protected SequenceMapper sequenceMapper;

    @Override
    public ActGamePlane start(Long partyId) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        List<ActGamePlane> todayList = actGamePlaneMapper.select(ActGamePlane.builder().partyId(partyId).day(today).build());
        return todayList.stream().filter(v -> StringUtils.equals(v.getStatus(), ActPlantEnum.StatusType.I.name()))
                .findFirst().orElseGet(() -> {
                    int dayLimit = Integer.valueOf(configMapper.selectConfig("GAME_PLANE_DAY_LIMIT"));
                    ActGamePlane game = ActGamePlane.builder().partyId(partyId).day(today).build();
                    if (todayList.size() >= dayLimit) {
                        game.setStatus(ActPlantEnum.StatusType.S.name());
                    } else {
                        game.setStatus(ActPlantEnum.StatusType.I.name());
                    }
                    actGamePlaneMapper.insertSelective(game);
                    return game;
                });
    }

    @Override
    public List<ActGamePlane> queryWeekRanking() {
        String startDay = DateTime.now().withDayOfWeek(1).toString("yyyyMMdd");
        return actGamePlaneMapper.selectWorkRanking(startDay);
    }

    @Override
    public ActGamePlane reward(ActGamePlane plane) {
        return Optional.ofNullable(actGamePlaneMapper.selectByPrimaryKey(plane.getId()))
                .filter(v -> v.getPartyId().equals(plane.getPartyId())).map(v -> {
                    if (StringUtils.equals(v.getStatus(), ActPlantEnum.StatusType.S.name())) {
                        plane.setStageReward(null);
                        plane.setRandomReward(null);
                        plane.setCoinReward(null);
                        plane.setUpdateTime(new Date());
                        actGamePlaneMapper.updateByPrimaryKeySelective(plane);
                        return plane;
                    } else {
                        int stageReward = 0;
                        int randomReward = 0;
                        int limitCoinReward = 25;
                        switch (plane.getStage()) {
                            case 4:
                                stageReward += 66;
                                randomReward = ThreadLocalRandom.current().nextInt(1, 89);
                                limitCoinReward = 100;
                            case 3:
                                stageReward += 30;
                                limitCoinReward = 75;
                            case 2:
                                stageReward += 10;
                                limitCoinReward = 50;
                            case 1:
                                limitCoinReward = 25;
                                stageReward += 5;
                        }
                        if (stageReward + plane.getCoinReward() > 0) {
                            if (plane.getCoinReward() > limitCoinReward) {
                                plane.setCoinReward(limitCoinReward);
                            }
                            Response<GoldcoinRecordVo> goldcoinRecordVoResponse =
                                    goldcoinServiceClient.doRecord(GoldcoinRecordVo.builder()
                                            .system("act")
                                            .amount(stageReward + randomReward + plane.getCoinReward())
                                            .fronPartyId(Constants.GOLD_SYS_ACCOUNT)
                                            .toPartyId(v.getPartyId())
                                            .remark("飞机大战")
                                            .type("GAME_PLANE")
                                            .requestId(GoldSequenceUtil.getSequence(v.getPartyId(), sequenceMapper
                                                    .getSequence())).build());
                            if (!Objects.equal(goldcoinRecordVoResponse.getCode(), 1000)) {
                                logger.error("添加金币失败！", JSON.toJSONString(goldcoinRecordVoResponse));
                                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
                            }
                        }
                        plane.setStatus(ActPlantEnum.StatusType.S.name());
                        plane.setStageReward(stageReward);
                        plane.setRandomReward(randomReward);
                        plane.setUpdateTime(new Date());
                        actGamePlaneMapper.updateByPrimaryKeySelective(plane);
                        return plane;
                    }
                }).orElse(plane);
    }

    public static void main(String[] args) {
        System.out.println(DateTime.now().withDayOfWeek(2).toString("yyyyMMdd"));
    }
}
