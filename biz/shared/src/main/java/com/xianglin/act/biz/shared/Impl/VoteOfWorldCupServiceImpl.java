package com.xianglin.act.biz.shared.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.xianglin.act.biz.shared.VoteOfWorldCupService;
import com.xianglin.act.common.dal.enums.PrizeType;
import com.xianglin.act.common.dal.mappers.*;
import com.xianglin.act.common.dal.model.*;
import com.xianglin.act.common.service.integration.GoldcoinServiceClient;
import com.xianglin.act.common.service.integration.MessageServiceClient;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.BizException;
import com.xianglin.act.common.util.GoldSequenceUtil;
import com.xianglin.appserv.common.service.facade.model.Request;
import com.xianglin.appserv.common.service.facade.model.enums.Constant;
import com.xianglin.appserv.common.service.facade.model.vo.MsgVo;
import com.xianglin.cif.common.service.facade.model.Response;
import com.xianglin.cif.common.service.facade.vo.GoldcoinAccountVo;
import com.xianglin.cif.common.service.facade.vo.GoldcoinRecordVo;
import com.xianglin.core.model.enums.UserType;
import com.xianglin.core.model.enums.VoteItemType;
import com.xianglin.core.model.enums.VoteRecordStatus;
import com.xianglin.core.model.vo.VoteActivityBaseInfoVO;
import com.xianglin.core.model.vo.VoteItemVO;
import com.xianglin.core.model.vo.VoteRecord;
import com.xianglin.core.model.vo.VoterVO;
import com.xianglin.core.service.PrizeAwardUtils;
import com.xianglin.core.service.VoteActivityContext;
import com.xianglin.xlschedule.common.model.enums.RpcType;
import com.xianglin.xlschedule.common.service.spi.annotation.ServiceInterface;
import com.xianglin.xlschedule.common.service.spi.annotation.ServiceMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.xianglin.act.common.util.ActPreconditions.ResponseEnum.*;
import static com.xianglin.act.common.util.GlobalRequestContext.currentPartyId;
import static com.xianglin.core.model.enums.Constants.GOLD_SYS_ACCOUNT;
import static com.xianglin.core.service.VoteActivityContext.getCurrentVoteActivity;
import static com.xianglin.core.service.VoteActivityContext.getVoteActivityCode;

/**
 * @author yefei
 * @date 2018-06-13 14:48
 */
@Service
@ServiceInterface
public class VoteOfWorldCupServiceImpl implements VoteOfWorldCupService {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(VoteOfWorldCupServiceImpl.class);

    private final static String NO_DELETED = "0";

    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private GoldcoinServiceClient goldcoinServiceClient;

    @Resource
    private SequenceMapper sequenceMapper;

    @Resource
    private ActVoteItemMapper voteItemMapper;

    @Resource
    private ActVoteRelMapper voteRelMapper;

    @Resource
    private PrizeAwardUtils prizeAwardUtils;

    @Resource
    private CustomerAcquireRecordMapper customerAcquireRecordMapper;

    @Resource
    private MessageServiceClient messageServiceClient;

    @Override
    public VoteActivity getVoteActivity(String activityCode) {
        if (StringUtils.isBlank(activityCode)) {
            throw new BizException(ACTIVITY_CODE_NOT_EXIST);
        }
        VoteActivity currentActivity = Optional
                .ofNullable(activityMapper.selectVoteActByActCode(activityCode))
                .orElseThrow(() -> new BizException(ACT_NOT_EXIST));

        VoteActivityContext.setCurrentVoteActivity(currentActivity);

        Optional.ofNullable(currentActivity.getExpireDate())
                .ifPresent(expireDate -> {
                    if (System.currentTimeMillis() > expireDate.getTime()) {
                        throw new BizException(ACTIVITY_END);
                    }
                });
        return currentActivity;
    }

    @Override
    public VoterVO vote(long partyId) {
        // check status
        ActVoteItem voteItem = ActVoteItem.builder()
                .activityCode(getVoteActivityCode())
                .partyId(partyId)
                .build();
        ActVoteItem item = voteItemMapper.selectOne(voteItem);
        if (VoteItemType.KNOCK_OUT.name().equals(item.getStatus())) {
            throw new BizException(ActPreconditions.ResponseEnum.VOTE_KNOCK_OUT);
        }

        Response<GoldcoinAccountVo> response = goldcoinServiceClient.queryAccount(currentPartyId());
        if (response.getCode() != 1000) {
            throw new BizException("调用CIF查询金币信息失败。" + response.getMemo());
        }
        return VoterVO.builder()
                .partyId(currentPartyId())
                .goldCoin(BigDecimal.valueOf(response.getResult().getAmount()))
                .build();
    }

