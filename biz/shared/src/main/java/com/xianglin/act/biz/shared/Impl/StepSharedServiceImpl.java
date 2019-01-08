package com.xianglin.act.biz.shared.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xianglin.act.biz.shared.ActPlantSharedService;
import com.xianglin.act.biz.shared.StepSharedService;
import com.xianglin.act.common.dal.mappers.*;
import com.xianglin.act.common.dal.model.*;
import com.xianglin.act.common.service.facade.constant.ExchangeStatusEnum;
import com.xianglin.act.common.service.facade.constant.StepDetailEnum;
import com.xianglin.act.common.service.facade.model.ActStepDetailDTO;
import com.xianglin.act.common.service.facade.model.ActStepDetailShareInfo;
import com.xianglin.act.common.service.integration.GoldcoinServiceClient;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.BizException;
import com.xianglin.act.common.util.GoldSequenceUtil;
import com.xianglin.appserv.common.service.facade.app.PersonalService;
import com.xianglin.appserv.common.service.facade.model.enums.Constant;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import com.xianglin.cif.common.service.facade.vo.GoldcoinRecordVo;
import com.xianglin.core.model.enums.ActPlantEnum;
import com.xianglin.core.model.enums.ActivityEnum;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StepSharedServiceImpl implements StepSharedService {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(StepSharedServiceImpl.class);

    protected final static long GOLD_SYS_ACCOUNT = 10000L;

    @Autowired
    private ActStepMapper actStepMapper;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private GoldcoinServiceClient goldcoinServiceClient;

    @Autowired
    protected SequenceMapper sequenceMapper;

    @Autowired
    private PersonalService personalService;

    @Autowired
    private ActPlantMapper actPlantMapper;

    @Autowired
    private ActPlantTipMapper actPlantTipMapper;

    @Resource
    private ActivityMapper activityMapper;

    @Autowired
    private ActPlantLvTranMapper actPlantLvTranMapper;

    @Override
    public List<ActStepDetail> synchStepDetail(List<ActStepDetail> details, Long partyId) {
        int stepNumber = 0;
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        if (CollectionUtils.isNotEmpty(details)) {
            for (ActStepDetail detail : details) {
                stepNumber += detail.getStepNumber();
                ActStepDetail step = ActStepDetail.builder().partyId(partyId).day(today).type(detail.getType()).isDeleted("N").build();
                ActStepDetail actStepDetail = actStepMapper.selectOne(step);
                if (actStepDetail == null) {
                    step.setStepNumber(detail.getStepNumber());
                    step.setStatus(ExchangeStatusEnum.I.name());
//                    actStepMapper.insert(step);
                    actStepMapper.insertSelective(step);
                    detail.setStatus(step.getStatus());
                } else {
                    
                    actStepDetail.setStepNumber(detail.getStepNumber());
                    actStepDetail.setUpdateTime(new Date());
                    actStepMapper.updateByPrimaryKey(actStepDetail);
                    detail.setGoldReward(actStepDetail.getGoldReward());
                    detail.setStatus(actStepDetail.getStatus());
                }
            }
            //更新当天总步数及金币数
            //在同步之前先查一下有没有今天的数据，没有就初始化一条
            ActStepDetail step = ActStepDetail.builder().partyId(partyId).day(today).type(StepDetailEnum.ALL.name()).isDeleted("N").build();
            ActStepDetail actStepDetail = actStepMapper.selectOne(step);
            if (actStepDetail == null) {
                step.setStatus(ExchangeStatusEnum.I.name());
                step.setStepNumber(stepNumber);
                actStepMapper.insertSelective(step);
            }
            actStepMapper.updateDayTotail(partyId, today);
        }
        return details;
    }

    @Override
    public int queryPartakeDay(Long partyId, String type) {
        return actStepMapper.selectActStepCount(ActStepDetail.builder().goldReward(0).isDeleted("N").partyId(partyId).type(type).build());
    }


    @Override
    public int queryConversions(Long partyId, String status) {
        return actStepMapper.selectCount(ActStepDetail.builder().partyId(partyId).isDeleted("N").status(status).build());
    }

    @Override
    public int queryGoldCoins(Long partyId, String status) {
        int goldCoins = 0;
        goldCoins = actStepMapper.selectGoldRewardSum(ActStepDetail.builder().isDeleted("N").partyId(partyId).status(status).build());
        return goldCoins;
    }

    @Override
    public ActStepDetail queryActStepDetail(ActStepDetail build) {
        return actStepMapper.selectOne(build);
    }

    @Override
    public ActStepDetail queryLuckyUser(String day, String status) {
        return actStepMapper.selectLuckyAward(day, status);
    }

    @Override
    public Boolean updateActStepDetail(ActStepDetail actStepDetail) {
        return actStepMapper.updateByPrimaryKey(actStepDetail) == 1;
    }

    @Override
    public List<ActStepDetail> queryTopList(String day) {
        Example example = new Example(ActStepDetail.class);
        example.and().andEqualTo("type",StepDetailEnum.ALL.name()).andEqualTo("day",day).andEqualTo("isDeleted","N");
        example.and().andGreaterThan("stepNumber",0);
        example.orderBy("stepNumber").desc().orderBy("createTime").desc();
        return actStepMapper.selectByExampleAndRowBounds(example,new RowBounds(0,5));
        /*
        String orderBy = "STEP_NUMBER DESC,CREATE_TIME DESC";
        String excludeStepNumber=">0";
        return actStepMapper.selectActStepDetailList(ActStepDetail.builder().day(day).type(StepDetailEnum.ALL.name()).build(), orderBy,excludeStepNumber, PageReq.builder().startPage(1).pageSize(5).build());*/
    }

    @Override
    public List<ActStepDetail> queryRewardList(Long partyId, Long lastId) {
        Example example = new Example(ActStepDetail.class);
        example.and().andEqualTo("partyId", partyId)
                .andEqualTo("status", ExchangeStatusEnum.S.name()).andEqualTo("isDeleted","N");
        if (lastId != null && lastId > 0) {
            example.and().andLessThan("id", lastId);
        }
        example.orderBy("id").desc();
        return actStepMapper.selectByExampleAndRowBounds(example, new RowBounds(0, 5));
//        return actStepMapper.selectActStepDetailList(ActStepDetail.builder().status(ExchangeStatusEnum.S.name()).partyId(partyId).build(),null, PageReq.builder().startPage(1).pageSize(5).build());
    }

    @Override
    public List<ActStepDetailDTO> queryActStepDetailShare() {
        int size = 20;
        List<ActStepDetailDTO> actStepDetailList = new LinkedList<>();
        Example example = new Example(ActStepDetail.class);
        example.and().andEqualTo("status",  ExchangeStatusEnum.S.name()).andEqualTo("isDeleted","N");
        example.and().andGreaterThan("stepNumber",0).andGreaterThan("goldReward",0);
        example.orderBy("createTime").desc();
        List<ActStepDetail> actStepDetails = actStepMapper.selectByExampleAndRowBounds(example, new RowBounds(0, 20));
        actStepDetails = actStepDetails.stream().sorted((v1, v2) -> {
            return (int) (v1.getRewardTime().getTime() - v2.getRewardTime().getTime());
        }).collect(Collectors.toList());
        actStepDetailList.addAll(convertActStepDetailDTOList(actStepDetails));
        size = size - actStepDetails.size();
        //查假数据
        String step_convert_recode = configMapper.selectConfig("STEP_CONVERT_RECODE");
        com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSONArray.parseArray(step_convert_recode);
        //net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(step_convert_recode);
        //List<ActStepDetail> list2 = (List) JSONArray.toCollection(jsonArray);
        List<ActStepDetailDTO> list2 = new ArrayList<>();
        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ActStepDetailDTO actStepDetail = ActStepDetailDTO.builder().showName(jsonObject.get("showName").toString()).stepNumber(Integer.valueOf(jsonObject.get("stepNumber").toString())).goldReward(Integer.valueOf(jsonObject.get("goldAward").toString())).build();
                list2.add(actStepDetail);
            }
        }
        list2 = list2.stream().limit(size).collect(Collectors.toList());
        Collections.shuffle(list2);
        actStepDetailList.addAll(list2);
        return actStepDetailList;
    }

    @Override
    public ActStepDetailShareInfo queryContentShare(Long partyId) {

        ActStepDetailShareInfo actStepDetailShareInfo = new ActStepDetailShareInfo();
        actStepDetailShareInfo.setShareTitle("走路也能赚金币，一步一步走出发财路，老乡快一起来吧！");
        actStepDetailShareInfo.setShareContent("对应时间段内步数达到1000步，即可兑换金币，可提现！");
        actStepDetailShareInfo.setInviteContent("对应时间段内步数达到1000步，即可兑换金币，可提现！");
        if (partyId != null) {  
            int gold = actStepMapper.selectGoldRewardSum(ActStepDetail.builder().partyId(partyId).status(ExchangeStatusEnum.S.name()).build());
            actStepDetailShareInfo.setInviteTitle("我在乡邻步步生金已经赚取" + gold + "金币，可提现！老乡快加入我们的队列吧！");
        }
        actStepDetailShareInfo.setTitieImg(configMapper.selectConfig("STEP_SHARE_IMG"));
        actStepDetailShareInfo.setUrl(configMapper.selectConfig("STEP_SHARE_URL"));
        return actStepDetailShareInfo;
    }

    @Override
    public int rewardStepNumber(Long partyId, String type,String day) {
        //查询活动是否已结束
//        Activity step1 = activityMapper.selectActivity("STEP");
//        if(step1.getExpireDate().before(new Date()) ||  step1.getStartDate().after(new Date())){
//            throw new BizException(ActPreconditions.ResponseEnum.STEP_ACTIVITY_END);
//        }
        int gold = 0;
        //查询当前的时间
        String now = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        StepDetailEnum stepDetailType = StepDetailEnum.getType(type);
        String startTime = stepDetailType.getStartTime();
        String endTime = stepDetailType.getEndTime();
        //当前时间大于开始时间并且小于结束时间
        if (Long.valueOf(now) < Long.valueOf(startTime)) { //还没有到兑换时间
            logger.warn("兑换时间异常！", "现在的时间：" + now + ", 兑换的时间段为：" + type);
            throw new BizException(ActPreconditions.ResponseEnum.STEP_NOT_EXCHANGETIME);
        }
        if (Long.valueOf(now) > Long.valueOf(endTime)) {  //已经超过兑换时间
            logger.warn("兑换时间异常！", "现在的时间：" + now + ", 兑换的时间段为：" + type);
            throw new BizException(ActPreconditions.ResponseEnum.STEP_NOT_EXCHANGETIME);
        }
        //步数不足1000，无法兑换
        ActStepDetail actStepDetail = actStepMapper.selectOne(ActStepDetail.builder().day(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)).type(type).partyId(partyId).isDeleted("N").build());
        if (actStepDetail != null && actStepDetail.getStepNumber().compareTo(new Integer(1000)) < 0) {
            logger.warn("没有到兑换的步数！", com.alibaba.fastjson.JSON.toJSONString(actStepDetail));
            throw new BizException(ActPreconditions.ResponseEnum.STEP_NOT_STEPNUMBER);
        }
        //查询当前时间段是否兑换过
        if (actStepDetail != null && actStepDetail.getStatus().equals(ExchangeStatusEnum.S.name())) {    //已经兑换过了
            logger.warn("已经兑换过了！", com.alibaba.fastjson.JSON.toJSONString(actStepDetail));
            throw new BizException(ActPreconditions.ResponseEnum.STEP_REWARD_STEPNUMBER);
        }
