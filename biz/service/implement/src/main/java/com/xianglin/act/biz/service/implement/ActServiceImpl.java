package com.xianglin.act.biz.service.implement;

import com.google.common.collect.Lists;
import com.xianglin.act.biz.shared.PopTipAssembleService;
import com.xianglin.act.biz.shared.PopTipLogService;
import com.xianglin.act.biz.shared.TabTipCloseEvent;
import com.xianglin.act.common.dal.mappers.ActivityMapper;
import com.xianglin.act.common.dal.mappers.CustomerPrizeMapper;
import com.xianglin.act.common.dal.model.Activity;
import com.xianglin.act.common.dal.model.ActivityConfig;
import com.xianglin.act.common.dal.model.CustomerPrize;
import com.xianglin.act.common.dal.model.Prize;
import com.xianglin.act.common.dal.support.pop.PopTipRequest;
import com.xianglin.act.common.service.facade.ActService;
import com.xianglin.act.common.service.facade.constant.PopTipTypeEnum;
import com.xianglin.act.common.service.facade.model.ActivityDTO;
import com.xianglin.act.common.service.facade.model.Response;
import com.xianglin.act.common.util.DateUtils;
import com.xianglin.act.common.util.SessionHelper;
import com.xianglin.appserv.common.service.facade.model.enums.Constant;
import com.xianglin.fala.session.Session;
import com.xianglin.gateway.common.service.spi.annotation.ServiceInterface;
import com.xianglin.gateway.common.service.spi.annotation.ServiceMethod;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.xianglin.act.common.util.DateUtils.DATETIME_FMT;

/**
 * @author yefei
 * @date 2018-04-02 13:22
 */
@com.alibaba.dubbo.config.annotation.Service
@org.springframework.stereotype.Service
@ServiceInterface(ActService.class)
public class ActServiceImpl implements ActService {

    private static final Logger logger = LoggerFactory.getLogger(ActServiceImpl.class);

    @Autowired
    private SessionHelper sessionHelper;

    @Autowired
    private PopTipAssembleService popTipsAssembleService;

    @Autowired
    private PopTipLogService popTipLogService;

    @Resource
    private RedissonClient redissonClient;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private com.xianglin.act.biz.shared.ActService actService;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private CustomerPrizeMapper customerPrizeMapper;

