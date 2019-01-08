package com.xianglin.act.biz.shared.Impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.xianglin.act.biz.shared.ActPlantSharedService;
import com.xianglin.act.biz.shared.StepSharedService;
import com.xianglin.act.common.dal.mappers.*;
import com.xianglin.act.common.dal.model.*;
import com.xianglin.act.common.service.facade.model.ActPlantLvTranDTO;
import com.xianglin.act.common.service.facade.model.ActPlantLvTranPageDTO;
import com.xianglin.act.common.service.facade.model.PageParam;
import com.xianglin.act.common.service.integration.CustomersInfoServiceClient;
import com.xianglin.act.common.service.integration.GoldcoinServiceClient;
import com.xianglin.act.common.service.integration.MessageServiceClient;
import com.xianglin.act.common.service.integration.UserRelationServiceClient;
import com.xianglin.act.common.util.*;
import com.xianglin.appserv.common.service.facade.MessageService;
import com.xianglin.appserv.common.service.facade.app.PersonalService;
import com.xianglin.appserv.common.service.facade.model.Request;
import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.enums.Constant;
import com.xianglin.appserv.common.service.facade.model.vo.MsgVo;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import com.xianglin.cif.common.service.facade.CustomersInfoService;
import com.xianglin.cif.common.service.facade.model.CustomersDTO;
import com.xianglin.cif.common.service.facade.vo.GoldcoinRecordVo;
import com.xianglin.core.model.enums.ActPlantEnum;
import com.xianglin.core.model.enums.ActivityEnum;
import com.xianglin.core.model.enums.Constants;
import com.xianglin.core.model.vo.*;
import com.xianglin.core.service.AttendanceUserStatusService;
import com.xianglin.xlStation.base.enums.XLStationEnums;
import com.xianglin.xlStation.base.model.SmsResponse;
import com.xianglin.xlnodecore.common.service.facade.BankReceiptService;
import com.xianglin.xlnodecore.common.service.facade.NodeService;
import com.xianglin.xlnodecore.common.service.facade.req.NodeReq;
import com.xianglin.xlnodecore.common.service.facade.resp.NodeResp;
import com.xianglin.xlnodecore.common.service.facade.vo.BankReceiptVo;
import com.xianglin.xlnodecore.common.service.facade.vo.NodeVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.joda.time.DateTime;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.xianglin.act.common.util.ActPreconditions.ResponseEnum.*;
import static com.xianglin.act.common.util.DTOUtils.map;

/**
 * @author ex-jiangyongtao
 * @date 2018/8/2  17:49
 */
@Service("actPlantSharedService")
public class ActPlantSharedServiceImpl implements ActPlantSharedService {

    private final static String MESSAGE_CONTENT = "你的验证码是#{XXX}，如非本人操作，请忽略本短信";

    private static final Logger logger = LoggerFactory.getLogger(ActPlantSharedServiceImpl.class);

    @Autowired
    private ActPlantMapper actPlantMapper;

    @Autowired
    private ActPlantTipMapper actPlantTipMapper;

    @Autowired
    private ActPlantTaskMapper actPlantTaskMapper;

    @Autowired
    private PersonalService personalService;

    @Autowired
    private AttendanceUserStatusService attendanceUserStatusService;

    @Autowired
    private ActPlantTaskDetailMapper actPlantTaskDetailMapper;

    @Autowired
    private ActPlantLvTranMapper actPlantLvTranMapper;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private BankReceiptService bankReceiptService;

    @Autowired
    private ActPlantLvMapper actPlantLvMapper;

    @Autowired
    private ActPlantPrizeMapper actPlantPrizeMapper;

    @Autowired
    private ConfigMapper configMapper;

    @Resource
    private MessageServiceClient messageServiceClient;

    @Resource
    private CustomersInfoServiceClient customersInfoServiceClient;

    @Resource
    private WxApiUtils wxApiUtils2;

    @Autowired
    private UserRelationServiceClient userRelationServiceClient;

    @Autowired
    private CustomersInfoService customersInfoService;

    @Resource
    private RedissonClient redissonClient;

    @Autowired
    private GoldcoinServiceClient goldcoinServiceClient;

    @Autowired
    protected SequenceMapper sequenceMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ActPlantNoticeMapper actPlantNoticeMapper;

    @Autowired
    private StepSharedService stepSharedService;