//        //查询是否是第666次兑换的用户
//        int count = actStepMapper.selectCount(ActStepDetail.builder().status(ExchangeStatusEnum.S.name()).day(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)).isDeleted("N").build());
//        if (count == 665) {    //幸运奖：990金币
//            gold = 990;
//        } else {    //其他奖：每次兑换金币95%的概率兑换到[100,300]，5%的概率兑换到[301,500]
//            int i = (int) (Math.random() * 100+1);
//            if (i > 5) {  //95%的概率兑换到[100,300]
//                gold = (int) (Math.random() * 200 + 100);
//            } else { //5%的概率兑换到[301,500]
//                gold = (int) (Math.random() * 199 + 301);
//            }
//        }

        //随机兑换[5,30]g爱心值
        Map<String,Integer> lvLimit = JSON.parseObject(configMapper.selectConfig("STEP_EXCHANGE_LV"),Map.class);
        int least = lvLimit.get("least");
        int most = lvLimit.get("most");
        gold = (int) (Math.random() * (most-least) + least);
        actStepDetail.setGoldReward(gold);
        actStepDetail.setStatus(ExchangeStatusEnum.S.name());
        actStepDetail.setRewardTime(new Date());
        Boolean flag = actStepMapper.updateByPrimaryKey(actStepDetail) == 1;
        if (flag) {
            actStepMapper.updateDayTotail(partyId, day);
//            //给账户发金币
//            com.xianglin.cif.common.service.facade.model.Response<GoldcoinRecordVo> goldcoinRecordVoResponse = goldcoinServiceClient.doRecord(GoldcoinRecordVo.builder().system("act").amount(gold).fronPartyId(GOLD_SYS_ACCOUNT).type(ActivityEnum.ACT_STEP.name()).remark(ActivityEnum.ACT_STEP.getRemark()).toPartyId(partyId).requestId(GoldSequenceUtil.getSequence(partyId, sequenceMapper.getSequence())).build());
//            if (!com.google.common.base.Objects.equal(goldcoinRecordVoResponse.getCode(), 1000)) {
//                logger.error("添加金币失败！", com.alibaba.fastjson.JSON.toJSONString(goldcoinRecordVoResponse));
//                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
//            }
            ActPlant actPlant = actPlantMapper.findByPartyId(partyId);
            if (actPlant == null){
                throw new BizException(ActPreconditions.ResponseEnum.NOTReciveLV);
            }
            //该用户自有的爱心值，再加上本次兑换的爱心值
            actPlant.setLv(actPlant.getLv() + gold);
            actPlant.setTotalLv(actPlant.getTotalLv() + gold);
            actPlant.setUpdateTime(new Date());
            actPlantMapper.updateByPrimaryKeySelective(actPlant);

            //保存爱心值 出入明细
            ActPlantLvTran actPlantLvTran = ActPlantLvTran.builder().partyId(partyId).lv(gold).type(ActPlantEnum.TranType.STEP.name()).status(ActPlantEnum.StatusType.S.name()).isDeleted(Constant.Delete_Y_N.N.name()).build();
            actPlantLvTranMapper.insertSelective(actPlantLvTran);

            long tipCount = JSON.parseArray(configMapper.selectConfig("PLANT_TIP_LEVEL"), Integer.class).stream().filter(v -> v <= actPlant.getLv()).count();
            int count = actPlantTipMapper.selectCount(ActPlantTip.builder().partyId(partyId).type(ActPlantEnum.TipType.LEVEL.name()).build());
            while (count++ < tipCount) {
                actPlantTipMapper.insertSelective(ActPlantTip.builder().partyId(partyId).type(ActPlantEnum.TipType.LEVEL.name())
                        .status(ActPlantEnum.StatusType.I.name()).tip("我的树又长大了一些").build());
            }
        }
        return gold;
    }

    @Override
    public int queryConversionsByDate(Long partyId,String day) {
        Example example = new Example(ActStepDetail.class);
        example.and().andEqualTo("partyId",partyId).andEqualTo("day",day).andEqualTo("status","S").andEqualTo("isDeleted",Constant.Delete_Y_N.N.name());
        Integer count = actStepMapper.selectCountByExample(example);
        return count;
    }

    /**
     * 集合对象转换
     *
     * @param detailDtos
     * @return
     */
    private List<ActStepDetailDTO> convertActStepDetailDTOList(List<ActStepDetail> detailDtos) {
        List<ActStepDetailDTO> actStepDetailDTOList = detailDtos.stream().map(vo -> {
            ActStepDetailDTO actStepDetailDTO = null;
            actStepDetailDTO = new ActStepDetailDTO();
            try {
                BeanUtils.copyProperties(actStepDetailDTO, vo);
                com.xianglin.appserv.common.service.facade.model.Response<UserVo> userVoResponse = personalService.queryUser(vo.getPartyId());
                if (userVoResponse.getResult() != null && StringUtils.isNotEmpty(userVoResponse.getResult().getShowName())) {
                    actStepDetailDTO.setShowName(userVoResponse.getResult().getShowName());
                }
            } catch (IllegalAccessException e) {
                logger.warn("convertActStepDetailDTOList:" + e);
            } catch (InvocationTargetException e) {
                logger.warn("convertActStepDetailDTOList:" + e);
            }
            return actStepDetailDTO;
        }).collect(Collectors.toList());
        return actStepDetailDTOList;
    }
}
