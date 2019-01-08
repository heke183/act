package com.xianglin.act.biz.service.implement;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.xianglin.act.biz.shared.ActPlantSharedService;
import com.xianglin.act.common.dal.mappers.*;
import com.xianglin.act.common.dal.model.*;
import com.xianglin.act.common.service.facade.ActPlantService;
import com.xianglin.act.common.service.facade.model.*;
import com.xianglin.act.common.service.integration.GoldcoinServiceClient;
import com.xianglin.act.common.util.*;
import com.xianglin.appserv.common.service.facade.app.PersonalService;
import com.xianglin.cif.common.service.facade.CustomersInfoService;
import com.xianglin.core.model.enums.ActPlantEnum;
import com.xianglin.gateway.common.service.spi.annotation.ServiceInterface;
import com.xianglin.gateway.common.service.spi.annotation.ServiceMethod;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Describe :
 * Created by xingyali on 2018/8/6 17:04.
 * Update reason :
 */
@Service
@org.springframework.stereotype.Service
@ServiceInterface(ActPlantService.class)
public class ActPlantServiceImpl implements ActPlantService {

    private static final Logger logger = LoggerFactory.getLogger(ActServiceImpl.class);

    @Autowired
    private SessionHelper sessionHelper;
    
    @Autowired
    private ActPlantSharedService actPlantSharedService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private WxApiUtils wxApiUtils2;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    protected SequenceMapper sequenceMapper;