    @Override
    public void voteSubmit(long partyId, BigDecimal amount) {

        // check param
        int i = voteRelMapper.selectCount(ActVoteRel.builder()
                .activityCode(getVoteActivityCode())
                .toPartyId(currentPartyId())
                .status(VoteRecordStatus.LOCKED.name())
                .build());
        if (i > 0) {
            logger.warn("用户：{} 已经投过票，调用不合法", currentPartyId());
            throw new BizException(ActPreconditions.ResponseEnum.FAIL);
        }

        // gold coin record
        GoldcoinRecordVo recordVo = GoldcoinRecordVo.builder()
                .system("act")
                .amount(-amount.stripTrailingZeros().intValue())
                .fronPartyId(GOLD_SYS_ACCOUNT)
                .type("VOTE")
                .remark("世界杯投票")
                .toPartyId(currentPartyId()).build();
        String sequence = GoldSequenceUtil.getSequence(currentPartyId(), sequenceMapper.getSequence());
        recordVo.setRequestId(sequence);
        Response<GoldcoinRecordVo> response = goldcoinServiceClient.doRecordWithBalanceCheck(recordVo, 0);

        if (response.getCode() == 500003) {
            throw new BizException(VOTE_GOLD_NOT_ENOUGH);
        } else if (response.getCode() != 1000) {
            throw new BizException(response.getTips());
        }

        // insert vote_rel
        updateVoteRecord(currentPartyId(),
                partyId,
                amount);
    }

    @Override
    public VoteActivityBaseInfoVO index() {
        boolean hasExpire = getCurrentVoteActivity().getDisplayDate().isBefore(LocalDateTime.now());

        long timestamp = Duration
                .between(LocalDateTime.now(), getCurrentVoteActivity().getDisplayDate())
                .getSeconds();

        List<VoteItemVO> voteItems = voteItemMapper.selectItemListOfWorldCup().stream()
                .map(voteItem -> VoteItemVO.builder()
                        .partyId(voteItem.getPartyId())
                        .imageUrl(voteItem.getImages())
                        .description(voteItem.getDescription())
                        .voteNum(voteItem.getBaseVoteNum() + voteItem.getRealVoteNum())
                        .isKnockOut(VoteItemType.KNOCK_OUT.name().equals(voteItem.getStatus()))
                        .build())
                .collect(Collectors.toList());

        return VoteActivityBaseInfoVO.builder()
                .title(VoteActivityContext.getVoteActivityName())
                .carouselImgs(getCurrentVoteActivity().getCarouselImgs())
                .hasExpire(hasExpire)
                .timestamp(timestamp)
                .items(voteItems)
                .hasPartakeIn(
                        Optional.ofNullable(currentPartyId())
                                .map(partyId -> voteRelMapper.selectCount(
                                        ActVoteRel.builder()
                                                .activityCode(getVoteActivityCode())
                                                .partyId(currentPartyId())
                                                .status(VoteRecordStatus.LOCKED.name())
                                                .isDeleted(NO_DELETED)
                                                .build()))
                                .map(count -> count > 0)
                                .orElse(false))
                .amount(BigDecimal.valueOf(voteRelMapper.countGold(getVoteActivityCode())).add(BigDecimal.valueOf(10000)))
                .build();
    }

    @ServiceMethod(description = "淘汰队伍", rpcType = RpcType.service)
    @Override
    public void knockOut(String activityCode, Long id) {
        logger.info("-------- 淘汰队伍：activityCode: {}, id: {}", activityCode, id);
        Preconditions.checkNotNull(activityCode);
        Preconditions.checkNotNull(id);

        ActVoteItem voteItem = ActVoteItem.builder()
                .id(id)
                .activityCode(activityCode)
                .status(VoteItemType.KNOCK_OUT.name())
                .updateDate(LocalDateTime.now())
                .isDeleted(NO_DELETED)
                .build();

        voteItemMapper.updateByPrimaryKeySelective(voteItem);
    }

    @ServiceMethod(description = " 更新投票记录状态，更新后可以继续投票", rpcType = RpcType.service)
    @Override
    public void updateVoteRecordStatus(String activityCode, String date) {
        logger.info("-------- 解锁投票记录: {}", activityCode);
        Preconditions.checkNotNull(activityCode);

        if (StringUtils.isNotBlank(date)) {
            VoteActivity voteActivity = activityMapper.selectVoteActByActCode(activityCode);
            // 更新投票时间
            JSONObject jsonObject = JSON.parseObject(voteActivity.getActDesc(), Feature.OrderedField);
            jsonObject.put("活动时间", date);

            // 2018年06月28日 20:00-2018年07月04日 06:00
            activityMapper.updateActDesc(activityCode, jsonObject.toJSONString());
        }
        // 更新投票状态
        voteRelMapper.updateVoteRecordStatus(activityCode);
    }

