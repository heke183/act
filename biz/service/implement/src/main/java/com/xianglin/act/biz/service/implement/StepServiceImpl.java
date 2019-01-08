package com.xianglin.act.biz.service.implement;

import com.google.common.collect.Lists;
import com.xianglin.act.biz.shared.StepSharedService;
import com.xianglin.act.common.dal.mappers.ActivityMapper;
import com.xianglin.act.common.dal.model.ActStepDetail;
import com.xianglin.act.common.service.facade.StepService;
import com.xianglin.act.common.service.facade.constant.ExchangeStatusEnum;
import com.xianglin.act.common.service.facade.constant.StepDetailEnum;
import com.xianglin.act.common.util.*;
import com.xianglin.act.common.service.facade.model.ActStepDetailDTO;
import com.xianglin.act.common.service.facade.model.ActStepDetailShareInfo;
import com.xianglin.act.common.service.facade.model.ActStepTotal;
import com.xianglin.act.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.app.PersonalService;
import com.xianglin.appserv.common.service.facade.model.AppSessionConstants;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import com.xianglin.gateway.common.service.spi.annotation.ServiceInterface;
import com.xianglin.gateway.common.service.spi.annotation.ServiceMethod;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lipf
 * @date 2018-07-16 17:51
 */
@Service
@ServiceInterface(StepService.class)
public class StepServiceImpl implements StepService {

    private static final Logger logger = LoggerFactory.getLogger(StepServiceImpl.class);
    
    @Autowired
    private StepSharedService stepSharedService;

    @Resource
    private RedissonClient redissonClient;

    @Autowired
    private SessionHelper sessionHelper;

    @Autowired
    private PersonalService personalService;
    
    @Autowired
    private ActivityMapper activityMapper;

    
    
    

    /**
     * 同步客户端数据
     *
     * 
     * @param details
     * @return
     */
    @ServiceMethod(description = "同步客户端数据")
    @Override
    public Response<List<ActStepDetailDTO>> synchStepDetail(List<ActStepDetailDTO> details,String day) {
        RLock lock = redissonClient.getLock("ACT:STEP:synchStepDet ail:" + sessionHelper.getCurrentPartyId());
        if (!lock.tryLock()) {
            return Response.ofSuccess(Lists.newArrayList());
        }
        
            try {
                logger.info("===========同步客户端数据，partyId:[[ {} ]]===========", sessionHelper.getCurrentPartyId());
                //判断客户端时间是否与当前服务器时间一致
                String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
                if(!today.equals(day)){   //时间不一致
                    return Response.ofFail(40001,"时间有误，请更正后再试");
                }
                List<ActStepDetail> detailDtos = details.stream().map(vo -> {
                    return ActStepDetail.builder().type(vo.getType()).stepNumber(vo.getStepNumber()).build();
                }).collect(Collectors.toList());
                detailDtos = stepSharedService.synchStepDetail(detailDtos,sessionHelper.getCurrentPartyId());
                details = detailDtos.stream().map(vo -> {
                    return ActStepDetailDTO.builder().type(vo.getType()).stepNumber(vo.getStepNumber()).status(vo.getStatus()).goldReward(vo.getGoldReward()).build();
                }).collect(Collectors.toList());
                return Response.ofSuccess(details);
            } catch (BizException e) {
                logger.warn("synchStepDetail: ",e);
                return Response.ofFail(Integer.valueOf(e.getResponseEnum().code),e.getResponseEnum().message);
            } finally {
                lock.unlock();
            }
    }

    /**
     * 查询用户步步生金活动总量
     *
     * @return
     */
    @ServiceMethod(description = "查询用户步步生金活动总量")
    @Override
    public Response<ActStepTotal> queryStepTotail() {
        Long partyId = sessionHelper.getCurrentPartyId();
        //累计参与天数
        int day = stepSharedService.queryPartakeDay(partyId,StepDetailEnum.ALL.name());
        //累计兑换次数
        int exchangeNumber = stepSharedService.queryConversions(partyId, ExchangeStatusEnum.S.name());
        //累计金币数量
        int gold = stepSharedService.queryGoldCoins(partyId, ExchangeStatusEnum.S.name());
        return Response.ofSuccess(ActStepTotal.builder().days(day).conversions(exchangeNumber).goldCoins(gold).build());
    }

    /**
     * 领取活动奖励
     *
     * @param type
     * @return
     */
    @ServiceMethod(description = "领取活动奖励")
    @Override
    public Response<Integer> reward(String type,String day) {
        RLock lock = redissonClient.getLock("ACT:ACTSTEP:REWARD:" + sessionHelper.getCurrentPartyId()+type);
        if (!lock.tryLock()) {
            return Response.ofSuccess(0);
        }
        try {
            //查询活动是否已结束
//            Activity step1 = activityMapper.selectActivity("STEP");
//            if(step1.getExpireDate().before(new Date()) ||  step1.getStartDate().after(new Date())){
//                return Response.ofFail(5005,"活动已结束！");
//            }
            Long partyId = sessionHelper.getCurrentPartyId();
            String did = sessionHelper.getSessionProp("did", String.class);
            //判断客户端时间是否与当前服务器时间一致
            String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            if(!today.equals(day)){   //时间不一致
                return Response.ofFail(5001,"时间有误，请更正后再试");
            }
            String sessionProp = sessionHelper.getSessionProp(AppSessionConstants.SYSTEM_TYPE, String.class);
            if(sessionProp.equals("IOS")){
                RAtomicLong atomicLong = redissonClient.getAtomicLong(did + day + type);
                atomicLong.expire(1,TimeUnit.DAYS);
                if(atomicLong.getAndIncrement()>3){
                    return Response.ofFail(40002,"该时间段兑换金币已到上限");
                } 
            }
            //兑换步数
            int gold =stepSharedService.rewardStepNumber(partyId,type,day);
            return Response.ofSuccess(gold);
        } catch (BizException e) {
            logger.warn("reward: ",e);
            return Response.ofFail(Integer.valueOf(e.getResponseEnum().code),e.getResponseEnum().tips);
        } finally {
            lock.unlock();
        }
    }
    