    @ServiceMethod(description = "查询正在进行的活动")
    @Override
    public Response<List<ActivityDTO>> selectAct() {

        RLock lock = redissonClient.getLock("ACT:POP_TIP:SELECTS:" + sessionHelper.getCurrentPartyId());
        if (!lock.tryLock()) {
            return Response.ofSuccess(Lists.newArrayList());
        }
        try {
            logger.info("===========请求弹框列表，partyId:[[ {} ]]===========", sessionHelper.getCurrentPartyId());
            Session session = sessionHelper.getSession();
            PopTipRequest popTipRequest = PopTipRequest.ofSession(session);
            Optional<List<ActivityDTO>> activityDTOS = popTipsAssembleService.assemblePopTips(popTipRequest);
            List<ActivityDTO> list = activityDTOS.orElse(Collections.emptyList());
            popTipLogService.batchLog(list);
            return Response.ofSuccess(list);
        } finally {
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }


    /**
     * @param popTipType 弹框类型
     * @param id         弹框id
     * @return
     */
    @ServiceMethod(description = "用户开51活动")
    @Override
    public Response<?> open(Integer popTipType, Long id) {

        checkArgument(popTipType != null, "参数无效：弹窗类型不能为空");
        if (popTipType != PopTipTypeEnum.POP_TIP_OF_ONE_BUTTON.getCode()) {
            checkArgument(id != null, "参数无效：弹窗id不能为空");
        }
        Long partyId = sessionHelper.getCurrentPartyId();
        TabTipCloseEvent popTipColseEvent = new TabTipCloseEvent(this, partyId, popTipType, id);
        applicationContext.publishEvent(popTipColseEvent);
        return Response.ofSuccess(null);
    }

    @Override
    public Response<Map<String, String>> queryActConfig(String activityCode) {
        Map<String,String> result = actService.queryActConfigList(activityCode).stream().collect(Collectors.toMap(ActivityConfig::getConfigKey,v->v.getConfigValue()));
        return Response.ofSuccess(result);
    }

    @Override
    public Response<Boolean> updateActConfig(String activityCode, Map<String, String> config) {
        Boolean flag = false;
        try {
            for (String key :config.keySet()){
                actService.updateActConfig(activityCode,key,config.get(key));
            }
            Activity activity = new Activity();
            activity.setActivityCode(activityCode);
            activity = activityMapper.selectOne(activity);
            if (StringUtils.isNotEmpty(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.REGISTER_START_TIME.name())
                    && StringUtils.isNotEmpty(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_START_TIME.name())){
                String regTime = config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.REGISTER_START_TIME.name());
                String voteTime = config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_START_TIME.name());
                Date start = queryActivityTime(regTime,voteTime,"START");
                activity.setStartDate(start);
            }
            if (StringUtils.isNotEmpty(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.REGISTER_END_TIME.name())
                    && StringUtils.isNotEmpty(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_END_TIME.name())){
                String regTime = config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.REGISTER_END_TIME.name());
                String voteTime = config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_END_TIME.name());
                Date end = queryActivityTime(regTime,voteTime,"END");
                activity.setExpireDate(end);
            }
            activityMapper.updateByPrimaryKeySelective(activity);

            String prizeCode = com.xianglin.act.common.service.facade.constant.ActivityConfig.PrizeType.XL_GOLD_COIN.name();
            String minValue = config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_MIN_GOLD.name());
            String maxValue = config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_MAX_GOLD.name());
            if (StringUtils.isNotEmpty(minValue) && StringUtils.isNotEmpty(maxValue)){
                customerPrizeMapper.updateCustomerPrizeMaxAndMinValue("VOTE_VOTER",Integer.parseInt(maxValue),Integer.parseInt(minValue),prizeCode);
            }else {
                customerPrizeMapper.updateCustomerPrizeMaxAndMinValue("VOTE_VOTER",0,0,prizeCode);
            }
            flag = true;
        }catch (Exception e){
            logger.warn("updateActConfig",e);
        }
        return Response.ofSuccess(flag);
    }

    @Override
    public Response<Boolean> insertActivityConfig(String activityCode, Map<String, String> config) {
        Boolean flag = false;
        try {
            for (String key :config.keySet()){
                ActivityConfig activityConfig = ActivityConfig.builder()
                        .activityCode(activityCode)
                        .configKey(key)
                        .configValue(config.get(key))
                        .createTime(new Date())
                        .updateTime(new Date())
                        .isDeleted(Constant.Delete_Y_N.N.name()).build();
                actService.insertActivityConfig(activityConfig);
            }
            flag = true;
        }catch (Exception e){
            logger.warn("insertActivityConfig",e);
        }
        return Response.ofSuccess(flag);
    }

    /**
     * 查询活动时间
     * @param regTime
     * @param voTime
     * @return
     */
    private Date queryActivityTime(String regTime,String voTime,String type) {
        Date voteTime = com.xianglin.act.common.util.DateUtils.formatStr(voTime, DATETIME_FMT);
        if (StringUtils.isEmpty(regTime)) {
            return voteTime;
        }
        //活动报名开始时间
        Date registerTime = com.xianglin.act.common.util.DateUtils.formatStr(regTime, DATETIME_FMT);
        if (StringUtils.contains(type, "END")) {
            if (registerTime.compareTo(voteTime) > 0) {
                return registerTime;
            } else {
                return voteTime;
            }
        } else {
            if (registerTime.compareTo(voteTime) < 0) {
                return registerTime;
            } else {
                return voteTime;
            }
        }
    }
}
