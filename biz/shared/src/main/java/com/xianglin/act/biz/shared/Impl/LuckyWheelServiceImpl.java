package com.xianglin.act.biz.shared.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Objects;
import com.xianglin.act.biz.shared.LuckyWheelService;
import com.xianglin.act.common.dal.mappers.ActPlantMapper;
import com.xianglin.act.common.dal.mappers.CustomerAcquireRecordMapper;
import com.xianglin.act.common.dal.model.CustomerAcquire;
import com.xianglin.act.common.dal.model.Prize;
import com.xianglin.act.common.service.integration.ActivityInviteServiceClient;
import com.xianglin.act.common.service.integration.CustomersInfoServiceClient;
import com.xianglin.act.common.service.integration.GoldcoinServiceClient;
import com.xianglin.act.common.service.integration.MessageServiceClient;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.BizException;
import com.xianglin.appserv.common.service.facade.model.vo.ActivityInviteDetailVo;
import com.xianglin.cif.common.service.facade.model.CustomersDTO;
import com.xianglin.cif.common.service.facade.model.Response;
import com.xianglin.cif.common.service.facade.vo.GoldcoinAccountVo;
import com.xianglin.core.model.CheckMessageVO;
import com.xianglin.core.model.CustomerDetail;
import com.xianglin.core.model.Player;
import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;
import com.xianglin.core.model.enums.Constants;
import com.xianglin.core.model.enums.UserEnv;
import com.xianglin.core.service.ActivityContext;
import com.xianglin.core.service.strategy.CustomerContext;
import com.xianglin.xlStation.base.enums.XLStationEnums;
import com.xianglin.xlStation.base.model.SmsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

import static com.xianglin.act.common.util.ActPreconditions.ResponseEnum.CUSTOMER_INFO_MISS;
import static com.xianglin.core.service.ActivityContext.getActivityCode;

/**
 * @author yefei
 * @date 2018-01-18 14:57
 */
@Service("activityService")
public class LuckyWheelServiceImpl implements LuckyWheelService {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(LuckyWheelServiceImpl.class);

    private final static String MESSAGE_CONTENT = "亲爱的用户，您本次验证码是#{XXX}，该验证码30分钟有效。";

    @Resource
    private CustomersInfoServiceClient customersInfoServiceClient;

    @Resource
    private CustomerAcquireRecordMapper customerAcquireRecordMapper;

    @Resource
    private MessageServiceClient messageServiceClient;

    @Resource
    private GoldcoinServiceClient goldcoinServiceClient;

    @Resource
    private CustomerContext customerContext;

    @Resource
    private ActivityInviteServiceClient activityInviteServiceClient;
    
    @Autowired
    private ActPlantMapper actPlantMapper;

    @Override
    public Prize start(ActivityRequest<Player> request) {
        Player player = request.getRequest();
        logger.debug("partyId: {}， 手机号：{} 参与活动。", player.getPartyId(), player.getMobilePhone());
        ActivityResponse response = new ActivityResponse();
        customerContext.handle(request, response);
        Prize prize = response.getPrize();
        return prize;
    }

    @Override
    public Set<CustomerAcquire> customerAcquireRecord() {
        Set<CustomerAcquire> customerAcquires = customerAcquireRecordMapper.selectCustomerAcquireRecord(getActivityCode());
        return customerAcquires;
    }

    @Override
    public CustomerDetail customerCount(Long partyId) {

        CustomerDetail customerDetail = new CustomerDetail();
        long customerCount = customerAcquireRecordMapper.selectCustomerCount(getActivityCode());
        customerDetail.setCustomerCount(customerCount + 300);

        // 新用户
        if (partyId == null) {
            long recordCount = customerAcquireRecordMapper.selectNewCustomerRecordCount(partyId, getActivityCode());
            customerDetail.setAvailableCount(1 - recordCount <= 0 ? 0 : 1);
        } else { // 老用户
            Response<GoldcoinAccountVo> goldcoinAccoutResponse = goldcoinServiceClient.queryAccount(partyId);
            if (Objects.equal(goldcoinAccoutResponse.getCode(), 1000)) {
                customerDetail.setGold(goldcoinAccoutResponse.getResult().getAmount());
            } else {
                logger.error("查询用户金币信息失败: {}", JSON.toJSONString(goldcoinAccoutResponse));
                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
            }
        }
        return customerDetail;
    }