    /**
     * 默认异步线程池
     */
    private final Executor executor = Executors.newFixedThreadPool(32, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    });

    /**
     * 排行榜前10
     *
     * @return
     */
    @Override
    public ActPlantRankingVo queryRankingList(Long partyId) {
        ActPlantRankingVo actPlantRankingVo = null;
        try {
            actPlantRankingVo = new ActPlantRankingVo();
            Example example = new Example(ActPlant.class);
            example.orderBy("lv").desc();
            example.and().andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name());
            List<ActPlant> actPlantList = actPlantMapper.selectByExampleAndRowBounds(example, new RowBounds(0, 10));
            if (actPlantList.size() > 0 && actPlantList.get(0).getPartyId().equals(partyId)) { //第一名
                actPlantRankingVo.setStatus("ONE");
            }
            ActPlantRankingVo finalActPlantRankingVo = actPlantRankingVo;
            List<ActPlantVo> actPlantVos = actPlantList.parallelStream().map(vo -> {
                if (StringUtils.isEmpty(finalActPlantRankingVo.getStatus()) && vo.getPartyId().equals(partyId)) { //在榜内
                    finalActPlantRankingVo.setStatus("OnRank");
                }
                ActPlantVo actPlantVo = null;
                try {
                    actPlantVo = map(vo, ActPlantVo.class);
                    //查询用户名称
                    Response<UserVo> userVoResponse = personalService.queryUser(vo.getPartyId());
                    if (userVoResponse.getResult() != null && StringUtils.isNotEmpty(userVoResponse.getResult().getShowName())) {
                        actPlantVo.setShowName(userVoResponse.getResult().getShowName());
                    } else {
                        com.xianglin.cif.common.service.facade.model.Response<CustomersDTO> customersDTOResponse = customersInfoService.selectByPartyId(vo.getPartyId());
                        if (customersDTOResponse.getResult() != null) {
                            String mobilePhone = customersDTOResponse.getResult().getMobilePhone();
                            actPlantVo.setShowName(StringUtils.substring(mobilePhone, 0, 3) + "***" + StringUtils.substring(mobilePhone, 7));
                        }
                    }
                } catch (Exception e) {
                    logger.warn("actPlantList", e);
                }
                return actPlantVo;
            }).collect(Collectors.toList());
            actPlantRankingVo = finalActPlantRankingVo;
            if (StringUtils.isEmpty(actPlantRankingVo.getStatus())) { //榜上无名，赶紧做任务
                actPlantRankingVo.setStatus("NotRank");
            }
            actPlantRankingVo = ActPlantRankingVo.builder().status(actPlantRankingVo.getStatus()).actPlantVoList(actPlantVos).build();
        } catch (Exception e) {
            logger.warn("queryRankingList shared", e);
        }
        return actPlantRankingVo;
    }


    /**
     * 查近三天的消息明细列表
     *
     * @return
     */
    @Override
    public List<ActPlantMessageDetailVo> messageDetailsList(Long partyId) {
        try {
            List<ActPlantMessageDetailVo> actPlantMessageDetailVos = new LinkedList<>();
            String startTime = null;
            String endTime = null;
            //查最后一天的消息
            Example example = new Example(ActPlantTip.class);
            List<String> list = new ArrayList<>();
            list.add(ActPlantEnum.TipType.TIP.name());
            list.add(ActPlantEnum.TipType.LEVEL.name());
            example.and().andIn("type", list).andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name()).andEqualTo("partyId", partyId);
            example.orderBy("createTime").desc();
            List<ActPlantTip> actPlantTips = actPlantTipMapper.selectByExampleAndRowBounds(example, new RowBounds(0, 1));
            if (actPlantTips.size() == 0) {
                return null;
            }
            String lastDay = DateUtils.formatDate(actPlantTips.get(0).getCreateTime(), "yyyy-MM-dd");
            startTime = lastDay + " 00:00:00";
            endTime = lastDay + " 23:59:59";
            if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
                String pattern = "yyyy-MM-dd HH:mm:ss";
                //查最后一天的消息
                actPlantMessageDetailVos.add(queryTipsByDate(startTime, endTime, partyId));
                //查开始时间减1天的消息
                actPlantMessageDetailVos.add(queryTipsByDate(DateUtils.skipDateTime(startTime, -1, pattern), DateUtils.skipDateTime(endTime, -1, pattern), partyId));
                //查开始时间减2天的消息
                actPlantMessageDetailVos.add(queryTipsByDate(DateUtils.skipDateTime(startTime, -2, pattern), DateUtils.skipDateTime(endTime, -2, pattern), partyId));
            }
            return actPlantMessageDetailVos;
        } catch (Exception e) {
            logger.warn("messageDetailsList", e);
        }
        return null;
    }

    /**
     * 根据某一天开始时间和结束时间查消息
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private ActPlantMessageDetailVo queryTipsByDate(String startTime, String endTime, Long partyId) {
        try {
            Example example = new Example(ActPlantTip.class);
            List<String> list = new ArrayList<>();
            list.add(ActPlantEnum.TipType.TIP.name());
            list.add(ActPlantEnum.TipType.LEVEL.name());
            example.and().andIn("type", list).andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name()).andEqualTo("partyId", partyId);
            example.orderBy("createTime").desc();
            example.and().andLessThanOrEqualTo("createTime", endTime);
            example.and().andGreaterThanOrEqualTo("createTime", startTime);
            List<ActPlantTip> actPlantTips = actPlantTipMapper.selectByExample(example);
            ActPlantMessageDetailVo actPlantMessageDetailVo = new ActPlantMessageDetailVo();
            actPlantMessageDetailVo.setDay(convertDate(startTime));//时间转换  07月27日
            if (actPlantTips.size() > 0) {
                List<ActPlantTipVo> actPlantTipVos = map(actPlantTips, ActPlantTipVo.class);
                actPlantTipVos = actPlantTipVos.stream().map(vo -> {//时间转换成格式： HH:mm
                    vo.setDateTime(DateUtils.formatDate(vo.getCreateTime(), "HH:mm"));
                    return vo;
                }).collect(Collectors.toList());
                actPlantMessageDetailVo.setActPlantTipVos(actPlantTipVos);
            }
            return actPlantMessageDetailVo;
        } catch (Exception e) {
            logger.warn("queryTipsByDate", e);
        }
        return null;
    }

    /**
     * 日期转换成格式： 07月27日
     *
     * @param startTime
     * @return
     */
    private String convertDate(String startTime) {
        if (StringUtils.isNotEmpty(startTime)) {
            String month = startTime.substring(5, 7);
            String day = startTime.substring(8, 10);
            return month + "月" + day + "日";
        }
        return null;
    }

    /**
     * 查用户的任务表
     *
     * @return
     */
    @Override
    public List<ActPlantTaskVo> task(Long partyId) {
        Response<UserVo> userVoResponse = personalService.queryUser(partyId);
        //查询任务列表
        Example example = new Example(ActPlantTask.class);
        example.and().andEqualTo("type", ActPlantEnum.ActPlantTaskTypeEnum.USERTASK.name()).andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name());
        example.orderBy("id").asc();
        List<ActPlantTask> actPlantTasks = actPlantTaskMapper.selectByExample(example);
        //存款仅站长可见
        if (!userVoResponse.getResult().getUserType().equals("nodeManager")) {
            actPlantTasks = actPlantTasks.stream().filter(vo -> !vo.getCode().equals(ActPlantEnum.ActPlantTaskCodeEnum.DEPOSIT.desc)).collect(Collectors.toList());
        }

        List<ActPlantTaskVo> actPlantTaskVos = actPlantTasks.stream().map(vo -> {
            ActPlantTaskVo actPlantTaskVo = null;
            try {
                actPlantTaskVo = DTOUtils.map(vo, ActPlantTaskVo.class);
                actPlantTaskVo.setStatus("I");
                if (vo.getCode().equals(ActPlantEnum.ActPlantTaskCodeEnum.SHARE_RECEIVE_LOVE.desc)) { //分享领取爱心
                    //查今天的分享次数
                    int count = queryPlantTaskDetailCountByCode(vo.getCode(), partyId);
                    if (count >= 4) {
                        //大于等于4次 返回状态 为明天再来 S
                        actPlantTaskVo.setStatus("S");
                    } else {
                        //小于 4次 返回状态 I 5g 加 还有多少 时间可以继续分享
                        actPlantTaskVo.setCompleteCount(count);
                        //查出今天最近一次分享的时间
                        Long time = shareReceiveLoveTime(partyId, vo.getCode());
                        if (time != null && time > 0) {
                            actPlantTaskVo.setCountDown(time);
                        }
                    }
                }
                if (vo.getCode().equals(ActPlantEnum.ActPlantTaskCodeEnum.PUNCH_IN.desc)) { //去打卡
                    //查询今天是否打卡过 打卡了 状态 为明天再来 S
                    //结束日期
                    Boolean flag = attendanceUserStatusService.hasSignUp(partyId, LocalDate.now(), LocalDate.now().plusDays(1));
                    if (flag) {
                        //打卡了 状态 为明天再来 S
                        actPlantTaskVo.setStatus("S");
                    }
                    actPlantTaskVo.setUrl(configMapper.selectConfig("PLANT_GOLD_COINS"));
                }
                if (vo.getCode().equals(ActPlantEnum.ActPlantTaskCodeEnum.INVITE.desc)) { //邀请好友来种树
                    //查邀请好友次数状态为I 和 邀请好友完成数
                    int count = queryPlantTaskDetailCountByCode(vo.getCode(), partyId);
                    actPlantTaskVo.setCompleteCount(count);
                }
                if (vo.getCode().equals(ActPlantEnum.ActPlantTaskCodeEnum.DEPOSIT.desc)) { //存款
                    //查询当日是否有存款，有3笔显示状态为明天再来 S
                    NodeReq nodeReq = new NodeReq();
                    com.xianglin.xlnodecore.common.service.facade.vo.NodeVo nodeVo = new NodeVo();
                    nodeVo.setNodeManagerPartyId(partyId);
                    nodeReq.setVo(nodeVo);
                    NodeResp nodeResp = nodeService.queryNodeInfoByNodeManagerPartyId(nodeReq);
                    if (nodeResp.getVo() != null) {
                        BankReceiptVo bankReceiptVo = new BankReceiptVo();
                        bankReceiptVo.setNodePartyId(nodeResp.getVo().getNodePartyId());
                        List<BankReceiptVo> bankReceiptVos = bankReceiptService.selectAll(bankReceiptVo);
                        bankReceiptVos = bankReceiptVos.stream().filter(v -> org.apache.commons.lang3.time.DateUtils.isSameDay(v.getSignupDate(), new Date())).collect(Collectors.toList());
                        if (bankReceiptVos.size() >= 3) {
                            actPlantTaskVo.setStatus("S");
                        } else { //没有达到3比显示状态为 I 和 存款次数
                            actPlantTaskVo.setCompleteCount(bankReceiptVos.size());
                        }
                    }
                    actPlantTaskVo.setUrl(configMapper.selectConfig("PLANT_ACCOUNT_TOOL"));
                }
                if (vo.getCode().equals(ActPlantEnum.ActPlantTaskCodeEnum.PHONE.desc) || vo.getCode().equals(ActPlantEnum.ActPlantTaskCodeEnum.SHOPPING.desc)) { //手机充值或 购物
                    //查当日是否充值或购物 充值后显示状态为明天再来 S
                    int count = queryPlantTaskDetailCountByCode(vo.getCode(), partyId);
                    if (count > 0) {
                        actPlantTaskVo.setStatus("S");
                    }

                    if (vo.getCode().equals(ActPlantEnum.ActPlantTaskCodeEnum.PHONE.desc)) {
                        actPlantTaskVo.setUrl(configMapper.selectConfig("PLANT_PHONE"));
                    }
                    if (vo.getCode().equals(ActPlantEnum.ActPlantTaskCodeEnum.SHOPPING.desc)) {
                        actPlantTaskVo.setUrl(configMapper.selectConfig("PLANT_MAI"));
                    }
                }
                if (vo.getCode().equals(ActPlantEnum.ActPlantTaskCodeEnum.STEP.desc)) {
                    //步步生金 h5自己跳转
                    Integer count = stepSharedService.queryConversionsByDate(partyId, DateUtils.formatDate(new Date(), DateUtils.DATE_TPT_TWO));
                    if (count >= 4) {
                        actPlantTaskVo.setStatus("S");
                    } else {
                        actPlantTaskVo.setCompleteCount(count);
                    }
                }
            } catch (Exception e) {
                logger.warn("actPlantTasks.stream() error", e);
            }
            return actPlantTaskVo;
        }).collect(Collectors.toList());
        return actPlantTaskVos;
    }


    /**
     * 还差多长时间领取爱心
     *
     * @param partyId
     * @param code
     * @return
     */
    private Long shareReceiveLoveTime(Long partyId, String code) {
        Example example1 = new Example(ActPlantTaskDetail.class);
        example1.and().andEqualTo("code", code).andEqualTo("partyId", partyId).andEqualTo("type", ActPlantEnum.ActPlantTaskTypeEnum.USERTASK.name()).andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name());
        example1.orderBy("createTime").desc();
        List<ActPlantTaskDetail> actPlantTaskDetails = actPlantTaskDetailMapper.selectByExampleAndRowBounds(example1, new RowBounds(0, 1));
        if (actPlantTaskDetails.size() == 0) {
            return null;
        }
        //两个时间相差得到的秒
        long seconds = (new Date().getTime() - actPlantTaskDetails.get(0).getCreateTime().getTime()) / 1000;
        logger.info("shareReceiveLoveTime 两个时间相差得到的秒：" + seconds);
        if (seconds <= (15 * 60)) { //如果在5分钟内就显示倒计时
            return 15 * 60 - seconds;
        }
        return null;
    }

    /**
     * 根据code查询每天的任务明细个数
     *
     * @param code
     * @return
     */
    private int queryPlantTaskDetailCountByCode(String code, Long partyId) {
        Example example1 = new Example(ActPlantTaskDetail.class);
        example1.and().andEqualTo("code", code).andEqualTo("type", ActPlantEnum.ActPlantTaskTypeEnum.USERTASK.name()).andEqualTo("status", "S").andEqualTo("partyId", partyId).andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name());
        if (!code.equals(ActPlantEnum.ActPlantTaskCodeEnum.INVITE.desc)) {
            example1.and().andEqualTo("day", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        }
        return actPlantTaskDetailMapper.selectCountByExample(example1);
    }

    /**
     * 兑换信息查询
     *
     * @param actPlantLvTranPageDTO
     * @return
     */
    @Override
    public List<ActPlantLvTranDTO> queryPlantExchange(ActPlantLvTranPageDTO actPlantLvTranPageDTO) {
        try {
            Example example = new Example(ActPlantLvTran.class);
            example.and().andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name());
            if (StringUtils.isNotEmpty(actPlantLvTranPageDTO.getLikeUserName())) {
                example.and().andLike("userName", "%" + actPlantLvTranPageDTO.getLikeUserName() + "%");
            }
            if (StringUtils.isNotEmpty(actPlantLvTranPageDTO.getMoblie())) {
                example.and().andEqualTo("mobile", actPlantLvTranPageDTO.getMoblie());
            }
            if (actPlantLvTranPageDTO.getPartyId() != null) {
                example.and().andEqualTo("partyId", actPlantLvTranPageDTO.getPartyId());
            }
            if (StringUtils.isNotEmpty(actPlantLvTranPageDTO.getCode())) {
                example.and().andEqualTo("prizeCode", actPlantLvTranPageDTO.getCode());
            }
            if (StringUtils.isNotEmpty(actPlantLvTranPageDTO.getStatus())) {
                example.and().andEqualTo("status", actPlantLvTranPageDTO.getStatus());
            } else {
                example.and().andNotEqualTo("status", ActPlantEnum.StatusType.I.name());
            }
            example.and().andEqualTo("type", ActPlantEnum.TranType.EXANGE.name());
//                    .andEqualTo("status", "S");
            example.orderBy("createTime").desc();
            List<ActPlantLvTran> actPlantLvTrans = actPlantLvTranMapper.selectByExampleAndRowBounds(example, new RowBounds((actPlantLvTranPageDTO.getStartPage() - 1) * actPlantLvTranPageDTO.getPageSize(), actPlantLvTranPageDTO.getPageSize()));
            actPlantLvTrans = actPlantLvTrans.stream().map(vo -> {
                vo.setIsOrder("N");
                ActPlantPrize actPlantPrize = actPlantPrizeMapper.selectOne(ActPlantPrize.builder().code(vo.getPrizeCode()).isDeleted(Constant.Delete_Y_N.N.name()).build());
                if (!StringUtils.equals(vo.getStatus(), ActPlantEnum.StatusType.A.name())) {
                    if (StringUtils.isEmpty(vo.getRemark()) && actPlantPrize.getRewardType().equals(ActPlantEnum.ActPrizeType.GOODS.name())) { //需要填单号
                        vo.setIsOrder("Y");
                    }
                }
                return vo;
            }).collect(Collectors.toList());
            return DTOUtils.map(actPlantLvTrans, ActPlantLvTranDTO.class);
        } catch (Exception e) {
            logger.warn("queryPlantExchange warn", e);
        }
        return null;
    }

    /**
     * 更新兑换信息
     *
     * @param actPlantLvTran
     * @return
     */
    @Override
    public Boolean updatePlantExchange(ActPlantLvTran actPlantLvTran) {
        ActPlantLvTran actPlantLvTran1 = actPlantLvTranMapper.selectByPrimaryKey(actPlantLvTran.getId());
        if (actPlantLvTran1 == null) {
            logger.error("更新兑换信息失败：{}");
            throw new BizException(ActPreconditions.ResponseEnum.DELETE);
        }
        actPlantLvTran.setUpdateTime(new Date());
        Boolean flag = actPlantLvTranMapper.updateByPrimaryKeySelective(actPlantLvTran) == 1;
        if (StringUtils.isNotEmpty(actPlantLvTran.getRemark())) {
            List<Long> partyIds = new ArrayList<>(1);
            partyIds.add(actPlantLvTran1.getPartyId());
            //发推送
            Request<MsgVo> request = new Request<>();
            MsgVo msgVo = new MsgVo();
            msgVo.setMsgTitle("福利树活动奖励");
            msgVo.setIsSave(Constant.YESNO.YES);
            msgVo.setMessage("你的礼品已发货，物流单号：<a style='color:#2f96ff;' href=\"https://m.kuaidi100.com/app/query/?&nu=" + actPlantLvTran.getRemark() + "\">" + actPlantLvTran.getRemark() + "</a>，注意查收！");
            msgVo.setMsgType(Constant.MsgType.CASHBONUS_TIP.name());
            msgVo.setIsDeleted("N");
            msgVo.setExpiryTime(0);
            msgVo.setLoginCheck(Constant.YESNO.NO.code);
            msgVo.setPassCheck(Constant.YESNO.NO.code);
            msgVo.setMsgSourceUrl(Constant.MsgType.CASHBONUS_TIP.name());
            msgVo.setPartyId(actPlantLvTran.getPartyId());
            request.setReq(msgVo);
            messageService.sendMsg(request, partyIds);
        }
        return flag;
    }

    /**
     * 添加用户的明细记录手机充值、购物
     * 每天一种类型只有一条记录
     *
     * @param actPlantTaskDetail
     * @return
     */
    @Override
    public Boolean insertActPlantTaskDetail(ActPlantTaskDetail actPlantTaskDetail) {
        //查询当天是否有记录
        List<ActPlantTaskDetail> actPlantTaskDetails = actPlantTaskDetailMapper.select(ActPlantTaskDetail.builder().type(actPlantTaskDetail.getType()).partyId(actPlantTaskDetail.getPartyId()).day(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)).code(actPlantTaskDetail.getCode()).isDeleted(ActPlantEnum.DeleteTypeEnum.N.name()).build());
        if (actPlantTaskDetails.size() == 0) {
            return actPlantTaskDetailMapper.insertSelective(actPlantTaskDetail) == 1;
        }
        return false;
    }


    /**
     * 查询活动的配置
     *
     * @param actCode
     * @return
     */
    @Override
    public String selectActConfigValue(String actCode) {
        return configMapper.selectConfig(actCode);
    }

    /**
     * 查询礼品列表
     *
     * @return
     */
    @Override
    public List<ActPlantPrize> queryPrizeList() {
        Example example = new Example(ActPlantPrize.class);
        example.and().andEqualTo("isDeleted", "N");
        List<ActPlantPrize> plantPrizes = actPlantPrizeMapper.selectByExample(example).stream().filter(vo -> !(vo.getLv() == 0)).collect(Collectors.toList());
        return plantPrizes;
    }

    /**
     * 查询用户是否参与过活动
     *
     * @param partyId
     * @return
     */
    @Override
    public ActPlantVo isJoinAct(Long partyId) {
        ActPlant actPlant = actPlantMapper.findByPartyId(partyId);
        ActPlantVo actPlantVo = null;
        try {
            actPlantVo = DTOUtils.map(actPlant, ActPlantVo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return actPlantVo;
    }

    /**
     * 用户开始参与活动
     *
     * @param partyId
     * @param openId
     * @return
     */
    @Override
    public ActPlant joinAct(Long partyId, String openId) {
        ActPlant actPlant = null;
        try {
            actPlant = ActPlant.builder().partyId(partyId).qr(createQrCode(partyId)).poster(createPoster(partyId)).build();
            if (StringUtils.isNotEmpty(openId)) {
                actPlant.setOpenId(openId);
            }
            actPlantMapper.insertSelective(actPlant);

            actPlantTipMapper.insertSelective(ActPlantTip.builder().partyId(partyId).type(ActPlantEnum.TipType.TIP.name()).tip("我领了一颗树苗").build());
            return actPlant;
        } catch (Exception e) {
            logger.warn("actPlant", e);
        }
        return actPlant;
    }

    /**
     * 查询用户爱心值和用户头像
     *
     * @param partyId
     * @return
     */
    @Override
    public Map<String, Object> selectUserLvAndImg(Long partyId) {
        Map<String, Object> resultMap = Maps.newConcurrentMap();
        UserVo userVo = personalService.queryUser(partyId).getResult();
        ActPlant actPlant = actPlantMapper.findByPartyId(partyId);
        if (userVo.getHeadImg() != null) {
            resultMap.put("userHeadImg", userVo.getHeadImg());
        }
        if (actPlant.getLv() != null) {
            resultMap.put("userLv", actPlant.getLv());
        }
        return resultMap;
    }

    /**
     * 查询当前用户的能量(包括可收取，可显示)
     *
     * @param partyId
     * @return
     */
    @Override
    public List<ActPlantLvVo> showLv(Long partyId) {
        Example example1 = new Example(ActPlantLv.class);
        example1.and().andEqualTo("partyId", partyId).andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name()).andLessThanOrEqualTo("shouTime", new Date()).andEqualTo("type", ActPlantEnum.ActPlantTaskTypeEnum.USERTASK.name())
                .andGreaterThan("expireTime", new Date()).andEqualTo("status", ActPlantEnum.StatusType.I.name());

        List<ActPlantLv> actPlantLvs = actPlantLvMapper.selectByExample(example1);
        List<ActPlantLvVo> actPlantLvVos = actPlantLvs.stream().map(vo -> {
            ActPlantLvVo actPlantLvVo = null;
            try {
                actPlantLvVo = DTOUtils.map(vo, ActPlantLvVo.class);
            } catch (Exception e) {
                logger.warn("actPlantLvs", e);
            }
            return actPlantLvVo;
        }).collect(Collectors.toList());
        return actPlantLvVos;
    }

    /**
     * 收取爱心值
     *
     * @param actPlantLvVo
     * @param partyId
     * @return
     */
    @Override
    public ActPlantLvObtainVo obtainLv(ActPlantLvVo actPlantLvVo, Long partyId) {
        //爱心值最多两人同时收取,爱心值小于等于3时，好友不可偷取。
        RLock lock = redissonClient.getLock("ACT:actPlantLvVo:" + partyId);
        ActPlantLvObtainVo actPlantLvObtainVo = new ActPlantLvObtainVo();
        ActPlantLv actPlantLv = null;
        int obtLv;
        try {
            if (!lock.tryLock()) {
                throw new BizException(ActPreconditions.ResponseEnum.REPEAT);
            }

            actPlantLv = actPlantLvMapper.selectByPrimaryKey(actPlantLvVo.getId());
            if (actPlantLv.getExpireTime().before(new Date())) {
                throw new BizException(ActPreconditions.ResponseEnum.ACTlV_EXPIRE);
            }

            obtLv = actPlantLv.getLv();
            Example example = new Example(ActPlant.class);
            example.and().andEqualTo("partyId", partyId);

            List<ActPlant> actPlants = actPlantMapper.selectByExample(example);
            ActPlant actPlant = actPlants.get(0);

            if (!actPlantLv.getPartyId().equals(partyId)) {
                actPlantLvObtainVo.setObtainLv(0);
                actPlantLvObtainVo.setCurrentLv(actPlant.getLv());
                return actPlantLvObtainVo;
            }

            ActPlantLvTran query = ActPlantLvTran.builder().partyId(partyId).lvId(actPlantLvVo.getId()).build();
            query = actPlantLvTranMapper.selectOne(query);
            if (query != null) {
                actPlantLvObtainVo.setObtainLv(query.getLv());
                actPlantLvObtainVo.setCurrentLv(actPlant.getLv());
                return actPlantLvObtainVo;
            }
            //如果frendspartyId为null就是自己收取爱心值,否则好友偷取
            if (actPlants.size() == 0) {
                //如果数据库没有数据，为该用户新建一条数据并更新该用户的爱心值。
                actPlantMapper.insertSelective(ActPlant.builder()
                        .partyId(partyId).lv(actPlantLv.getLv())
                        .totalLv(actPlantLv.getLv()).build());
                //自己收取的爱心值以后，本次剩余爱心值为0
                actPlantLv.setLv(0);
                actPlantLv.setStatus(ActPlantEnum.StatusType.S.name());
                actPlantLvMapper.updateByPrimaryKeySelective(actPlantLv);

            } else {
                //该用户自有的爱心值，再加上本次收取的爱心值
                actPlant.setLv(actPlant.getLv() + actPlantLv.getLv());
                actPlant.setTotalLv(actPlant.getTotalLv() + actPlantLv.getLv());
                actPlant.setUpdateTime(new Date());
                actPlantMapper.updateByPrimaryKeySelective(actPlant);

                //自己收取的爱心值以后，本次剩余爱心值为0
                actPlantLv.setLv(0);
                actPlantLv.setStatus(ActPlantEnum.StatusType.S.name());
                actPlantLv.setUpdateTime(new Date());
                actPlantLvMapper.updateByPrimaryKeySelective(actPlantLv);
            }

            //保存提示消息
            plantLevelTip(partyId);

            //同时保存爱心交易明细一条记录
            actPlantLvTranMapper.insertSelective(ActPlantLvTran.builder().partyId(partyId)
                    .lv(actPlantLv.getLv())
                    .lvId(actPlantLv.getId())
                    .type(actPlantLv.getType()).isDeleted("N").status(ActPlantEnum.StatusType.I.name()).type(ActPlantEnum.TranType.COLLECT.name()).build());

            actPlantLvObtainVo.setObtainLv(obtLv);
            actPlantLvObtainVo.setCurrentLv(actPlant.getLv());
        } catch (BizException b) {
            throw new BizException(ActPreconditions.ResponseEnum.ACTlV_EXPIRE);
        } catch (Exception e) {
            logger.warn("actPlantLv", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return actPlantLvObtainVo;
    }

    /**
     * 兑换奖品
     *
     * @param partyId
     * @param actPlantPrizeCode
     * @return
     */
    @Override
    public Long exchangePrize(Long partyId, String actPlantPrizeCode) {
        RLock lock = redissonClient.getLock("ACT:partyId:actPlantPrizeCode" + partyId);

        ActPlantLvTran actPlantLvTran = new ActPlantLvTran();

        //获取用户当前的爱心值
        ActPlant actPlant = actPlantMapper.findByPartyId(partyId);
        //获取兑换礼品需要的爱心值
        ActPlantPrize actPrizeLv = actPlantPrizeMapper.selectOne(ActPlantPrize.builder().code(actPlantPrizeCode).isDeleted(Constant.Delete_Y_N.N.name()).build());

        //如果用户当前爱心值大于兑换奖品需要带的爱心值，则进行兑换，否则提示爱心值不足
        if (actPlant.getLv() >= actPrizeLv.getLv()) {
            //如果爱心值足够兑换奖品,再判断用户是否实名认证
            if (!userCertification(partyId)) {
                logger.info("用户没有进行实名认证! userVo = {}", userCertification(partyId));
                throw new BizException(USER_NOT_CERTIFICATION);
            }
        } else {
            logger.info("用户没有足够的爱心值! UserLv()={}", actPlant.getLv());
            throw new BizException(USERLV_NOTENOGH);
        }

        try {
            if (!lock.tryLock()) {
                throw new BizException(ActPreconditions.ResponseEnum.REPEAT);
            }
            String now = DateUtils.formatDate(new Date(), DateUtils.DATE_FMT);
            String startTime = now + " 00:00:00";

            Example example = new Example(ActPlantLvTran.class);
            example.and().andEqualTo("partyId", partyId).andEqualTo("isDeleted", Constant.Delete_Y_N.N.name()).andNotEqualTo("status", ActPlantEnum.StatusType.I.name())
                    .andEqualTo("type", ActPlantEnum.TranType.EXANGE.name()).andGreaterThanOrEqualTo("createTime", startTime);
            List<ActPlantLvTran> actPlantLvTrans = actPlantLvTranMapper.selectByExample(example);
            if (actPlantLvTrans.size() >= 3) {
                throw new BizException(ACT_PLANT_EXCHANGE_LIMIT);
            } else {
                actPlantLvTrans.forEach(v -> {
                    if (actPlantPrizeCode.equals(v.getPrizeCode())) {
                        throw new BizException(ACT_PLANT_EXCHANGE_LIMIT);
                    }
                });
            }

            if (!actPrizeLv.getRewardType().equals(ActPlantEnum.ActPrizeType.GOODS.name())) {
                //更新爱心值
                actPlant.setUpdateTime(new Date());
                actPlant.setLv(actPlant.getLv() - actPrizeLv.getLv());
                actPlantMapper.updateByPrimaryKeySelective(actPlant);
            }

            UserVo userVo = personalService.queryUser(partyId).getResult();

            String str = null;

            if (actPrizeLv.getRewardType().equals(ActPlantEnum.ActPrizeType.GOODS.name())) {
                str = ActPlantEnum.StatusType.I.name();
            } else {
                str = ActPlantEnum.StatusType.A.name();
            }

            actPlantLvTran = ActPlantLvTran.builder().partyId(partyId).name(actPrizeLv.getName())
                    .prizeCode(actPlantPrizeCode)
                    .type(ActPlantEnum.TranType.EXANGE.name())
                    .userName(userVo.getTrueName())
                    .mobile(userVo.getLoginName())
                    .isDeleted("N")
                    .status(str).build();
            actPlantLvTranMapper.insertSelective(actPlantLvTran);

            if (!actPrizeLv.getRewardType().equals(ActPlantEnum.ActPrizeType.VOUCHERS.name())) {
                String status = ActPlantEnum.StatusType.S.name();
                if (actPrizeLv.getRewardType().equals(ActPlantEnum.ActPrizeType.GOODS.name())) {
                    status = ActPlantEnum.StatusType.I.name();
                }
                updateExchangeStatus(actPlantLvTran.getId(), status);
            }

        } catch (BizException b) {
            logger.warn("exchangePrize", b);
            if (b.getResponseEnum().code.equals(ACT_PLANT_EXCHANGE_LIMIT.code)) {
                throw new BizException(ACT_PLANT_EXCHANGE_LIMIT);
            }
        } catch (Exception e) {
            logger.warn("exchangePrize", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return actPlantLvTran.getId();
    }

    /**
     * 查询用户是否实名认证
     *
     * @param partyId
     * @return
     */
    @Override
    public Boolean userCertification(Long partyId) {
        com.xianglin.cif.common.service.facade.model.Response<CustomersDTO> resp2 = customersInfoService.selectCustomsAlready2Auth(partyId);
        logger.info("selectCustomsAlready2Auth info", resp2.toString());
        Boolean isAuth = false;
        if (resp2.getResult() != null) {
            if (StringUtils.isNotEmpty(resp2.getResult().getAuthLevel())) {
                isAuth = true;
            }
        }
        return isAuth;
    }

    /**
     * 提交地址信息
     *
     * @return
     */
    @Override
    public Long addressCommit(ActPlantLvTranVo actPlantLvTranVo) {
        RLock lock = redissonClient.getLock("ACT:PLANT:TRAN:partyId" + actPlantLvTranVo.getPartyId());
        ActPlantLvTran actPlantLvTran = actPlantLvTranMapper.selectByPrimaryKey(actPlantLvTranVo.getId());
        try {

            if (!lock.tryLock()) {
                throw new BizException(ActPreconditions.ResponseEnum.REPEAT);
            }

            //查询用户爱心值信息
            ActPlant actPlant = actPlantMapper.findByPartyId(actPlantLvTranVo.getPartyId());
            //获取奖品信息
            ActPlantPrize actPlantPrize = ActPlantPrize.builder().code(actPlantLvTranVo.getPrizeCode()).isDeleted(Constant.Delete_Y_N.N.name()).build();
            actPlantPrize = actPlantPrizeMapper.selectOne(actPlantPrize);

            if (actPlantPrize.getRewardType().equals(ActPlantEnum.ActPrizeType.GOODS.name())) {
                //更新爱心值
                actPlant.setUpdateTime(new Date());
                actPlant.setLv(actPlant.getLv() - actPlantPrize.getLv());
                actPlantMapper.updateByPrimaryKeySelective(actPlant);
            }

            //更新爱心交易明细
            actPlantLvTran.setStatus(ActPlantEnum.StatusType.S.name());
            actPlantLvTran.setMobile(actPlantLvTranVo.getMobile());
            actPlantLvTran.setAddress(actPlantLvTranVo.getAddress());
            actPlantLvTran.setUpdateTime(new Date());
            actPlantLvTran.setUserName(actPlantLvTranVo.getUserName());
            actPlantLvTranMapper.updateByPrimaryKeySelective(actPlantLvTran);
        } catch (BizException b) {
            if (b.getResponseEnum().code.equals(REPEAT.code)) {
                throw new BizException(REPEAT);
            }
            if (b.getResponseEnum().code.equals(USERLV_NOTENOGH.code)) {
                throw new BizException(USERLV_NOTENOGH);
            }
        } catch (Exception e) {
            logger.warn("actPlantLvTranVo", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return actPlantLvTran.getId();
    }

    /**
     * 根据手机号查用户
     *
     * @param phone
     * @return
     */
    @Override
    public UserVo queryUserByPhone(String phone) {
        Response<UserVo> userVoResponse = personalService.queryUserByPhone(phone);
        if (userVoResponse.getResult() != null) {
            return userVoResponse.getResult();
        }
        return null;
    }

    /**
     * 新用户领取树苗的状态
     *
     * @param openId
     * @return
     */
    @Override
    public Map<String, Object> queryNewUserReciveState(String openId, Long partyId) {
        if (StringUtils.isBlank(openId)
                && partyId == null) {
            logger.error("openId and mobile phone is null!");
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(openId)) {
            map.put("openId", openId);
        } else {
            map.put("partyId", partyId);
        }
        Example example = new Example(ActPlant.class);
        example.and().andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name());
        if (StringUtils.isNotEmpty(openId)) {
            example.and().andEqualTo("openId", openId);
        } else {
            example.and().andEqualTo("partyId", partyId);
        }
        example.orderBy("createTime").desc();
        List<ActPlant> actPlants = actPlantMapper.selectByExample(example);
        if (actPlants.size() > 0) {
            String reciveDay = DateUtils.formatDate(actPlants.get(0).getCreateTime(), "yyyyMMdd");
            String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            //String yesterday = LocalDate.now().plus(-1, ChronoUnit.DAYS).format(DateTimeFormatter.BASIC_ISO_DATE);
            //如果领取树苗的时间为今天，状态为明天领取
            if (reciveDay.equals(today)) {
                map.put("status", "yesterday");
            } else {
                map.put("status", "today");
            }


            //查询可领取克数
            List<ActPlantLv> actPlantLvs = actPlantLvMapper.select(ActPlantLv.builder().isDeleted(ActPlantEnum.DeleteTypeEnum.N.name()).type(ActPlantEnum.ActPlantTaskCodeEnum.REGISTER.name()).partyId(actPlants.get(0).getPartyId()).build());
            //查询是否注册了app
            Response<UserVo> userVoResponse = personalService.queryUser(actPlants.get(0).getPartyId());
            if (userVoResponse.getResult() != null) {
                actPlantLvs = actPlantLvs.stream().map(vo -> {
                    vo.setStatus("S");
                    return vo;
                }).collect(Collectors.toList());
            }
            map.put("actPlantLvs", actPlantLvs);

        }

        return map;
    }


    /**
     * 新用户注册，并领取树苗和爱心值奖励
     *
     * @param plantRegisterVo
     * @return
     */
    @Override
    public Long register(PlantRegisterVo plantRegisterVo) {
        RLock lock = redissonClient.getLock("ACT PLANT:register:" + plantRegisterVo);
        try {
            if (!lock.tryLock()) {
                throw new BizException(REPEAT);
            }
            String openId = null;
            if (StringUtils.isBlank(plantRegisterVo.getMobilePhone())) {
                logger.error("mobile phone is null!");
                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
            }

            //判断验证码是否正确
            SmsResponse smsResponse = messageServiceClient.checkSmsCode(plantRegisterVo.getMobilePhone(), plantRegisterVo.getCode(), Boolean.TRUE);
            if (!(XLStationEnums.ResultSuccess.getCode() == smsResponse.getBussinessCode())) {
                throw new BizException(ActPreconditions.ResponseEnum.CHECK_MESSAGE_FAIL);
            }
            // 开户
            CustomersDTO customersDTO = new CustomersDTO();
            customersDTO.setMobilePhone(plantRegisterVo.getMobilePhone());
            customersDTO.setCreator(plantRegisterVo.getMobilePhone());
            customersDTO.setInvitationPartyId(plantRegisterVo.getFromPartyId());
            com.xianglin.cif.common.service.facade.model.Response<CustomersDTO> openAccountResponse =
                    customersInfoServiceClient.openAccount(customersDTO, Constants.SYSTEM_NAME);
            if (!openAccountResponse.isSuccess()) {
                logger.error("用户开户失败: {}", JSON.toJSONString(openAccountResponse));
                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
            }


            //微信通过openId查是否领取过树苗
            if (StringUtils.isNotEmpty(plantRegisterVo.getOpenId())) {
                List<ActPlant> actPlants = actPlantMapper.select(ActPlant.builder().openId(plantRegisterVo.getOpenId()).build());
                if (actPlants.size() > 0) {
                    logger.error("用户开户失败: {}", JSON.toJSONString(actPlants));
                    throw new BizException(ActPreconditions.ResponseEnum.OPENID_EXIST);
                }
                openId = plantRegisterVo.getOpenId();
            }

            //同步用户的partyID到plant表 送爱心值和小树苗给用户  在明细记录记录添加邀请记录

            //领取树苗

            ActPlant actPlant = joinAct(openAccountResponse.getResult().getPartyId(), openId);

            //添加用户注册的爱心值记录 4次
            for (int i = 0; i < 4; i++) {
                actPlantLvMapper.insertSelective(ActPlantLv.builder().lv(5).totalLv(5).partyId(openAccountResponse.getResult().getPartyId()).status("I").matureTime(new Date()).expireTime(DateUtils.skipDateTime(new Date(), 365)).shouTime(new Date()).type(ActPlantEnum.ActPlantTaskTypeEnum.REGISTER.name()).build());
            }

            //添加邀请用户的邀请记录
            ActPlantTaskDetail actPlantTaskDetail = ActPlantTaskDetail.builder().partyId(plantRegisterVo.getFromPartyId()).code(ActPlantEnum.ActPlantTaskCodeEnum.INVITE.desc).day(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)).type(ActPlantEnum.ActPlantTaskTypeEnum.USERTASK.name()).refId(actPlant.getPartyId() + "").status("I").build();
            actPlantTaskDetailMapper.insertSelective(actPlantTaskDetail);


            return openAccountResponse.getResult().getPartyId();
        } finally {
            lock.unlock();
        }
    }


    /**
     * 查询用户信息
     *
     * @param partyId
     * @return
     */
    @Override
    public UserVo selectUserInfo(Long partyId) {
        return personalService.queryUser(partyId).getResult();
    }

    @Override
    public ActPlant queryPlant(Long partyId) {
        ActPlant plant = actPlantMapper.findByPartyId(partyId);
        if (StringUtils.isEmpty(plant.getQr())) {
            plant.setQr(createQrCode(partyId));
        }
        return plant;
    }

    @Override
    public List<ActPlantFollow> queryFollows(Long partyId) {
        final AtomicInteger rank = new AtomicInteger(0);
        List<CompletableFuture<ActPlantFollow>> list = userRelationServiceClient.getUserFellowList(partyId).stream()
                .map(vo -> CompletableFuture.supplyAsync(() -> {
                    final ActPlantFollow follow = ActPlantFollow.builder().partyId(vo.getPartyId()).headImage(vo.getHeadImg()).name(vo.getShowName()).build();

                    //查询兑换次数
                    ActPlant actPlant = ActPlant.builder().partyId(follow.getPartyId()).isDeleted("N").build();
                    actPlant = actPlantMapper.selectOne(actPlant);
                    if (actPlant == null) {
                        follow.setHasPlant(false);
                        follow.setLv(-1);
                    } else {
                        follow.setHasPlant(true);
                        follow.setLv(actPlant.getLv());
                    }
                    //查询兑换次数
                    Example example1 = new Example(ActPlantLvTran.class);
                    example1.and().andEqualTo("isDeleted", "N").andEqualTo("partyId", follow.getPartyId()).andEqualTo("type", ActPlantEnum.TranType.EXANGE.name()).andEqualTo("status", ActPlantEnum.StatusType.S.name());
                    follow.setExchangeNum(actPlantLvTranMapper.selectCountByExample(example1));
                    //查询最近能量
                    example1 = new Example(ActPlantLv.class);
                    example1.and().andEqualTo("partyId", follow.getPartyId()).andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name()).andGreaterThan("expireTime", new Date())
                            .andLessThan("shouTime", new Date()).andEqualTo("status", ActPlantEnum.StatusType.I.name());
                    example1.orderBy("matureTime").asc();

                    actPlantLvMapper.selectByExample(example1).stream().filter(v -> {
                        //去掉已经偷取过的能量
                        return actPlantLvTranMapper.selectCount(ActPlantLvTran.builder().partyId(partyId).lvId(v.getId()).build()) == 0;
                    }).filter(v -> {
                        return TimeUnit.MILLISECONDS.toSeconds(v.getMatureTime().getTime() - System.currentTimeMillis()) <= 3600;
                        //每个爱心最多被偷2克
                    }).filter(v -> v.getTotalLv() - v.getLv() < 2)
                            .findFirst().ifPresent(v -> {
                        long limit = TimeUnit.MILLISECONDS.toSeconds(v.getMatureTime().getTime() - System.currentTimeMillis());
                        follow.setRecentLv(limit > 0 ? limit : 0L);
                    });

                    //查询是否邀请
                    example1 = new Example(ActPlantTip.class);
                    example1.and().andEqualTo("partyId", partyId).andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name()).andEqualTo("type", ActPlantEnum.TipType.INVITE.name())
                            .andEqualTo("tip", follow.getPartyId());
                    follow.setHasInvite(actPlantTipMapper.selectCountByExample(example1) > 0);
                    return follow;
                }, executor))
                .collect(Collectors.toList());

        return list.stream().map(CompletableFuture::join)
                .sorted((v1, v2) ->
                        v2.getLv() - v1.getLv()
                ).peek(vo -> {
                    vo.setRank(rank.addAndGet(1));
                }).collect(Collectors.toList());

    }

    @Override
    public Boolean followInvite(Long currentPartyId, Long followPartyId) {
        ActPlantTip tip = ActPlantTip.builder().partyId(currentPartyId).type(ActPlantEnum.TipType.INVITE.name()).tip(followPartyId + "").build();
        int result = actPlantTipMapper.selectCount(tip);
        if (result == 0) {
            actPlantTipMapper.insertSelective(tip);
        }
        return Boolean.TRUE;
    }

    @Override
    public List<ActPlantLvVo> queryPlantLvs(Long currentPartyId, Long followPartyId) {
        Example example1 = new Example(ActPlantLv.class);
        example1.and().andEqualTo("partyId", followPartyId).andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name()).andLessThan("shouTime", new Date()).andEqualTo("type", ActPlantEnum.ActPlantTaskTypeEnum.USERTASK.name())
                .andGreaterThan("expireTime", new Date()).andEqualTo("status", ActPlantEnum.StatusType.I.name());

        return actPlantLvMapper.selectByExample(example1).stream().map(v -> {
            ActPlantLvVo vo = ActPlantLvVo.builder().id(v.getId()).lv(v.getLv()).expireTime(v.getExpireTime())
                    .shouTime(v.getShouTime()).matureTime(v.getMatureTime()).partyId(v.getPartyId()).build();
            if (TimeUnit.MILLISECONDS.toSeconds(vo.getMatureTime().getTime() - System.currentTimeMillis()) > 0) {
                vo.setRencentTime(TimeUnit.MILLISECONDS.toSeconds(vo.getMatureTime().getTime() - System.currentTimeMillis()));
            } else {
                vo.setRencentTime(0L);
            }

            boolean canColect = false;
            //调整计算方式
            if (vo.getRencentTime() == 0 && v.getTotalLv() - v.getLv() < 2) {
                Example e = new Example(ActPlantLvTran.class);
                e.and().andEqualTo("partyId", currentPartyId).andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name())
                        .andEqualTo("lvId", vo.getId());
                canColect = (actPlantLvTranMapper.selectCountByExample(e) == 0);
            }
            vo.setCanCollect(canColect);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 生成海报二维码
     *
     * @param partyId
     * @return
     */
    private String createQrCode(Long partyId) {
        String url = null;
        try {
            wxApiUtils2.setRedirectUrl(configMapper.selectConfig("PLANT_USER_QR_URL"));
            url = QRUtils.qrCreate(wxApiUtils2.getAuthUrl(partyId));
            logger.info("二维码：{}", url);
        } catch (Exception e) {
            logger.warn("createQrCode", e);
        }
        return url;
    }

    /**
     * 生成海报
     *
     * @param partyId
     * @return
     */
    private String createPoster(Long partyId) {
        String url = null;
        try {
            url = Html2ImageUtil.createImage(configMapper.selectConfig("PLANT_USER_POSTER_URL") + "?partyId=" + partyId);
        } catch (Exception e) {
            logger.warn("createQrCode", e);
        }
        return url;
    }

    /**
     * 查询单个爱心值信息
     *
     * @param actPlantLvId 爱心值表主键
     * @return 爱心值信息
     */
    @Override
    public ActPlantLvVo selectActPlantLv(Long actPlantLvId) {
        ActPlantLv actPlantLv = actPlantLvMapper.selectByPrimaryKey(actPlantLvId);
        ActPlantLvVo actPlantLvVo = null;
        try {
            actPlantLvVo = DTOUtils.map(actPlantLv, ActPlantLvVo.class);
        } catch (Exception e) {
            logger.warn("actPlantLv", e);
        }
        return actPlantLvVo;
    }

    @Override
    public Integer collectFollowLv(Long currentPartyId, Long lvId) {
        int result = 0;
        ActPlantLv lv = actPlantLvMapper.selectByPrimaryKey(lvId);
        ActPlantLvTran query = ActPlantLvTran.builder().partyId(currentPartyId).lvId(lvId).build();
        query = actPlantLvTranMapper.selectOne(query);
        if (query != null) {
            return query.getLv();
        }
        //判断偷取上限
        int dayLimit = Integer.valueOf(configMapper.selectConfig("PLANT_COLLECT_LIMIT_DAY"));
        Example limitQuery = new Example(ActPlantLvTran.class);
        limitQuery.and().andEqualTo("partyId", currentPartyId)
                .andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name())
                .andEqualTo("type", ActPlantEnum.TranType.COLLECT.name())
                .andGreaterThan("createTime", DateTime.now().withHourOfDay(0).withMillisOfDay(0).withSecondOfMinute(1).toDate());
        int count = actPlantLvTranMapper.selectCountByExample(limitQuery);
        if (count >= dayLimit) {
            throw new BizException("今天已偷取了太多爱心值，明天再来吧！");
        }

        int hourLimit = Integer.valueOf(configMapper.selectConfig("PLANT_COLLECT_LIMIT_HOUR"));
        limitQuery.and().andGreaterThan("createTime", DateTime.now().minusHours(1).toDate());
        count = actPlantLvTranMapper.selectCountByExample(limitQuery);
        if (count >= hourLimit) {
            throw new BizException("你当前时间段偷取已达上限，请下个时间段再来！");
        }

        //修改可偷取值计算方式
        int restVal = lv.getLv() - lv.getTotalLv() + 2;
        if (restVal > 0) {
            result = ThreadLocalRandom.current().nextInt(1, restVal + 1);
            ActPlantLvTran tran = ActPlantLvTran.builder().lvId(lv.getId()).lv(result).partyId(currentPartyId)
                    .type(ActPlantEnum.TranType.COLLECT.name()).status(ActPlantEnum.StatusType.S.name()).build();
            actPlantLvTranMapper.insertSelective(tran);

            ActPlantLv updateLv = ActPlantLv.builder().id(lv.getId()).lv(lv.getLv() - result).updateTime(new Date()).build();
            actPlantLvMapper.updateByPrimaryKeySelective(updateLv);

            UserVo user = personalService.queryUser(currentPartyId).getResult();
            ActPlantTip tip = ActPlantTip.builder().partyId(lv.getPartyId()).type(ActPlantEnum.TipType.TIP.name()).tip(String.format("<b>%s</b>偷了你的爱心%dg", user.getShowName(), result)).build();
            actPlantTipMapper.insertSelective(tip);

            ActPlant plant = actPlantMapper.findByPartyId(currentPartyId);
            plant.setLv(plant.getLv() + tran.getLv());
            plant.setTotalLv(plant.getTotalLv() + tran.getLv());
            plant.setUpdateTime(new Date());
            actPlantMapper.updateByPrimaryKeySelective(plant);
            //生成提示信息
            plantLevelTip(currentPartyId);
        }
        return result;
    }

    @Override
    public Boolean sendMsg(String phone) {
        com.xianglin.xlStation.base.model.Response response =
                messageServiceClient.sendSmsCode(
                        phone,
                        MESSAGE_CONTENT,
                        String.valueOf(60 * 30));

        if (!(XLStationEnums.ResultSuccess.getCode() == response.getBussinessCode())) {
            logger.error("验证码发送失败：{}", JSON.toJSONString(response));
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
        return true;
    }


    public static void main(String[] args) {
        int i = 0;
        do {
            System.out.println(ThreadLocalRandom.current().nextInt(3));
        } while (i++ < 100);
    }

    @Override
    public String findActCode() {
        String actCode = "NORMAL";
        String backgroundImgTime = configMapper.selectConfig("ACT_PLANT_BACKGROUND_IMG_TIME");
        List<Map> resultList = JSONArray.parseArray(backgroundImgTime, Map.class);
        for (Map<String, String> result : resultList) {
            Date startTime = DateUtils.parse(DateUtils.DATE_FMT, result.get("startTime"));
            Date endTime = DateUtils.parse(DateUtils.DATE_FMT, result.get("stopTime"));
            Date now = new Date();
            if (now.before(endTime) && now.after(startTime)) {
                actCode = ActPlantEnum.PlantFestivalCode.getActCode(result.get("actCode"));
            }
        }
        return actCode;
    }

    /**
     * 判断植树活动是否结束
     *
     * @return
     */
    @Override
    public Boolean activityIsStop() {
        String str = "PLANT_TREE_ACTIVITY_TIME";
        Date now = new Date();
        String configValue = configMapper.selectConfig(str);
        Map map = JSON.parseObject(configValue);
        String startValue = map.get("startTime").toString();
        String stopValue = map.get("stopTime").toString();
        Date startTime = DateUtils.formatStr(startValue, DateUtils.DATETIME_FMT);
        Date stopTime = DateUtils.formatStr(stopValue, DateUtils.DATETIME_FMT);
        if (stopTime.after(now) && startTime.before(now)) {
            return false;
        } else {
            throw new BizException(ActPreconditions.ResponseEnum.ACT_PLANT_END);
        }
    }

    /**
     * 查詢用戶的partyId和openID
     *
     * @return
     */
    @Override
    public Map<String, Object> queryUserOpenId(String code, Long partyId) {
        try {
            Map<String, Object> map = new HashMap<>();
            //根据code查openid
            String openId = wxApiUtils2.getOpenId(code);
            if (openId == null) {
                String authUrl = wxApiUtils2.getAuthUrl(partyId);
                map.put("authUrl", authUrl);
                logger.warn("未拿到openId,重新跳转: {}", authUrl);
                throw new BizException(ActPreconditions.ResponseEnum.RP_WX_REDIRECT, map);
            }
            map.put("openId", openId);
            //根据openid查询partyId
            List<ActPlant> actPlants = actPlantMapper.select(ActPlant.builder().openId(openId).build());
            if (actPlants.size() > 0) {
                map.put("partyId", actPlants.get(0).getPartyId());
            } else {
                map.put("partyId", null);
            }
            return map;
        } catch (UnsupportedEncodingException e) {
            logger.warn("queryUserOpenId" + e);
        }
        return null;
    }

    /**
     * 根据参数查询兑换数
     *
     * @param actPlantLvTranPageDTO
     * @return
     */
    @Override
    public int queryPlantExchangeCount(ActPlantLvTranPageDTO actPlantLvTranPageDTO) {
        Example example = new Example(ActPlantLvTran.class);
        example.and().andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name());
        if (StringUtils.isNotEmpty(actPlantLvTranPageDTO.getLikeUserName())) {
            example.and().andLike("userName", "%" + actPlantLvTranPageDTO.getLikeUserName() + "%");
        }
        if (StringUtils.isNotEmpty(actPlantLvTranPageDTO.getMoblie())) {
            example.and().andEqualTo("mobile", actPlantLvTranPageDTO.getMoblie());
        }
        if (actPlantLvTranPageDTO.getPartyId() != null) {
            example.and().andEqualTo("partyId", actPlantLvTranPageDTO.getPartyId());
        }
        if (StringUtils.isNotEmpty(actPlantLvTranPageDTO.getCode())) {
            example.and().andEqualTo("prizeCode", actPlantLvTranPageDTO.getCode());
        }
        if (StringUtils.isNotEmpty(actPlantLvTranPageDTO.getStatus())) {
            example.and().andEqualTo("status", actPlantLvTranPageDTO.getStatus());
        } else {
            example.and().andNotEqualTo("status", ActPlantEnum.StatusType.I.name());
        }
        example.and().andEqualTo("type", ActPlantEnum.TranType.EXANGE.name());
//        example.and().andEqualTo("type", ActPlantEnum.TranType.EXANGE.name()).andEqualTo("status", "S");
        return actPlantLvTranMapper.selectCountByExample(example);
    }

    /**
     * 用户弹窗信息
     *
     * @param partyId
     * @return
     */
    @Override
    public List<ActPlantTipVo> findByPartyId(Long partyId) {
        Example example = new Example(ActPlantTip.class);
        example.and().andEqualTo("partyId", partyId).andEqualTo("status", "I");
        List<ActPlantTip> actPlantTips = actPlantTipMapper.selectByExample(example);
        List<ActPlantTipVo> actPlantTipVos = null;
        try {
            actPlantTipVos = DTOUtils.map(actPlantTips, ActPlantTipVo.class);
        } catch (Exception e) {
            logger.warn("actPlantTips", e);
        }
        return actPlantTipVos;
    }

    /**
     * 更新用户弹窗信息
     *
     * @param partyId
     * @return
     */
    @Override
    public void updateByPartyId(Long partyId) {
        actPlantTipMapper.updateByPartyId(partyId);
    }


    @Override
    public ActPlantHomePageVo userHomePageInfo(Long partyId) {
        ActPlantHomePageVo actPlantHomePageVo = new ActPlantHomePageVo();

        //用户是否参与活动
        ActPlantVo actPlantVo = null;
        //用户信息
        UserVo userVo = null;
        //用户弹窗信息
        List<ActPlantTipVo> actPlantTipVos = null;
        //查询用户可以展示的所有爱心值
        List<ActPlantLvVo> actPlantLvVos = null;
        try {
            actPlantLvVos = showLv(partyId);
            if (actPlantLvVos.size() > 0) {
                actPlantLvVos.forEach(actPlantLvVo -> {
                    Long time = (actPlantLvVo.getMatureTime().getTime() - System.currentTimeMillis()) / 1000;
                    if (time > 0) {
                        actPlantLvVo.setCanCollect(false);
                        actPlantLvVo.setRencentTime(time);
                    } else {
                        actPlantLvVo.setCanCollect(true);
                        actPlantLvVo.setRencentTime(0L);
                    }
                });
            }
            actPlantVo = isJoinAct(partyId);
            userVo = personalService.queryUser(partyId).getResult();
            actPlantTipVos = findByPartyId(partyId);
            //如果弹窗信息大于0，则弹窗，继续将所有的弹窗信息更改为已使用状态 STATUS = S
            if (actPlantTipVos.size() > 0) {
                actPlantHomePageVo.setActOfTips(true);
                updateByPartyId(partyId);
                redissonClient.getSet("ACT:PLANT:TIP:REWARD:partyId").add(partyId);
            } else {
                actPlantHomePageVo.setActOfTips(false);
            }
            //返回活动的code
            String actCode = findActCode();
            actPlantHomePageVo.setActCode(actCode);
            //actplantvo 为空则该用户没有参与活动
            if (actPlantVo == null) {
                actPlantHomePageVo.setUserJoin(false);
                actPlantHomePageVo.setUserPoster(null);
                actPlantHomePageVo.setUserLv(0);
            } else {
                actPlantHomePageVo.setUserLv(actPlantVo.getLv());
                actPlantHomePageVo.setUserJoin(true);
                if (actPlantVo.getPoster() != null) {
                    actPlantHomePageVo.setUserPoster(actPlantVo.getPoster());
                } else {
                    actPlantHomePageVo.setUserPoster(null);
                }
            }

            if (actPlantLvVos.size() > 0) {
                actPlantHomePageVo.setActPlantLvVos(actPlantLvVos);
            } else {
                actPlantHomePageVo.setActPlantLvVos(new ArrayList<>());
            }

            if (StringUtils.isNotEmpty(userVo.getHeadImg())) {
                actPlantHomePageVo.setUserHeadImg(userVo.getHeadImg());
            } else {
                String defaultImg = configMapper.selectConfig("default_user_headimg");
                actPlantHomePageVo.setUserHeadImg(defaultImg);
            }
            //查询轮播消息
            List<ActPlantNotice> actPlantNoticeList = queryActPlantNotices(null, true);
            List<ActPlantNoticeVo> actPlantNoticeVoList = DTOUtils.map(actPlantNoticeList, ActPlantNoticeVo.class);
            actPlantHomePageVo.setActPlantNoticeVoList(actPlantNoticeVoList);
            actPlantHomePageVo.setIsDisplay(configMapper.selectConfig("STEP_DISPLAY"));
        } catch (Exception e) {
            logger.warn("userHomePageInfo error", e);
        }
        return actPlantHomePageVo;
    }


    /**
     * 分享爱心
     *
     * @param partyId
     * @return
     */
    @Override
    public Boolean shareLv(Long partyId) {
        Example example = new Example(ActPlantTaskDetail.class);
        example.and().andEqualTo("partyId", partyId).andEqualTo("code", ActPlantEnum.ActPlantTaskCodeEnum.SHARE_RECEIVE_LOVE.desc).andEqualTo("day", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)).andEqualTo("type", ActPlantEnum.ActPlantTaskTypeEnum.USERTASK.name()).andEqualTo("status", "S");
        example.orderBy("createTime").desc();
        List<ActPlantTaskDetail> select = actPlantTaskDetailMapper.selectByExample(example);
        if (select.size() == 0) {
            return insertShareLv(partyId);
        }
        //查最后一次分享的时间是否大于15分钟,只能分享4次
        //两个时间相差得到的秒
        if (select.size() < 4) {
            long seconds = (new Date().getTime() - select.get(0).getCreateTime().getTime()) / 1000;
            if (seconds >= 15 * 60) {
                return insertShareLv(partyId);
            }
        }


        return false;
    }

    private Boolean insertShareLv(Long partyId) {
        ActPlantTaskDetail actPlantTaskDetail = ActPlantTaskDetail.builder().partyId(partyId).code(ActPlantEnum.ActPlantTaskCodeEnum.SHARE_RECEIVE_LOVE.desc).day(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)).type(ActPlantEnum.ActPlantTaskTypeEnum.USERTASK.name()).status("S").build();
        actPlantTaskDetailMapper.insertSelective(actPlantTaskDetail);
        return actPlantLvMapper.insertSelective(ActPlantLv.builder().partyId(partyId).status("I").type(ActPlantEnum.ActPlantTaskTypeEnum.USERTASK.name()).lv(5).totalLv(5).matureTime(DateTime.now().plusSeconds(-30).toDate()).expireTime(DateTime.now().plusHours(12).toDate()).shouTime(DateTime.now().plusSeconds(-30).toDate()).taskId(actPlantTaskDetail.getId()).build()) == 1;
    }

    /**
     * 判断用户是否注册App或领取了树苗
     *
     * @param phone
     * @return
     */
    @Override
    public Map<String, Boolean> isRegisterOrisReceiveTree(String phone) {
        Map<String, Boolean> map = new HashMap<>();
        map.put("isReceiveTree", false);
        map.put("isRegister", false);
        Response<UserVo> userVoResponse = personalService.queryUserByPhone(phone);
        if (userVoResponse.getResult() != null) {
            map.put("isRegister", true);
            return map;
        }
        //查是否领取了树苗
        com.xianglin.cif.common.service.facade.model.Response<CustomersDTO> customersDTOResponse = customersInfoServiceClient.selectByMobilePhone(phone);
        if (customersDTOResponse.getResult() != null) {
            //根据cif的partyID查询是否领取了树苗
            ActPlant byPartyId = actPlantMapper.findByPartyId(customersDTOResponse.getResult().getPartyId());
            if (byPartyId != null) {
                map.put("isReceiveTree", true);
            }
        }

        return map;
    }

    /**
     * 生成小树又长大了的提示
     *
     * @param partyId
     */
    public void plantLevelTip(Long partyId) {
        try {
            ActPlant plant = actPlantMapper.selectOne(ActPlant.builder().partyId(partyId).build());
            long tipCount = JSON.parseArray(configMapper.selectConfig("PLANT_TIP_LEVEL"), Integer.class).stream().filter(v -> v <= plant.getLv()).count();
            int count = actPlantTipMapper.selectCount(ActPlantTip.builder().partyId(partyId).type(ActPlantEnum.TipType.LEVEL.name()).isDeleted(Constant.Delete_Y_N.N.name()).build());
            while (count++ < tipCount) {
                actPlantTipMapper.insertSelective(ActPlantTip.builder().partyId(partyId).type(ActPlantEnum.TipType.LEVEL.name())
                        .status(ActPlantEnum.StatusType.I.name()).tip("我的树又长大了一些").build());
            }
        } catch (Exception e) {
            logger.warn("", e);
        }
    }

    /**
     * 同步昨日签到
     *
     * @param shouHour
     * @param partyId
     */
    private void addDailyLv(int shouHour, Long partyId) {
        DateTime shouTime = DateTime.now().withHourOfDay(shouHour).withMinuteOfHour(0).withSecondOfMinute(1);
        DateTime matureTime = shouTime;
        DateTime expireTime = shouTime.plusHours(1);
        int defaultLv = Integer.valueOf(configMapper.selectConfig("PLANT_LV_DEF"));
        ActPlantLv lv = ActPlantLv.builder().partyId(partyId).lv(defaultLv).totalLv(defaultLv).type(ActPlantEnum.ActPlantTaskTypeEnum.USERTASK.name())
                .shouTime(shouTime.toDate()).matureTime(matureTime.toDate()).expireTime(expireTime.toDate()).status(ActPlantEnum.StatusType.I.name()).build();
        actPlantLvMapper.insertSelective(lv);

    }


    /**
     * 同步昨日签到和记账
     *
     * @param partyId
     */
    private void synSignUpAndDeposit(Long partyId) {
        // 打卡
        List<ActivityPartake> list = attendanceUserStatusService.querysignUp(partyId, LocalDate.now().minusDays(1), LocalDate.now());
        list.stream().findAny().ifPresent(vo -> {
            ActPlantTaskDetail detail = ActPlantTaskDetail.builder().partyId(partyId).code(ActPlantEnum.ActPlantTaskCodeEnum.PUNCH_IN.desc).day(LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE))
                    .type(ActPlantEnum.ActPlantTaskTypeEnum.REGISTER.name()).refId(vo.getId() + "").status(ActPlantEnum.StatusType.S.name()).build();
            actPlantTaskDetailMapper.insertSelective(detail);
        });

        //存款
        NodeReq nodeReq = new NodeReq();
        com.xianglin.xlnodecore.common.service.facade.vo.NodeVo nodeVo = new NodeVo();
        nodeVo.setNodeManagerPartyId(partyId);
        nodeReq.setVo(nodeVo);
        NodeResp nodeResp = nodeService.queryNodeInfoByNodeManagerPartyId(nodeReq);
        if (nodeResp.getVo() != null) {
            BankReceiptVo bankReceiptVo = new BankReceiptVo();
            bankReceiptVo.setNodePartyId(nodeResp.getVo().getNodePartyId());
            bankReceiptService.selectAll(bankReceiptVo).stream().filter(v -> org.apache.commons.lang3.time.DateUtils.isSameDay(v.getCreateDate(), DateTime.now().minusDays(1).toDate()))
                    .limit(3).forEach(v -> {
                ActPlantTaskDetail detail = ActPlantTaskDetail.builder().partyId(partyId).code(ActPlantEnum.ActPlantTaskCodeEnum.DEPOSIT.desc).day(LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE))
                        .type(ActPlantEnum.ActPlantTaskTypeEnum.REGISTER.name()).refId(v.getId() + "").status(ActPlantEnum.StatusType.S.name()).build();
                actPlantTaskDetailMapper.insertSelective(detail);
            });
        }
    }

    /**
     * 发放每日定时爱心
     */
    @Scheduled(cron = "0 0 1  * * ? ")
    public void sendDailyLv() {
        //TODO wanglei
        RLock lock = redissonClient.getLock("ACT:sendDailyLv:task");
        try {
            logger.info("---------- begin to sendDailyLv  ");
            if (!lock.tryLock()) {
                throw new BizException(ActPreconditions.ResponseEnum.REPEAT);
            }
            String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            RSet<String> daySet = redissonClient.getSet("ACT:sendDailyLv:task:DAY");
            if (daySet.contains(today)) {
                return;
            }
            daySet.add(today);
            actPlantMapper.selectAll().parallelStream().filter(vo -> StringUtils.equals(vo.getIsDeleted(), ActPlantEnum.DeleteTypeEnum.N.name()))
                    .forEach(vo -> {
                        //1,生成每天的随机爱心
                        addDailyLv(7, vo.getPartyId());
                        addDailyLv(11, vo.getPartyId());
                        addDailyLv(18, vo.getPartyId());

                        //2,打卡 充值 乡邻购下单,存款
                        synSignUpAndDeposit(vo.getPartyId());
                        Example example1 = new Example(ActPlantTaskDetail.class);
                        example1.and().andEqualTo("partyId", vo.getPartyId()).andEqualTo("day", LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE))
                                .andIn("code", Arrays.asList(ActPlantEnum.ActPlantTaskCodeEnum.PUNCH_IN.desc, ActPlantEnum.ActPlantTaskCodeEnum.DEPOSIT.desc, ActPlantEnum.ActPlantTaskCodeEnum.PHONE.desc, ActPlantEnum.ActPlantTaskCodeEnum.SHOPPING.desc));
                        actPlantTaskDetailMapper.selectByExample(example1).stream().limit(6).forEach(v -> {
                            DateTime shouTime = DateTime.now().withHourOfDay(ThreadLocalRandom.current().nextInt(4, 10)).withMinuteOfHour(ThreadLocalRandom.current().nextInt(59));
                            //1个小时后开始领取
                            DateTime matureTime = shouTime.plusHours(1);
                            //12小时后过期
                            DateTime expireTime = matureTime.plusHours(12);
                            int defaultLv = Integer.valueOf(configMapper.selectConfig("PLANT_LV_DEF"));
                            ActPlantLv lv = ActPlantLv.builder().partyId(v.getPartyId()).lv(defaultLv).totalLv(defaultLv).taskId(v.getId()).type(ActPlantEnum.ActPlantTaskTypeEnum.USERTASK.name())
                                    .shouTime(shouTime.toDate()).matureTime(matureTime.toDate()).expireTime(expireTime.toDate()).status(ActPlantEnum.StatusType.I.name()).build();
                            actPlantLvMapper.insertSelective(lv);
                        });
                    });

            logger.info("---------- end to sendDailyLv  ");
        } catch (Exception e) {
            logger.warn("", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Map<String, Object> getRandomPrize(Long currentPartyId) {
        Map<String, Object> resultMap = Maps.newConcurrentMap();
        Set partyIds = redissonClient.getSet("ACT:PLANT:TIP:REWARD:partyId");
        if (partyIds.contains(currentPartyId)) {
            int random = ThreadLocalRandom.current().nextInt(3);
            ActPlantEnum.ActPlantRandom tip = null;
            switch (random) {
                case 0:
                    //2元话费券
                    EcApis.getRegisterUserAward(currentPartyId + "", EcApis.EshopType.TICKET, new BigDecimal(2), "福利树活动话费券");
                    tip = ActPlantEnum.ActPlantRandom.ACT_PLANT_2_TICKET;
                    break;
                case 1:
                    //5元优惠券
                    EcApis.getRegisterUserAward(currentPartyId + "", EcApis.EshopType.COUPON, new BigDecimal(5), "福利树活动优惠券");
                    tip = ActPlantEnum.ActPlantRandom.ACT_PLANT_5_TICKET;
                    break;
                default:
                    com.xianglin.cif.common.service.facade.model.Response<GoldcoinRecordVo> goldcoinRecordVoResponse =
                            goldcoinServiceClient.doRecord(GoldcoinRecordVo.builder()
                                    .system("act")
                                    .amount(100)
                                    .fronPartyId(Constants.GOLD_SYS_ACCOUNT)
                                    .toPartyId(currentPartyId)
                                    .remark(ActPlantEnum.ActPlantGoldType.ACT_PLANT_GOLD_TYPE.desc)
                                    .type(ActivityEnum.ACT_PLANT.name())
                                    .requestId(GoldSequenceUtil.getSequence(currentPartyId, sequenceMapper
                                            .getSequence())).build());
                    if (!com.google.common.base.Objects.equal(goldcoinRecordVoResponse.getCode(), 1000)) {
                        logger.error("添加金币失败！", com.alibaba.fastjson.JSON.toJSONString(goldcoinRecordVoResponse));
                        throw new BizException(ActPreconditions.ResponseEnum.ERROR);
                    }
                    tip = ActPlantEnum.ActPlantRandom.ACT_PLANT_100_GOLD_RANDOM;
            }
            if (tip != null) {
                Request<MsgVo> request = new Request<>();
                MsgVo msgVo = new MsgVo();
                msgVo.setMsgTitle("福利树活动随机奖励");
                msgVo.setIsSave(Constant.YESNO.YES);
                msgVo.setMessage(tip.getDesc());
                msgVo.setMsgType(Constant.MsgType.CASHBONUS_TIP.name());
                msgVo.setIsDeleted("N");
                msgVo.setExpiryTime(0);
                msgVo.setLoginCheck(Constant.YESNO.NO.code);
                msgVo.setPassCheck(Constant.YESNO.NO.code);
                msgVo.setMsgSourceUrl(Constant.MsgType.CASHBONUS_TIP.name());
                msgVo.setPartyId(currentPartyId);
                request.setReq(msgVo);
                messageService.sendMsg(request, Arrays.asList(currentPartyId));
            }
            resultMap.put("randomPrize", tip.getCode());
            partyIds.remove(currentPartyId);
        }
        return resultMap;

    }

    @Override
    public String queryQrCode(Long partyId) {
        return createQrCode(partyId);
    }

    @Override
    public void sendRankMsg() {
        RLock lock = redissonClient.getLock("ACT:sendRanking:task");
        try {
            logger.info("---------- begin to sendDailyLv  ");
            if (!lock.tryLock()) {
                throw new BizException(ActPreconditions.ResponseEnum.REPEAT);
            }
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            RSet<String> daySet = redissonClient.getSet("ACT:sendDailyLv:task:DAY");
            if (daySet.contains(today)) {
                return;
            }
            AtomicInteger rankInt = new AtomicInteger(0);
            Arrays.stream(configMapper.selectConfig("PLANT_RANK_USER").split(",")).forEach(v -> {
                daySet.add(today);
                Long partyId = Long.valueOf(v);
                int rank = rankInt.addAndGet(1);
                String message = "恭喜你，在乡邻福利树活动中，获得第%s名奖励%s元，去金币明细中查看，感谢你的参与！";
                String sms = "恭喜你，在乡邻福利树活动中，获得第%s名奖励%s元，感谢你的参与！";
                String amt = "";
                String rankStr = "";
                switch (rank) {
                    case 1:
                        rankStr = "一";
                        amt = "288";
                        break;
                    case 2:
                        rankStr = "二";
                        amt = "188";
                        break;
                    case 3:
                        rankStr = "三";
                        amt = "88";
                }
                final String fsms = String.format(sms, rankStr, amt);
                final String fmessage = String.format(message, rankStr, amt);
                ;
                Optional.ofNullable(customersInfoService.selectByPartyId(partyId).getResult()).ifPresent(u -> {
                    messageServiceClient.sendSmsCode(
                            u.getMobilePhone(),
                            fsms,
                            String.valueOf(60 * 30));

                    Request<MsgVo> request = new Request<>();
                    MsgVo msgVo = new MsgVo();
                    msgVo.setMsgTitle("乡邻福利树活动");
                    msgVo.setIsSave(Constant.YESNO.YES);
                    msgVo.setMessage(fmessage);
                    msgVo.setMsgType(Constant.MsgType.CASHBONUS_TIP.name());
                    msgVo.setIsDeleted("N");
                    msgVo.setExpiryTime(0);
                    msgVo.setLoginCheck(Constant.YESNO.NO.code);
                    msgVo.setPassCheck(Constant.YESNO.NO.code);
                    msgVo.setMsgSourceUrl(Constant.MsgType.CASHBONUS_TIP.name());
                    msgVo.setPartyId(partyId);
                    request.setReq(msgVo);
                    messageService.sendMsg(request, Arrays.asList(partyId));
                });
            });

            logger.info("---------- end to sendDailyLv  ");
        } catch (Exception e) {
            logger.warn("", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<ActPlantNotice> queryActPlantNotices(PageParam pageParam, Boolean isApp) {

        Example example = new Example(ActPlantNotice.class);
        example.and().andEqualTo("isDeleted", Constant.Delete_Y_N.N.name());
        if (isApp) {
            example.and().andLessThanOrEqualTo("startTime", new Date()).andGreaterThanOrEqualTo("endTime", new Date());
        }
        example.orderBy(" id").desc();
        List<ActPlantNotice> actPlantNoticeList = null;
        if (isApp) {
            actPlantNoticeList = actPlantNoticeMapper.selectByExample(example);
        } else {
            actPlantNoticeList = actPlantNoticeMapper.selectByExampleAndRowBounds(example, new RowBounds((pageParam.getCurPage() - 1) * pageParam.getPageSize(), pageParam.getPageSize()));
        }
        return actPlantNoticeList;
    }

    @Override
    public Integer queryActPlantNoticesCount() {
        return actPlantNoticeMapper.selectCount(ActPlantNotice.builder().isDeleted(Constant.Delete_Y_N.N.name()).build());
    }

    @Override
    public Long inserActPlantNotice(ActPlantNotice actPlantNotice) {
        actPlantNoticeMapper.insertSelective(actPlantNotice);
        return actPlantNotice.getId();
    }

    @Override
    public Boolean updateActPlantNotice(ActPlantNotice actPlantNotice) {
        Boolean flag = false;
        try {
            flag = actPlantNoticeMapper.updateByPrimaryKeySelective(actPlantNotice) == 1;
        } catch (Exception e) {
            logger.warn("updateActPlantNotice", e);
        }
        return flag;
    }

    @Override
    public Boolean updateExchangeStatus(Long id, String status) {

        RLock lock = redissonClient.getLock("ACT:updateExchangeStatus:id" + id);

        try {
            if (!lock.tryLock()) {
                throw new BizException(REPEAT);
            }

            ActPlantLvTran actPlantLvTran = actPlantLvTranMapper.selectByPrimaryKey(id);

            if (actPlantLvTran.getStatus().equals(ActPlantEnum.StatusType.S.name())) {
                throw new BizException(REPEAT);
            }

            UserVo userVo = personalService.queryUser(actPlantLvTran.getPartyId()).getResult();
            actPlantLvTran.setUserName(userVo.getTrueName());
            actPlantLvTran.setMobile(userVo.getLoginName());
            actPlantLvTran.setIsDeleted(Constant.Delete_Y_N.N.name());
            actPlantLvTran.setStatus(status);

            if (StringUtils.equals(status, ActPlantEnum.StatusType.F.name())) {
                actPlantLvTranMapper.updateByPrimaryKeySelective(actPlantLvTran);

                Request<MsgVo> request = new Request<>();
                MsgVo msgVo = new MsgVo();
                msgVo.setMsgTitle("福利树活动");
                msgVo.setIsSave(Constant.YESNO.YES);
                msgVo.setMessage("你涉及违规操作，兑换不成功！如有疑问请拨打400客服热线！");
                msgVo.setMsgType(Constant.MsgType.CASHBONUS_TIP.name());
                msgVo.setIsDeleted("N");
                msgVo.setExpiryTime(0);
                msgVo.setLoginCheck(Constant.YESNO.NO.code);
                msgVo.setPassCheck(Constant.YESNO.NO.code);
                msgVo.setMsgSourceUrl(Constant.MsgType.NOTIFY.name());
                msgVo.setPartyId(actPlantLvTran.getPartyId());
                request.setReq(msgVo);
                messageService.sendMsg(request, Arrays.asList(actPlantLvTran.getPartyId()));
                return true;
            }

            //获取礼品信息
            ActPlantPrize actPrizeLv = actPlantPrizeMapper.selectOne(ActPlantPrize.builder().code(actPlantLvTran.getPrizeCode()).isDeleted(Constant.Delete_Y_N.N.name()).build());
            //如果兑换的是现金则 将现金转换为金币，加上自身持有的金币
            if (actPrizeLv.getRewardType().equals(ActPlantEnum.ActPrizeType.GOLD.getCode())) {
                actPlantLvTran = ActPlantLvTran.builder().partyId(actPlantLvTran.getPartyId()).name(actPrizeLv.getName()).prizeCode(actPlantLvTran.getPrizeCode()).type(ActPlantEnum.TranType.EXANGE.name()).isDeleted("N").status(ActPlantEnum.StatusType.S.name()).userName(userVo.getTrueName()).mobile(userVo.getLoginName()).id(id).build();
                com.xianglin.cif.common.service.facade.model.Response<GoldcoinRecordVo> goldcoinRecordVoResponse =
                        goldcoinServiceClient.doRecord(GoldcoinRecordVo.builder()
                                .system("act")
                                .amount(actPrizeLv.getGold())
                                .fronPartyId(Constants.GOLD_SYS_ACCOUNT)
                                .toPartyId(actPlantLvTran.getPartyId())
                                .remark(ActPlantEnum.ActPlantGoldType.ACT_PLANT_GOLD_PRIZE.desc)
                                .type(ActivityEnum.ACT_PLANT.name())
                                .requestId(GoldSequenceUtil.getSequence(actPlantLvTran.getPartyId(), sequenceMapper
                                        .getSequence())).build());
                if (!com.google.common.base.Objects.equal(goldcoinRecordVoResponse.getCode(), 1000)) {
                    logger.error("添加金币失败！", JSON.toJSONString(goldcoinRecordVoResponse));
                    throw new BizException(ActPreconditions.ResponseEnum.ERROR);
                }
            } else if (actPrizeLv.getRewardType().equals(ActPlantEnum.ActPrizeType.VOUCHERS.getCode())) {
                switch (actPrizeLv.getCode()) {
                    case "1005":
                        EcApis.getRegisterUserAward(actPlantLvTran.getPartyId() + "", EcApis.EshopType.TICKET, new BigDecimal(20), "福利树活动话费券");
                        break;
                    case "1006":
                        EcApis.getRegisterUserAward(actPlantLvTran.getPartyId() + "", EcApis.EshopType.TICKET, new BigDecimal(50), "福利树活动话费券");
                        break;
                }

                Request<MsgVo> request = new Request<>();
                MsgVo msgVo = new MsgVo();
                msgVo.setMsgTitle("福利树活动");
                msgVo.setIsSave(Constant.YESNO.YES);
                msgVo.setMessage("你提交的兑换信息已审核通过，请去【我的】-【优惠券】中查看！");
                msgVo.setMsgType(Constant.MsgType.CASHBONUS_TIP.name());
                msgVo.setIsDeleted("N");
                msgVo.setExpiryTime(0);
                msgVo.setLoginCheck(Constant.YESNO.NO.code);
                msgVo.setPassCheck(Constant.YESNO.NO.code);
                msgVo.setMsgSourceUrl(Constant.MsgType.CASHBONUS_TIP.name());
                msgVo.setPartyId(actPlantLvTran.getPartyId());
                request.setReq(msgVo);
                messageService.sendMsg(request, Arrays.asList(actPlantLvTran.getPartyId()));
            }
//                    else {
//                        actPlantLvTran = ActPlantLvTran.builder().partyId(actPlantLvTran.getPartyId()).name(actPrizeLv.getName()).prizeCode(actPlantLvTran.getPrizeCode()).type(ActPlantEnum.TranType.EXANGE.name()).isDeleted("N").status(status).build();
//                    }
            //保存爱心交易明细
            actPlantLvTranMapper.updateByPrimaryKeySelective(actPlantLvTran);
        } catch (BizException b) {
            logger.warn("updateExchangeStatus", b);
            if (b.getResponseEnum().code.equals(USER_NOT_CERTIFICATION.code)) {
                throw new BizException(USER_NOT_CERTIFICATION);
            }
            if (b.getResponseEnum().code.equals(USERLV_NOTENOGH.code)) {
                throw new BizException(USER_NOT_CERTIFICATION);
            }
        } catch (Exception e) {
            logger.warn("updateExchangeStatus", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return true;
    }

}