    @ServiceMethod(description = "世界杯结算", rpcType = RpcType.service)
    @Override
    public void deal(String activityCode) {
        logger.info("-------- 世界杯结算: {}", activityCode);
        Preconditions.checkNotNull(activityCode);

        List<ActVoteItem> voteItems = voteItemMapper.select(
                ActVoteItem.builder()
                        .activityCode(activityCode)
                        .status(VoteItemType.ACTIVE.name())
                        .isDeleted(NO_DELETED)
                        .build());

        if (voteItems.size() != 1) {
            throw new BizException("世界杯结算异常, 还存在" + voteItems.size() + "支队伍");
        }

        final ActVoteItem voteItem = voteItems.get(0);
        // 查询投票者占比
        List<ActVoteRel> voteRels = voteRelMapper.selectVoterRatio(activityCode, voteItem.getPartyId());
        HashMap<Long, ActVoteRel> temp = Maps.newHashMap();

        voteRels.stream().forEach(vote -> {
            // update award amount
            voteRelMapper.updateByPrimaryKeySelective(ActVoteRel
                    .builder()
                    .id(vote.getId())
                    .awardAmount(vote.getAwardAmount())
                    .build());
            // 累加发送
            if (temp.containsKey(vote.getPartyId())) {
                ActVoteRel actVoteRel = temp.get(vote.getPartyId());
                actVoteRel.setAwardAmount(actVoteRel.getAwardAmount().add(vote.getAwardAmount()));
            } else {
                vote.setAwardAmount(vote.getAwardAmount());
                temp.put(vote.getPartyId(), vote);
            }
        });

        award(temp, activityCode);
    }

    private void award(HashMap<Long, ActVoteRel> temp, String activityCode) {
        temp.entrySet().stream().forEach(entry -> {
            try {
                Prize prize = Prize.builder()
                        .amount(entry.getValue().getAwardAmount())
                        .prizeName("世界杯金币奖励")
                        .activityCode(activityCode)
                        .build();
                prize.setPrizeType(PrizeType.XL_GOLD_COIN.name());

                prizeAwardUtils.award(Party.crateParty(entry.getKey()), prize);

                // insert acquire record
                CustomerAcquire acquire = CustomerAcquire.builder()
                        .mobilePhone(null)
                        .partyId(entry.getKey())
                        .prizeCode(prize.getPrizeCode())
                        .activityCode(activityCode)
                        .memcCode(prize.getMemcCode())
                        .prizeValue(prize.getAmount())
                        .userType(UserType.VOTER_APP_USER.name())
                        .build();

                customerAcquireRecordMapper.insertCustomerAcquireRecord(acquire);

                // 站内信
                Request<MsgVo> param = new Request<>();
                param.setReq(MsgVo.builder()
                        .partyId(entry.getKey())
                        .msgTitle("世界杯金币奖励")
                        .isSave(Constant.YESNO.YES)
                        .message(String.format("恭喜你，在激情世界杯竞猜赢大奖活动中，获得%s金币，感谢你的参与！", entry.getValue().getAwardAmount()))
                        .msgType(Constant.MsgType.CASHBONUS_TIP.name())
                        .loginCheck(Constant.YESNO.NO.code)
                        .passCheck(Constant.YESNO.NO.code).expiryTime(0)
                        .isDeleted("N")
                        .msgSource(Constant.MsgType.CASHBONUS_TIP.name()).build());

                com.xianglin.appserv.common.service.facade.model.Response<Boolean> booleanResponse
                        = messageServiceClient.sendMsg(param, Collections.singletonList(entry.getKey()));
                if (!booleanResponse.isSuccess()) {
                    throw new BizException("app奖励通知发送失败");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });

        /**
         * 更新奖励金额 null 为 0
         */
        voteRelMapper.updateAwardAmount(activityCode);

    }

    @Override
    public List<VoteRecord> voteRecord() {
        List<ActVoteRel> rels = voteRelMapper.selectVoteRecord(getVoteActivityCode(), currentPartyId());
        return rels.stream().map(rel -> VoteRecord.builder()
                .toPartyId(rel.getToPartyId())
                .amount(rel.getAmount())
                .award(rel.getAwardAmount())
                .name(rel.getName())
                .dateTime(rel.getCreateDate())
                .build()).collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = RuntimeException.class)
    public void updateVoteRecord(long partyId, long toPartyId, BigDecimal amount) {
        // 业务
        voteItemMapper.updateVoteNum(toPartyId);
        voteRelMapper.insertRecordOfWorldCup(
                getVoteActivityCode(),
                partyId,
                UserType.VOTER_APP_USER.name(),
                toPartyId,
                amount);
    }

}