    @Override
    public void sendMessage(Player player) {

        Response<CustomersDTO> customersDTOResponse = customersInfoServiceClient.selectByMobilePhone(player.getMobilePhone());

        if (customersDTOResponse.isSuccess()) {
            CustomersDTO result = customersDTOResponse.getResult();
            if (result.getRoleDTOs() != null) {
                long count = result.getRoleDTOs().stream().filter(role -> Constants.APP_USER.equals(role.getRoleCode())).count();
                ActPreconditions.checkCondition(count > 0, ActPreconditions.ResponseEnum.ALREADY_REGISTER);
            }

            // 新用户
            long l = customerAcquireRecordMapper.selectNewCustomerRecordCount(result.getPartyId(), getActivityCode());
            if (l > 0) {
                throw new BizException(ActPreconditions.ResponseEnum.ALREADY_PLAY);
            }
        }

        com.xianglin.xlStation.base.model.Response response = messageServiceClient.sendSmsCode(player.getMobilePhone(), MESSAGE_CONTENT, String.valueOf(60 * 30));
        if (!(XLStationEnums.ResultSuccess.getCode() == response.getBussinessCode())) {
            logger.error("验证码发送失败：{}", JSON.toJSONString(response));
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
    }

    @Override
    public CheckMessageVO checkMessage(CheckMessageVO checkMessageVO) {
        ActPreconditions.checkNotNull(checkMessageVO.getFromPartyId(), ActPreconditions.ResponseEnum.PARTY_IS_NULL);

        SmsResponse smsResponse = messageServiceClient.checkSmsCode(checkMessageVO.getMobilePhone(), checkMessageVO.getCode(), Boolean.TRUE);
        ActPreconditions.checkCondition(!(XLStationEnums.ResultSuccess.getCode() == smsResponse.getBussinessCode()),
                ActPreconditions.ResponseEnum.CHECK_MESSAGE_FAIL);

        // 开户
        CustomersDTO customersDTO = new CustomersDTO();
        customersDTO.setMobilePhone(checkMessageVO.getMobilePhone());
        customersDTO.setCreator(checkMessageVO.getMobilePhone());
        Response<CustomersDTO> openAccountResponse = customersInfoServiceClient.openAccount(customersDTO, "act");
        ActPreconditions.checkCondition(!openAccountResponse.isSuccess(), ActPreconditions.ResponseEnum.ERROR);

        // 分享添加金币
        ActivityInviteDetailVo vo = new ActivityInviteDetailVo();
        vo.setSource("大转盘");
        vo.setLoginName(checkMessageVO.getMobilePhone());
        vo.setRecPartyId(checkMessageVO.getFromPartyId());
        vo.setActivityCode("109");
        com.xianglin.appserv.common.service.facade.model.Response<Boolean> invite = activityInviteServiceClient.invite(vo);
        logger.info("添加分享记录： {}", JSON.toJSONString(invite));
        ActPreconditions.checkCondition(!invite.getResult(), ActPreconditions.ResponseEnum.ERROR);

        checkMessageVO.setPartyId(openAccountResponse.getResult().getPartyId());
        return checkMessageVO;
    }

    @Override
    public String activityRule(UserEnv userEnv) {
        JSONObject jsonObject = JSON.parseObject(ActivityContext.get().getActivityRule());
        if (userEnv != null) {
            return jsonObject.get(userEnv.name()).toString();
        } else {
            return jsonObject.get(UserEnv.OUTER.name()).toString();
        }
    }

    @Override
    public Integer queryLv(Long partyId) {
        return actPlantMapper.findByPartyId(partyId).getLv();
    }
}