    /**
     * 查询排行榜
     *
     * @return
     */
    @ServiceMethod(description = "查询排行榜",alias = "com.xianglin.act.common.service.facade.StepService.queryRanking")
    @Override
    public Response<List<ActStepDetailDTO>> queryRanking() {
        List<ActStepDetailDTO> actStepDetailDTOList = null;
        try {
            String day = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            List<ActStepDetail> list = stepSharedService.queryTopList(day);
            if(list.size()>0){
                actStepDetailDTOList = convertActStepDetailDTOList(list); 
            }
        } catch (Exception e) {
            logger.warn("queryRanking: ",e);
            return Response.ofFail("请求失败");
        }
        return Response.ofSuccess(actStepDetailDTOList);
    }

    /**
     * 奖励明细查询
     *
     * @param lastId 最后一条数据id
     * @return
     */
    @ServiceMethod(description = "奖励明细查询")
    @Override
    public Response<List<ActStepDetailDTO>> queryRewardList(Long lastId) {
        List<ActStepDetailDTO> actStepDetailDTOList = null;
        try {
            actStepDetailDTOList = new ArrayList<>();
            Long partyId = sessionHelper.getCurrentPartyId();
            List<ActStepDetail> detailDtos =  stepSharedService.queryRewardList(partyId,lastId);
            if(detailDtos.size()>0){
                actStepDetailDTOList = convertActStepDetailDTOList(detailDtos);  
            }
        } catch (Exception e) {
            logger.warn("queryRanking: ",e);
            return Response.ofFail("请求失败");
        }
        return Response.ofSuccess(actStepDetailDTOList);
    }

    /**
     * 查询分享文案内容
     * @return
     */
    @ServiceMethod(description = "查询分享文案内容")
    @Override
    public Response<ActStepDetailShareInfo> queryContentShare() {
        //当前用户已经在此活动中赚取到的金币数
        Long partyId = sessionHelper.getCurrentPartyId();
        ActStepDetailShareInfo actStepDetailShareInfo = stepSharedService.queryContentShare(partyId);
        return Response.ofSuccess(actStepDetailShareInfo);
    }


    /*@ServiceMethod(description = "查询分享明细")
    @Override
    public Response<List<ActStepDetailDTO>> queryActStepDetailShare() {
        List<ActStepDetail> actStepDetails = stepSharedService.queryActStepDetailShare();
        //对象转换查询用户的真是姓名
        List<ActStepDetailDTO> actStepDetailDTOList = convertActStepDetailDTOList(actStepDetails);
        return Response.ofSuccess(actStepDetailDTOList);
    }*/

    /**
     * 集合对象转换
     * @param detailDtos
     * @return
     */
    private List<ActStepDetailDTO> convertActStepDetailDTOList(List<ActStepDetail> detailDtos){
        List<ActStepDetailDTO> actStepDetailDTOList = detailDtos.stream().map(vo -> {
            ActStepDetailDTO actStepDetailDTO = null;
            actStepDetailDTO =new ActStepDetailDTO();
            try {
                BeanUtils.copyProperties(actStepDetailDTO, vo);
                com.xianglin.appserv.common.service.facade.model.Response<UserVo> userVoResponse = personalService.queryUser(vo.getPartyId());
                if(userVoResponse.getResult() != null && StringUtils.isNotEmpty(userVoResponse.getResult().getShowName())){
                    actStepDetailDTO.setShowName(userVoResponse.getResult().getShowName());
                }
                if(vo.getRewardTime() != null){
                    actStepDetailDTO.setRewardTime(DateUtils.formatDate(vo.getRewardTime(),"yyyy-MM-dd HH:mm:ss"));   
                }
            } catch (IllegalAccessException e) {
               logger.warn("convertActStepDetailDTOList:"+e);
            } catch (InvocationTargetException e) {
                logger.warn("convertActStepDetailDTOList:"+e);
            }
            return actStepDetailDTO;
        }).collect(Collectors.toList());
        return actStepDetailDTOList;
    }
    
    private ActStepDetailDTO converActStepDetailDTO(ActStepDetail actStepDetail){
        ActStepDetailDTO actStepDetailDTO = null;
        actStepDetailDTO =new ActStepDetailDTO();
        try {
            BeanUtils.copyProperties(actStepDetailDTO, actStepDetail);
        } catch (IllegalAccessException e) {
            logger.warn("convertActStepDetailDTOList:"+e);
        } catch (InvocationTargetException e) {
            logger.warn("convertActStepDetailDTOList:"+e);
        }
        return  actStepDetailDTO;
    }


}