    /**
     * 添加用户的明细记录
     * @param actPlantTaskDetailDTO
     * @return
     */
    @ServiceMethod(description = "添加用户的明细记录")
    @Override
    public Response<Boolean> insertActPlantTaskDetail(ActPlantTaskDetailDTO actPlantTaskDetailDTO) {
        RLock lock = redissonClient.getLock("ACT:PLANT:insertActPlantTaskDetail:" + actPlantTaskDetailDTO);
        if (!lock.tryLock()) {
            return Response.ofSuccess(false);
        }
        Boolean flag = null;
        try {
            actPlantTaskDetailDTO.setDay(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            flag = actPlantSharedService.insertActPlantTaskDetail(DTOUtils.map(actPlantTaskDetailDTO,ActPlantTaskDetail.class));
        } catch (Exception e) {
            logger.warn("insertActPlantTaskDetail",e);
        } finally {
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
        return Response.ofSuccess(flag);
    }

    /**
     *  兑换信息查询                                                                              E
     * @param actPlantLvTranPageDTO
     * @return
     */
    @Override
    public Response<List<ActPlantLvTranDTO>> queryPlantExchange(ActPlantLvTranPageDTO actPlantLvTranPageDTO) {
        List<ActPlantLvTranDTO> actPlantLvTrans = actPlantSharedService.queryPlantExchange(actPlantLvTranPageDTO);
        return Response.ofSuccess(actPlantLvTrans);
    }

    @Override
    public Response<Integer> queryPlantExchangeCount(ActPlantLvTranPageDTO actPlantLvTranPageDTO) {
       int count= actPlantSharedService.queryPlantExchangeCount(actPlantLvTranPageDTO);
        return Response.ofSuccess(count);
    }

    /**
     *  更新兑换信息
     * @param actPlantLvTranDTO
     * @return
     */
    @Override
    public Response<Boolean> updatePlantExchange(ActPlantLvTranDTO actPlantLvTranDTO) {
        Boolean flag = false;
        try {
           flag = actPlantSharedService.updatePlantExchange(DTOUtils.map(actPlantLvTranDTO,ActPlantLvTran.class));
        } catch (Exception e) {
            logger.warn("updatePlantExchange",e);
        }
        return Response.ofSuccess(flag);
    }

    /**
     * 查询兑换的礼品
     * @return
     */
    @Override
    public Response<List<ActPlantPrizeDTO>> queryActPlantPrize() {
        List<ActPlantPrizeDTO> actPlantPrizeDTOs = new ArrayList<>();
        try {
            List<ActPlantPrize> actPlantPrizes = actPlantSharedService.queryPrizeList();
            actPlantPrizes = actPlantPrizes.stream().filter(vo-> !vo.getCode().equals(ActPlantEnum.ActPlantPrizeCodeEnum.MONEY.desc)).collect(Collectors.toList());
            actPlantPrizeDTOs = DTOUtils.map(actPlantPrizes, ActPlantPrizeDTO.class);
        } catch (Exception e) {
            logger.warn("queryActPlantPrize",e);
        }
        return Response.ofSuccess(actPlantPrizeDTOs);
    }

    @Override
    public Response<PageResult<ActPlantNoticeDTO>> queryActPlantNotice(PageParam pageParam) {
        PageResult<ActPlantNoticeDTO> pageResult = new PageResult<>();
        Integer count = 0;
        List<ActPlantNoticeDTO> actPlantNoticeDTOList = Lists.newArrayList();
        try {
            actPlantNoticeDTOList = DTOUtils.map(actPlantSharedService.queryActPlantNotices(pageParam,false),ActPlantNoticeDTO.class);
            count = actPlantSharedService.queryActPlantNoticesCount();
        }catch (Exception e){
            logger.warn("queryActPlantNotice ",e);
        }

        pageResult.setCount(count);
        pageResult.setResult(actPlantNoticeDTOList);
        return Response.ofSuccess(pageResult);
    }

    @Override
    public Response<Boolean> updateActPlantNotice(ActPlantNoticeDTO actPlantNoticeDTO) {
        Boolean flag = false;
        try {
            flag = actPlantSharedService.updateActPlantNotice(DTOUtils.map(actPlantNoticeDTO,ActPlantNotice.class));
        }catch (Exception e){
            logger.warn("updateActPlantNotice",e);
        }
        return Response.ofSuccess(flag);
    }

    @Override
    public Response<Boolean> inserActPlantNotice(ActPlantNoticeDTO actPlantNoticeDTO) {
        Boolean flag = false;
        try {
            Long id = actPlantSharedService.inserActPlantNotice(DTOUtils.map(actPlantNoticeDTO,ActPlantNotice.class));
            if (id != null){
                flag = true;
            }
        }catch (Exception e){
            logger.warn("inserActPlantNotice",e);
        }
        return Response.ofSuccess(flag);
    }
    
    @ServiceMethod(description = "分享信息")
    @Override
    public Response<ActPlantShareDTO> share() {
        ActPlantShareDTO actPlantShareDTO = null;
        try {
            actPlantShareDTO = new ActPlantShareDTO();
            Long currPartyId = sessionHelper.getCurrentPartyId();
            actPlantShareDTO.setContent("每天都来收爱心，树苗长大你获利，点击前往》");
            actPlantShareDTO.setTitle("快来帮我领爱心，我要免费得888元");
            actPlantShareDTO.setImage(configMapper.selectConfig("PLANT_SHARE_IMG_URL"));
            if (currPartyId != null) {
                actPlantShareDTO.setUrlWX(wxApiUtils2.getAuthUrl(sessionHelper.getCurrentPartyId()));
            }
            actPlantShareDTO.setUrlWB(configMapper.selectConfig("ACT_PLANT_SHARE_WB"));
            actPlantShareDTO.setUrlQQ(configMapper.selectConfig("ACT_PLANT_SHARE") + "?partyId=" + currPartyId);
        } catch (UnsupportedEncodingException e) {
            logger.warn("share: ",e);
            return Response.ofFail("请求失败");
        }
        return Response.ofSuccess(actPlantShareDTO);
    }

    @Override
    public Response<Boolean> updateExchangeStatus(Long id,String status) {
       Boolean flag = actPlantSharedService.updateExchangeStatus(id,status);
       return Response.ofSuccess(flag);
    }


}
