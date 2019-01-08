package com.xianglin.act.biz.shared.Impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xianglin.act.common.service.facade.model.ActVoteDTO;
import com.xianglin.act.common.service.facade.model.PageParam;
import com.xianglin.act.common.service.facade.model.PageResult;
import com.xianglin.act.common.service.facade.model.VoteAcquireRecordDTO;
import com.xianglin.act.common.util.DTOUtils;
import com.xianglin.act.common.util.DateUtils;
import com.xianglin.appserv.common.service.facade.MessageService;
import com.xianglin.appserv.common.service.facade.UserService;
import com.xianglin.appserv.common.service.facade.app.PersonalService;
import com.xianglin.cif.common.service.facade.CustomersInfoService;
import com.xianglin.core.model.vo.VoteShareVO;
import com.xianglin.core.service.PrizeAwardUtils;
import com.xianglin.act.biz.shared.VoteActService;
import com.xianglin.act.common.dal.enums.PrizeType;
import com.xianglin.act.common.dal.mappers.*;
import com.xianglin.act.common.dal.model.*;
import com.xianglin.act.common.service.integration.*;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.config.db.BaseConfiguration;
import com.xianglin.appserv.common.service.facade.app.ArticleService;
import com.xianglin.appserv.common.service.facade.model.Request;
import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.enums.Constant;
import com.xianglin.appserv.common.service.facade.model.vo.ArticleTipVo;
import com.xianglin.appserv.common.service.facade.model.vo.ArticleVo;
import com.xianglin.appserv.common.service.facade.model.vo.MsgVo;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import com.xianglin.cif.common.service.facade.model.CustomersDTO;
import com.xianglin.cif.common.service.facade.model.RoleDTO;
import com.xianglin.core.model.base.IVoteActAwardMsg;
import com.xianglin.core.model.base.VoteActAwardMsg;
import com.xianglin.core.model.enums.*;
import com.xianglin.act.common.util.BizException;
import com.xianglin.core.model.vo.VoteItemVO;
import com.xianglin.core.service.AttendanceUserStatusService;
import com.xianglin.core.service.VoteActivityContextV2;
import com.xianglin.xlStation.base.enums.XLStationEnums;
import com.xianglin.xlStation.base.model.SmsResponse;
import com.xianglin.xlschedule.common.service.spi.annotation.ServiceInterface;
import com.xianglin.xlschedule.common.service.spi.annotation.ServiceMethod;
import jodd.util.ArraysUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.xianglin.act.common.util.GlobalRequestContext.currentPartyId;
import static com.xianglin.act.common.util.ActPreconditions.ResponseEnum.*;
import static com.xianglin.core.service.ActivityContext.getActivityCode;
import static com.xianglin.core.service.VoteActivityContextV2.getCurrentVoteActivity;
import static com.xianglin.core.service.VoteActivityContextV2.getVoteActivityCode;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/1 10:57.
 */
@Service
@ServiceInterface(VoteActService.class)
public class VoteActServiceImpl implements VoteActService {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(VoteActServiceImpl.class);

    private final static String MESSAGE_CONTENT = "你的验证码是#{XXX}，如非本人操作，请忽略本短信";

    private final static String SHARED_IMG = "https://cdn02.xianglin.cn/25de90ba11f2e4af3890aa86ff699b1d-267810.jpg";

    private final static String VOTE_PARTAKE_COUNT = "VOTE_PARTAKE_COUNT";

    private final static String VOTE_CODE = "HD001";

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String DATETIME_FMT = "yyyy-MM-dd HH:mm:ss";

    private static final Map<String, Map<String, IVoteActAwardMsg>> VOTE_ACT_AWARD_MSG = Maps.newHashMap();

    static {
        try {
            //初始化消息提示map
            String packageName = DuanWuVoteActAwardMsgEnum.class.getPackage().getName();
            Reflections reflections = new Reflections(packageName);
            Set<Class<?>> voteActAwardMsgEnums = reflections.getTypesAnnotatedWith(VoteActAwardMsg.class);
            voteActAwardMsgEnums.stream()
                    .forEach(enumClass -> {
                        VoteActAwardMsg msgType = enumClass.getAnnotation(VoteActAwardMsg.class);
                        if (msgType == null) {
                            return;
                        }
                        if (!IVoteActAwardMsg.class.isAssignableFrom(enumClass)) {
                            return;
                        }
                        Map<String, IVoteActAwardMsg> temp = Maps.newHashMap();
                        if (enumClass.isEnum()) {
                            Object[] enumConstants = enumClass.getEnumConstants();
                            for (int i = 0; i < enumConstants.length; i++) {
                                Enum enumConstant = (Enum) enumConstants[i];
                                String prizeName = enumConstant.name();
                                temp.put(prizeName, (IVoteActAwardMsg) enumConstant);
                            }
                        }
                        VOTE_ACT_AWARD_MSG.put(msgType.activityCode(), temp);
                    });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private ActVoteItemMapper voteItemMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private PersonalServiceClient personalServiceClient;

    @Autowired
    private AttendanceUserStatusService attendanceUserStatusService;

    @Autowired
    private ActVoteRelMapper actVoteRelMapper;

    @Autowired
    private PrizeAwardUtils prizeAwardUtils;

    @Autowired
    private MessageServiceClient messageServiceClient;

    @Autowired
    private CustomersInfoServiceClient customersInfoServiceClient;

    @Autowired
    private CustomerPrizeMapper customerPrizeMapper;

    @Autowired
    private CustomerAcquireRecordMapper customerAcquireRecordMapper;

    @Autowired
    private PrizeMapper prizeMapper;

    @Autowired
    private ArticleServiceClient articleServiceClient;

    @Autowired
    private CustomerAcquireContactinfoMapper customerAcquireContactinfoMapper;

    @Autowired
    private AppgwService appgwService;

    @Autowired
    private BaseConfiguration baseConfiguration;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private ActivityConfigMapper activityConfigMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private PersonalService personalService;


    @Override
    public Activity getVoteActContext(String activityCode) {

        if (StringUtils.isBlank(activityCode)) {
            throw new BizException(ACTIVITY_CODE_NOT_EXIST);
        }
        Activity activity = activityMapper.selectActivity(activityCode);
        if (activity != null) {
            VoteActivityContextV2.setCurrentVoteActivity(activity);
        } else {
            throw new BizException(ACT_NOT_EXIST);
        }
        return activity;
    }

    @Override
    public void addVoteItem(String description, String imgUrl) {

        Long partyId = currentPartyId();
        if (partyId == null) {
            throw new BizException(CUSTOMER_INFO_MISS);
        }

        Date now1 = new Date();
        ActivityConfig voteDateStart = queryActivityConfigByCode(getActivityCode(),com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.REGISTER_START_TIME.name());
        ActivityConfig voteDateEnd = queryActivityConfigByCode(getActivityCode(),com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.REGISTER_END_TIME.name());
        boolean registerIsExpire = registerIsExpire(now1,v -> now1.after(DateUtils.parse(DateUtils.DATETIME_FMT,voteDateStart.getConfigValue())) && now1.before(DateUtils.parse(DateUtils.DATETIME_FMT,voteDateEnd.getConfigValue())));
        if (!registerIsExpire){
            logger.info("不在报名时间内，不可报名",voteDateEnd.getConfigValue(),voteDateEnd.getConfigValue());
            throw new BizException(REGISTER_DATE_LIMIT);
        }

        //当前已参与报名人数
        Integer count = voteItemMapper.selectCount(ActVoteItem.builder().activityCode(getActivityCode()).isDeleted("0").build());
        //是否限制报名人数
        isLimitPeopleNum(count);

        ActVoteItem actVoteItemTemp = ActVoteItem.builder()
                .activityCode(getCurrentVoteActivity().getActivityCode())
                .partyId(partyId)
                .isDeleted("0")
                .build();
        List<ActVoteItem> tempList = voteItemMapper.select(actVoteItemTemp);
        if (tempList.size() > 0) {
            throw new BizException(ACT_HAS_TAKE_PART_IN);
        }

        LocalDateTime now = LocalDateTime.now();
        ActVoteItem actVoteItem = ActVoteItem.builder()
                .activityCode(getCurrentVoteActivity().getActivityCode())
                .orderNumber(countPartakePeople() + 1)
                .partyId(partyId)
                .images(imgUrl)
                .description(description)
                .baseVoteNum(0)
                .realVoteNum(0)
                .isDeleted("0")
                .creator(partyId.toString())
                .updater(partyId.toString())
                .createDate(now)
                .updateDate(now)
                .build();
        voteItemMapper.insert(actVoteItem);
    }

    private boolean registerIsExpire(Date now,Predicate<Date> p) {
        return p.test(now);
    }

    @Override
    public boolean checkPartakeInStatus() {

        Long partyId = currentPartyId();
        if (partyId == null) {
            return false;
        }
        ActVoteItem actVoteItemTemp = ActVoteItem.builder()
                .activityCode(getCurrentVoteActivity().getActivityCode())
                .partyId(partyId)
                .isDeleted("0")
                .build();
        List<ActVoteItem> tempList = voteItemMapper.select(actVoteItemTemp);
        return tempList.size() > 0;
    }

    @Override
    public VoteShareVO itemShareInfo() {

        VoteShareVO voteShareVO = new VoteShareVO();

        try {
            Map<String,String> config = queryActivityConfigByCode(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityCode.HD001.name(),getKeys());

            //是否显示二维码/logo
            if (StringUtils.isNotEmpty(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.LOGO_QR_TYPE.name()))){
                voteShareVO.setLogoTyepe(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.LOGO_QR_TYPE.name()));
            }
            //二维码/logo
            if (StringUtils.isNotEmpty(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.LOGO_CODE.name()))){
                voteShareVO.setLogo(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.LOGO_CODE.name()));
            }
            //分享图标
            if (StringUtils.isNotEmpty(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.WECHAT_SHARED_IMG.name()))){
                voteShareVO.setIcon(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.WECHAT_SHARED_IMG.name()));
            }
            //分享内容
            if (StringUtils.isNotEmpty(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.WECHAT_CONTENT.name()))){
                voteShareVO.setSubTitle(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.WECHAT_CONTENT.name()));
            }
            //分享标题
            if (StringUtils.isNotEmpty(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.WECHAT_TITLE.name()))){
                voteShareVO.setTitle(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.WECHAT_TITLE.name()));
            }
            //是否需要未注册用户注册
            if (StringUtils.isNotEmpty(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.NEED_USER_REGISTER.name()))){
                voteShareVO.setNeedUserRegister(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.NEED_USER_REGISTER.name()));
            }
            //分享路径
            if (StringUtils.isNotEmpty(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.SHARED_TYPE.name()))){
                voteShareVO.setShareType(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.SHARED_TYPE.name()));
            }
            //投票按钮
            if (StringUtils.isNotEmpty(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_IMG.name()))){
                voteShareVO.setVoteImg(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_IMG.name()));
            }
            //参与按钮
            if (StringUtils.isNotEmpty(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.JOIN_IMG.name()))){
                voteShareVO.setJoinImg(config.get(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.JOIN_IMG.name()));
            }
            //获取分享链接
            voteShareVO.setShareUrl(configMapper.selectConfig("VOTE_SHARE_URL"));
            //获取预览分享链接
            voteShareVO.setPreviewShareUrl(configMapper.selectConfig("VOTE_PREVIEW_SHARE_URL"));
        }catch (Exception e){
            logger.warn("请先配置参数!",e);
            throw new BizException(ActPreconditions.ResponseEnum.VOTE_KEY_EMPTY);
        }
        return voteShareVO;
    }

    private List<String> getKeys(){
        ArrayList<String> list = Lists.newArrayList();
        list.add(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.LOGO_QR_TYPE.name());
        list.add(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.LOGO_CODE.name());
        list.add(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.WECHAT_SHARED_IMG.name());
        list.add(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.WECHAT_CONTENT.name());
        list.add(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.WECHAT_TITLE.name());
        list.add(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.NEED_USER_REGISTER.name());
        list.add(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.SHARED_TYPE.name());
        list.add(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_IMG.name());
        list.add(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.JOIN_IMG.name());
        return list;
    }

    @Override
    public Map<String, String> queryActivityConfigByCode(String activityCodes,List<String> keys) {
        Example example = new Example(ActivityConfig.class);
        example.and().andEqualTo("activityCode", com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityCode.HD001.name()).andEqualTo("isDeleted",Constant.Delete_Y_N.N.name());
        if (CollectionUtils.isNotEmpty(keys)){
            example.and().andIn("configKey",keys);
        }
        Map<String,String> config =  activityConfigMapper.selectByExample(example).stream().collect(Collectors.toMap(ActivityConfig::getConfigKey,v -> v.getConfigValue()));
        if (config == null){
            logger.error("查询参数配置失败：{}");
            throw new BizException(ActPreconditions.ResponseEnum.VOTE_KEY_EMPTY);
        }else {
            return config;
        }
    }

    @Override
    public Boolean updateActivity(String activityCode, String type) {
        Activity activity = activityMapper.selectActivity(VOTE_CODE);
        if (activity == null){
            //异常，活动不存在
            logger.error("查询活动失败：{}");
            throw new BizException(ActPreconditions.ResponseEnum.ACT_NOT_EXIST);
        }
        switch (type) {
            case "END":
                endActivity(VOTE_CODE);
                break;
            case "PUBLISH": //发布活动
                publishActivity(VOTE_CODE);
                break;
            case "CLEAR": //清除活动记录
                clearActivityRecord(VOTE_CODE);
                break;
            default:
                logger.warn("updateActivity type not exi: " + type);
        }
        
        return true;
    }

    /**
     *核销管理列表 
     * @param pageParam
     * @return
     */
    @Override
    public PageResult<VoteAcquireRecordDTO> queryAcquireRecordList(PageParam<VoteAcquireRecordDTO> pageParam) {
        PageResult<VoteAcquireRecordDTO> pageResult = new PageResult<>();
        Example example = new Example(CustomerAcquire.class);
        example.and().andEqualTo("activityCode", VOTE_CODE).andEqualTo("isDeleted","0").andNotEqualTo("prizeCode",PrizeType.XL_GOLD_COIN.name());
        if(StringUtils.isNotEmpty(pageParam.getParam().getPhone())){
            example.and().andEqualTo("mobilePhone", pageParam.getParam().getPhone());  
        }
        example.orderBy("createDate").desc();
        List<CustomerAcquire> customerAcquires = customerAcquireRecordMapper.selectByExampleAndRowBounds(example, new RowBounds((pageParam.getCurPage() - 1) * pageParam.getPageSize(), pageParam.getPageSize()));
        List<VoteAcquireRecordDTO> collect = customerAcquires.stream()
                .map(this::convertVoteAcquireRecordDTO)
                .collect(Collectors.toList());

        int count = customerAcquireRecordMapper.selectCountByExample(example);
        pageResult.setCount(count);
        pageResult.setResult(collect);
        return pageResult;
    }
    
    private VoteAcquireRecordDTO convertVoteAcquireRecordDTO(CustomerAcquire customerAcquire){
        if(customerAcquire == null){
            return null;
        }
        String address = null;
        String isReceive = "N";
        String isMemecCode = "Y";
        String trueName = null;
        

        //查询奖品
        Prize prize = prizeMapper.selectActivityPrize(VOTE_CODE, customerAcquire.getPrizeCode());
        if(prize == null){
            //异常，未查询到奖品
            throw new BizException(ActPreconditions.ResponseEnum.VOTE_PRIZE_NOT_EXIST);
        }
        //查用户的收件人信息
        ContactInfo contactInfo = customerAcquireContactinfoMapper.selectOne(ContactInfo.builder().partyId(customerAcquire.getPartyId()).isDeleted("0").build());
        if(prize.getPrizeType().equals(PrizeType.ENTITY.name()) && contactInfo != null){
            address =contactInfo.getName()+"/"+contactInfo.getMobilePhone()+"/"+ contactInfo.getAddress();
        }
        if(!prize.getPrizeType().equals(PrizeType.ENTITY.name())){
            customerAcquire.setMemcCode(null);
        }
        //查用户是否领取   状态为奖品发放中或者点我晒单为已领取
        if(StringUtils.isNotEmpty(customerAcquire.getStatus()) && (StringUtils.equals(customerAcquire.getStatus(),VoteAwardEnum.GRANTING.name()) || StringUtils.equals(customerAcquire.getStatus(),VoteAwardEnum.SHARED.name()) || StringUtils.equals(customerAcquire.getStatus(),VoteAwardEnum.ENDED.name()))){
            isReceive = "Y"; 
        }
        
        //是否需要填写物流单号
        if(StringUtils.isNotEmpty(customerAcquire.getMemcCode()) || !prize.getPrizeType().equals(PrizeType.ENTITY.name()) || !StringUtils.isNotEmpty(address)){
            isMemecCode = "N";
        }
        //查用户的真是名称
        Response<UserVo> userVoResponse = personalService.queryUser(customerAcquire.getPartyId());
        if(userVoResponse.getResult()!= null && StringUtils.isNotEmpty(userVoResponse.getResult().getTrueName())){
            trueName = userVoResponse.getResult().getTrueName();
        }
        return VoteAcquireRecordDTO.builder().id(customerAcquire.getId()).activityCode(customerAcquire.getActivityCode()).activityName(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityCode.HD001.desc).activityType(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityType.VOTE.desc).isReceive(isReceive).phone(customerAcquire.getMobilePhone()).trueName(trueName).receiveTime(com.xianglin.act.common.util.DateUtils.formatDate(customerAcquire.getAcquireDate(),DATETIME_FMT)).prize(prize.getPrizeDesc()).addressee(address).memcCode(customerAcquire.getMemcCode()).isMemecCode(isMemecCode).build();
    }

    /**
     *修改物流单号 
     * @param voteAcquireRecordDTO
     * @return
     */
    @Override
    public Boolean updateAcquireRecord(VoteAcquireRecordDTO voteAcquireRecordDTO) {
        CustomerAcquire customerAcquire = customerAcquireRecordMapper.selectOne(CustomerAcquire.builder().id(voteAcquireRecordDTO.getId()).isDeleted("0").build());
        if(customerAcquire == null){  //记录不存在
            throw new BizException(ActPreconditions.ResponseEnum.VOTE_ACQUIRE_RECORD_NOT_EXIST);
        }
        customerAcquire.setMemcCode(voteAcquireRecordDTO.getMemcCode());
        Boolean flag = customerAcquireRecordMapper.updateByPrimaryKey(customerAcquire)==1;
        List<Long> partyIds = new ArrayList<>(1);
        partyIds.add(customerAcquire.getPartyId());
        //发推送
        Request<MsgVo> request = new Request<>();
        MsgVo msgVo = new MsgVo();
        msgVo.setMsgTitle("投票活动奖励");
        msgVo.setIsSave(Constant.YESNO.YES);
        msgVo.setMessage("你的礼品已发货，物流单号：<a style='color:#2f96ff;' href=\"https://m.kuaidi100.com/app/query/?&nu=" + voteAcquireRecordDTO.getMemcCode() + "\">" + voteAcquireRecordDTO.getMemcCode() + "</a>，注意查收！");
        msgVo.setMsgType(Constant.MsgType.CASHBONUS_TIP.name());
        msgVo.setIsDeleted("N");
        msgVo.setExpiryTime(0);
        msgVo.setLoginCheck(Constant.YESNO.NO.code);
        msgVo.setPassCheck(Constant.YESNO.NO.code);
        msgVo.setMsgSourceUrl(Constant.MsgType.NOTIFY.name());
        msgVo.setPartyId(customerAcquire.getPartyId());
        request.setReq(msgVo);
        messageService.sendMsg(request, partyIds);
        return flag;
    }

    @Override
    public ActivityConfig queryActivityConfigByCode(String activityCode, String key) {
        ActivityConfig activityConfig = activityConfigMapper.selectOne(ActivityConfig.builder().activityCode(activityCode).configKey(key).build());
        return activityConfig;
    }

    /**
     * 检查预览用户
     * @param partyId
     */
    @Override
    public void checkPreviewUserByPartyId(Long partyId) {
        if(partyId == null){
            throw new BizException(NO_LOGIN_403); 
        }
        String vote_preview_user = configMapper.selectConfig("VOTE_PREVIEW_USER");
        if(!StringUtils.contains(vote_preview_user,partyId+"")){
            throw new BizException(VOTE_PREVIEW_USER_NOT_EXIST);
        }
    }


    @Override
    public Boolean isStopRegister(String start,String end) {
        ActivityConfig startConfig = queryActivityConfigByCode(getActivityCode(),start);
        ActivityConfig endConfig = queryActivityConfigByCode(getActivityCode(),end);
        Date startTime = DateUtils.parse(DateUtils.DATETIME_FMT,startConfig.getConfigValue());
        Date endTime = DateUtils.parse(DateUtils.DATETIME_FMT,endConfig.getConfigValue());
        Date now = new Date(System.currentTimeMillis());
        if (now.after(startTime) && now.before(endTime)){
            return false;
        }
        return true;
    }

    @Override
    public Boolean randomVote(final String activityCode) {
        String interval = configMapper.selectConfig("VOTE_RANDOM_INTERVAL");
        String[] strs = StringUtils.split(interval,"-");
        final int start = Integer.valueOf(strs[0]);
        final int end = Integer.valueOf(strs[1]);
        Arrays.stream(configMapper.selectConfig("VOTE_RANDOM_USERS").split(",")).parallel()
                .map(Long::valueOf)
                .forEach(v -> {
                    ActVoteItem item = getVoteItemByPartyId(v,activityCode);
                    java.util.Optional.ofNullable(item).ifPresent(vote -> {
                        int random = ThreadLocalRandom.current().nextInt(start,end);
                        voteItemMapper.updateByPrimaryKeySelective(ActVoteItem.builder().id(vote.getId())
                        .baseVoteNum(vote.getBaseVoteNum() + random)
                        .updateDate(LocalDateTime.now()).build());
                    });
                });
        return Boolean.TRUE;
    }

    /**
     * 结束活动
     * @param activityCode
     * @return
     */
    private Boolean endActivity(String activityCode) {
        List<String> list = new ArrayList<>();
        list.add("VOTE_END_TIME");
        list.add("REGISTER_END_TIME");
        Map<String,String> map = queryActivityConfig(activityCode,list);
        if(map == null){ //异常，未查询到投票时间
            logger.error("异常，未查询到投票时间：{}");
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
        //将投票结束时间改成当前时间
        activityConfigMapper.updateByCodeAndKey(activityCode,"VOTE_END_TIME", com.xianglin.act.common.util.DateUtils.formatDate(new Date(),DATETIME_FMT));
        activityConfigMapper.updateByCodeAndKey(activityCode,"REGISTER_END_TIME", com.xianglin.act.common.util.DateUtils.formatDate(new Date(),DATETIME_FMT));
        //修改活动开始的开关
        list.clear();
        list.add("ISPUBLISH");
        Map<String,String> isPublish = queryActivityConfig(activityCode,list);
        if(isPublish == null){ //异常，未查询到投票时间
            logger.error("异常，未查询到活动开始的开关：{}");
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
        Boolean flag = activityConfigMapper.updateByCodeAndKey(activityCode,"ISPUBLISH", "END")==1;
        return flag;
    }

    /**
     * 发布活动
     * @param activityCode
     * @return
     */
    private Boolean publishActivity(String activityCode) {
        List<String> list = new ArrayList<>();
        list.add("ISPUBLISH");
        Map<String,String> map = queryActivityConfig(VOTE_CODE,list);
        if(map == null){ //异常，未查询到投票时间
            logger.error("异常，未查询到投票时间：{}");
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
        Boolean flag = activityConfigMapper.updateByCodeAndKey(VOTE_CODE,"ISPUBLISH", "PUBLISH")==1;
        return flag;
    }

    /**
     * 清除活动记录
     * @param activityCode
     * @return
     */
    private Boolean clearActivityRecord(String activityCode) {
        List<ActVoteItem> actVoteItems = voteItemMapper.select(ActVoteItem.builder().isDeleted("0").activityCode(activityCode).build());
        actVoteItems.stream().forEach(vo->{
            vo.setIsDeleted("1");
            vo.setUpdateDate(LocalDateTime.now());
            voteItemMapper.updateByPrimaryKey(vo);   
        });
        List<ActVoteRel> actVoteRels = actVoteRelMapper.select(ActVoteRel.builder().isDeleted("0").activityCode(activityCode).build());
        actVoteRels.stream().forEach(vo->{
            vo.setIsDeleted("1");
            vo.setUpdateDate(LocalDateTime.now());
            actVoteRelMapper.updateByPrimaryKey(vo);
        });
        //清楚核销管理
        List<CustomerAcquire> customerAcquires = customerAcquireRecordMapper.select(CustomerAcquire.builder().isDeleted("0").activityCode(VOTE_CODE).build());
        customerAcquires.stream().forEach(vo->{
            vo.setIsDeleted("1");
            vo.setUpdateDate(LocalDateTime.now());
            customerAcquireRecordMapper.updateByPrimaryKey(vo); 
        });
        
        return true;
    }

    @Override
    public ActVoteDTO queryVoteActivityList() {
        Activity activity = activityMapper.selectActivity(VOTE_CODE);
        if (activity == null){
            //异常，活动不存在
            logger.error("查询活动失败：{}");
            throw new BizException(ActPreconditions.ResponseEnum.ACT_NOT_EXIST);
        }
        ActVoteDTO actVoteDTO = ActVoteDTO.builder().id(activity.getId()).activityCode(activity.getActivityCode()).activityName(activity.getActivityName()).type(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityType.VOTE.desc).createTime(com.xianglin.act.common.util.DateUtils.formatDate(activity.getCreateDate(),DATETIME_FMT)).build();

        List<String> list = new ArrayList<>();
        list.add("REGISTER_START_TIME");//活动报名开始时间
        list.add("REGISTER_END_TIME");//动报名截止时间
        list.add("VOTE_START_TIME");//投票开始时间
        list.add("VOTE_END_TIME");//投票结束时间
        //查参数配置
        Map<String,String> map = queryActivityConfig(VOTE_CODE,list);
        if(map != null){
          //查询活动开始时间和结束时间
            String registerStartTime =  map.get("REGISTER_START_TIME");
            String voteStartTime = map.get("VOTE_START_TIME");
            String registerEndTime =  map.get("REGISTER_END_TIME");
            String voteEndTime = map.get("VOTE_END_TIME");
            if(StringUtils.isNotEmpty(voteStartTime) && StringUtils.isNotEmpty(voteEndTime)){
                Date startTime = queryActivityTime(registerStartTime,voteStartTime,"START");
                Date endTime = queryActivityTime(registerEndTime,voteEndTime,"END");
                //查询活动状态
                String status = queryActivityStatus(startTime,endTime);
                actVoteDTO.setStatus(status);
                actVoteDTO.setActivityTime(com.xianglin.act.common.util.DateUtils.formatDate(startTime,DATETIME_FMT)+"-"+com.xianglin.act.common.util.DateUtils.formatDate(endTime,DATETIME_FMT));
            }else{
                actVoteDTO.setStatus("NOTPUBLISH");
            }
            
        }else{ //未发布
            actVoteDTO.setStatus("NOTPUBLISH");
        }
        return actVoteDTO;
    }


    /**
     * 查询活动时间
     * @param regTime
     * @param voTime
     * @return
     */
    private Date queryActivityTime(String regTime,String voTime,String type) {
        Date voteTime = com.xianglin.act.common.util.DateUtils.formatStr(voTime,DATETIME_FMT);
        if(StringUtils.isEmpty(regTime)) {
            return voteTime;
        }
        //活动报名开始时间
        Date registerTime = com.xianglin.act.common.util.DateUtils.formatStr(regTime,DATETIME_FMT);
        if(StringUtils.contains(type,"END")){
            if(registerTime.compareTo(voteTime)>0){
                return registerTime;
            }else{
                return voteTime;
            }
        }else{
            if(registerTime.compareTo(voteTime)<0){
                return registerTime;
            }else{
                return voteTime;
            }
        }
        
    }

    /**
     * 查询活动状态
     * @param startTime   endTime
     * @return
     */
    private String queryActivityStatus(Date startTime,Date endTime) {
        //未发布：活动保存后 并未发布
           //查询是否点击发布
        ActivityConfig activityConfig = activityConfigMapper.selectOne(ActivityConfig.builder().activityCode(VOTE_CODE).configKey("ISPUBLISH").build());
        if(activityConfig != null && activityConfig.getConfigValue().equals("NOTPUBLISH")){
            return "NOTPUBLISH"; 
        }
        
        if(activityConfig != null && activityConfig.getConfigValue().equals("PUBLISH")){
            //未开始：活动已发布，当前时间<活动开始时间
            if(startTime.compareTo(new Date())>0){
                return "NOTBEGIN";
            }

            //进行中：活动已发布，活动开始时间<=当前时间>活动结束时间
            if(startTime.compareTo(new Date())<=0 && new Date().compareTo(endTime)<0){
                return "ONGOING";
            } 
        }
        
        //奖励发放中：即顺延活动结束时间后的5个工作日
        //当前时间大于结束时间并且小于等于结束时间后的5个工作日
        if(new Date().compareTo(endTime)>0 && new Date().compareTo(DateUtils.skipDateTime(endTime,5))<=0){
            return "GRANT";
        }

        //已结束：活动已发布，当前时间>=活动结束时间+5天
        if(new Date().compareTo(DateUtils.skipDateTime(endTime,5)) >= 0){
            return "FINISH";
        }
        return null;
    }

    /**
     * 查参数配置
     * @return
     */
    private Map<String,String> queryActivityConfig(String activityCode,List<String> list) {
        Example example = new Example(ActivityConfig.class);
        example.and().andEqualTo("activityCode", activityCode).andIn("configKey",list).andEqualTo("isDeleted","N");
        Map<String,String> config =  activityConfigMapper.selectByExample(example).stream().collect(Collectors.toMap(ActivityConfig::getConfigKey,v -> v.getConfigValue()));
        return config;
    }

    @Override
    public int countPartakePeople() {

        ActVoteItem actVoteItem = ActVoteItem.builder()
                .activityCode(getCurrentVoteActivity().getActivityCode())
                .isDeleted("0")
                .build();
        return voteItemMapper.selectCount(actVoteItem);
    }

    @Override
    public boolean hasPartakeInAct(Long currentPartyId) {

        return getVoteItemByPartyId(currentPartyId) != null;
    }

    @Override
    public List<VoteItemVO> queryItemList(OrderTypeEnum orderType, Integer curPage, Integer pageSize,Long lastId) {

        try {
            checkArgument(orderType != null);
            checkArgument(curPage != null);
            checkArgument(pageSize != null);
        } catch (Exception e) {
            throw new BizException(ACT_PARAM_ERROR);
        }
        Long currentPartyId = currentPartyId();

        String order = orderType.name();
        Activity activity = getCurrentVoteActivity();
        String activityCode = activity.getActivityCode();
        List<ActVoteItem> actVoteItems = voteItemMapper.selectItemList(activityCode, order, pageSize,lastId);
        return actVoteItems.stream()
                .map(actVoteItemTemp -> this.convertDO2VO(currentPartyId, actVoteItemTemp))
                .collect(Collectors.toList());
    }

    @Override
    public List<VoteItemVO> queryPopularityItemList() {
        Example example = new Example(ActVoteItem.class);
        example.setOrderByClause("(BASE_VOTE_NUM + REAL_VOTE_NUM) DESC, CREATE_DATE ASC");
        ActVoteItem actVoteItem = ActVoteItem.builder()
                .activityCode(getCurrentVoteActivity().getActivityCode())
                .isDeleted("0")
                .build();
        example.and().andEqualTo(actVoteItem);
        List<ActVoteItem> actVoteItems = voteItemMapper.selectByExampleAndRowBounds(example, new RowBounds(0, 100));
        return actVoteItems.stream()
                .map(this::convertDO2CommonVO)
                .collect(Collectors.toList());
    }

    @Override
    public ActVoteItem getVoteItemByPartyId(Long partyId) {

        ActVoteItem actVoteItem = ActVoteItem.builder()
                .activityCode(getCurrentVoteActivity().getActivityCode())
                .partyId(partyId)
                .isDeleted("0")
                .build();
        return voteItemMapper.selectOne(actVoteItem);
    }

    public ActVoteItem getVoteItemByPartyId(Long partyId,String activityCode) {

        ActVoteItem actVoteItem = ActVoteItem.builder()
                .activityCode(activityCode)
                .partyId(partyId)
                .isDeleted("0")
                .build();
        return voteItemMapper.selectOne(actVoteItem);
    }

    @Override
    public VoteItemVO getVoteItem(Long id) {

        if (id == null) {
            throw new BizException(FAIL);
        }
        ActVoteItem actVoteItem = voteItemMapper.selectByPrimaryKey(id);
        if (actVoteItem == null) {
            return null;
        }
        Long partyId = currentPartyId();
        VoteItemVO voteItemVO = convertDO2VO(partyId, actVoteItem);

        com.xianglin.appserv.common.service.facade.model.Response<UserVo> userVoResponse = personalServiceClient.queryUser(voteItemVO.getPartyId());
        UserVo result = userVoResponse.getResult();
        if (result == null) {
            throw new BizException(ACT_USER_INFO_ERROR);
        }
        voteItemVO.setUserName(attendanceUserStatusService.getUserName(result));
        voteItemVO.setHeadImg(attendanceUserStatusService.getHeadImg(result));
        return voteItemVO;
    }

    private VoteItemVO convertDO2VO(Long partyId, ActVoteItem actVoteItem) {

        if (actVoteItem == null) {
            return null;
        }
        VoteItemVO itemVO = this.convertDO2CommonVO(actVoteItem);
        //未登录则没有是否投票状态
        if (partyId == null) {
            itemVO.setShowVoteButton(true);
            return itemVO;
        }

        Activity activity = getCurrentVoteActivity();
        ActivityConfig activityConfig = this.queryActivityConfigByCode(activity.getActivityCode(), com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_TYPE.name());

        String voteMode = activityConfig.getConfigValue();
        if (voteMode == null) {
            voteMode = "A";
        }
        Object startDate = null;
        Object endDate = null;
        switch (voteMode) {
            case "B":
                startDate = LocalDate.now();
                endDate = ((LocalDate) startDate).plusDays(1);
                break;
            case "A":
                //不做处理
                break;
            default:
                throw new BizException(ACT_CONVERT_ERROR);
        }
        Example example = new Example(ActVoteRel.class);
        Example.Criteria criteria = example.and()
                .andEqualTo("isDeleted", "0")
                .andEqualTo("partyId", partyId)
                .andEqualTo("userType", UserType.VOTER_APP_USER.name())
                .andEqualTo("activityCode", getVoteActivityCode())
                .andEqualTo("toPartyId", actVoteItem.getPartyId());
        if (startDate != null) {
            criteria.andGreaterThanOrEqualTo("createDate", startDate);
        }
        if (startDate != null) {
            criteria.andLessThan("createDate", endDate);
        }
        List<ActVoteRel> actVoteRels = actVoteRelMapper.selectByExample(example);
        itemVO.setShowVoteButton(actVoteRels.isEmpty());
        return itemVO;
    }

    @Override
    public VoteItemVO searchVoteItem(String serialNumber) {

        long itemId;
        try {
            itemId = Long.parseLong(serialNumber);
        } catch (NumberFormatException e) {
            throw new BizException(FAIL);
        }
        ActVoteItem query = ActVoteItem.builder()
                .orderNumber(Integer.valueOf(serialNumber))
                .activityCode(getActivityCode())
                .isDeleted(Constant.Delete_1_0.N.code).build();
        ActVoteItem actVoteItem = voteItemMapper.selectOne(query);

        return this.convertDO2VO(currentPartyId(), actVoteItem);
    }

    /**
     * hasVote字段不转换
     *
     * @param actVoteItem
     * @return
     */
    private VoteItemVO convertDO2CommonVO(ActVoteItem actVoteItem) {

        if (actVoteItem == null) {
            return null;
        }
        Long id = actVoteItem.getId();
        Long partyId = actVoteItem.getPartyId();
        Integer baseVoteNum = actVoteItem.getBaseVoteNum();
        Integer realVoteNum = actVoteItem.getRealVoteNum();
        String headImg = null;
        UserVo userVo = personalServiceClient.queryUser(partyId).getResult();
        if (userVo != null){
            if (StringUtils.isNotEmpty(userVo.getHeadImg())){
                headImg = userVo.getHeadImg();
            }else {
                headImg = configMapper.selectConfig("DEFAULT_USER_HEADIMG");
            }
        }
        return VoteItemVO.builder()
                .id(id)
                .serialNum(paddingId(actVoteItem.getOrderNumber().longValue()))
                .partyId(partyId)
                .voteNum(null2Zero(baseVoteNum) + null2Zero(realVoteNum))
                .imageUrl(actVoteItem.getImages())
                .description(actVoteItem.getDescription())
                .headImg(headImg)
                .build();
    }

    @Override
    public boolean isExpire() {

        LocalDateTime endTime = getEndTime();
        return LocalDateTime.now().isAfter(endTime);
    }

    @Override
    public LocalDateTime getEndTime() {
        ActivityConfig reEnd = ActivityConfig.builder()
                .activityCode(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityCode.HD001.name())
                .configKey(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.REGISTER_END_TIME.name())
                .build();
        reEnd = activityConfigMapper.selectOne(reEnd);
        ActivityConfig voEnd = ActivityConfig.builder()
                .activityCode(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityCode.HD001.name())
                .configKey(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_END_TIME.name())
                .build();

        reEnd = activityConfigMapper.selectOne(reEnd);
        voEnd = activityConfigMapper.selectOne(voEnd);

       Date endTime = queryActivityTime(reEnd.getConfigValue(),voEnd.getConfigValue(),"END");

        return DateUtils.parseDate2LocalDateTime(endTime);
    }

    private String paddingId(Long id) {

        checkArgument(id != null);
        if (id > 9999) {
            throw new BizException(ACT_ID_TOO_BIG);
        }
        return Strings.padStart(id.toString(), 4, '0');
    }

    private int null2Zero(Integer integer) {

        if (integer == null) {
            return 0;
        }
        return integer;
    }


    @Override
    public CustomerAcquire voteItem(Long partyId, Long toPartyId) {

        ActivityConfig start = queryActivityConfigByCode(getActivityCode(), com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_START_TIME.name());
        ActivityConfig end = queryActivityConfigByCode(getActivityCode(), com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_END_TIME.name());

        if (!isVoteItem(start.getConfigValue(),end.getConfigValue())){
            throw new BizException(VOTE_DATE_LIMIT);
        }

        final Activity activity = VoteActivityContextV2.getCurrentVoteActivity();

        checkVoteCondition(partyId, toPartyId, activity);

        // record
        updateVoteRecord(partyId, UserType.VOTER_APP_USER, toPartyId);

        ActivityConfig voteAwardType = queryActivityConfigByCode(getActivityCode(), com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_REWARD_TYPE.name());

        CustomerAcquire acquire = new CustomerAcquire();
        CustomerPrize customerPrize = customerPrizeMapper.selectCustomerPrizeUnique(
                activity.getActivityCode(),
                UserType.VOTE_VOTER.name(),
                PrizeType.XL_GOLD_COIN.name());

        //若配置了随机金币奖励则发放随机金币奖励
        if (StringUtils.isNotEmpty(voteAwardType.getConfigValue()) && StringUtils.equals(voteAwardType.getConfigValue(),com.xianglin.act.common.service.facade.constant.ActivityConfig.VoteGoldType.B.name())){

            Prize prize = new Prize();
            // 随机生成奖品的值
            prize = randomPrizeValue(customerPrize);
            prize.setActivityCode(activity.getActivityCode());
            //发放随机金币奖励
            prizeAwardUtils.award(Party.crateParty(partyId), prize);

            // insert acquire record
            acquire = CustomerAcquire.builder()
                    .mobilePhone(null)
                    .partyId(partyId)
                    .prizeCode(PrizeType.XL_GOLD_COIN.name())
                    .activityCode(activity.getActivityCode())
                    .memcCode(prize.getMemcCode())
                    .prizeValue(prize.getAmount())
                    .userType(UserType.VOTER_APP_USER.name())
                    .build();

            customerAcquireRecordMapper.insertCustomerAcquireRecord(acquire);
        }

        // 发送投票消息
        ArticleTipVo articleTipVo = new ArticleTipVo();
        articleTipVo.setPartyId(partyId);
        articleTipVo.setToPartyId(toPartyId);
        articleTipVo.setTipType(Constant.ArticleTipType.FOLLOW.name());
        articleTipVo.setTipStatus(Constant.YESNO.YES.code);
        articleTipVo.setContent("给你投了一票");
        articleServiceClient.publishArticleTip(articleTipVo);

        return acquire;
    }

    // 站内投票 前置条件 检查
    private void checkVoteCondition(Long partyId, Long toPartyId, Activity activity) {

        // 只能给自己投一次
        if (partyId.equals(toPartyId)) {
            ActVoteRel actVoteRel = ActVoteRel.builder()
                    .activityCode(getVoteActivityCode())
                    .partyId(partyId)
                    .toPartyId(toPartyId)
                    .isDeleted("0")
                    .build();

            int i = actVoteRelMapper.selectCount(actVoteRel);
            if (i >= 1) {
                throw new BizException(ActPreconditions.ResponseEnum.VOTE_LIMIT_SELF);
            }
        }
        //查询投票限制
        ActivityConfig peopleNum = queryActivityConfigByCode(getCurrentVoteActivity().getActivityCode(), com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_PEOPLE_NUM.name());
        ActivityConfig voteType = queryActivityConfigByCode(getCurrentVoteActivity().getActivityCode(), com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.VOTE_TYPE.name());

        //B——每日都可投票 A——一次性投票
        if (com.xianglin.act.common.service.facade.constant.ActivityConfig.VoteType.B.name().equals(voteType.getConfigValue())) {
            // 每个用户点一天最多点5个赞
            int i = actVoteRelMapper.selectVotedCountToday(partyId, getVoteActivityCode());
            if (i >= Integer.parseInt(peopleNum.getConfigValue())) {
                throw new BizException(ActPreconditions.ResponseEnum.VOTE_LIMIT);
            }
        } else {
            ActVoteRel actVoteRel = ActVoteRel.builder()
                    .activityCode(getVoteActivityCode())
                    .partyId(partyId)
                    .userType(UserType.VOTER_APP_USER.name())
                    .isDeleted("0")
                    .build();
            int i = actVoteRelMapper.selectCount(actVoteRel);
            // 限制次数
            if (i >= 1) {
                throw new BizException(ActPreconditions.ResponseEnum.VOTE_LIMIT);
            }
        }

        // 每日只能为同一名选手投一票
        Example o = new Example(ActVoteRel.class);
        LocalDate localDate = LocalDateTime.now().toLocalDate();

        o.and().andEqualTo("partyId", partyId)
                .andEqualTo("toPartyId", toPartyId)
                .andEqualTo("userType", UserType.VOTER_APP_USER.name())
                .andBetween("createDate", localDate, localDate.plusDays(1)).andEqualTo("isDeleted","0");
        if (actVoteRelMapper.selectCountByExample(o) >= 1) {
            //每日只能为同一名选手投一票
            throw new BizException(ActPreconditions.ResponseEnum.VOTE_LIMIT_OF_DAY);
        }
    }

    private Prize randomPrizeValue(CustomerPrize customerPrize) {

        BigDecimal add = new BigDecimal(Math.random()).multiply(customerPrize.getMaxValue().subtract(customerPrize.getMinValue()));
        BigDecimal amount = customerPrize.getMinValue().add(add);

        final Activity activity = VoteActivityContextV2.getCurrentVoteActivity();

        Prize prize = prizeMapper.selectActivityPrize(activity.getActivityCode(), customerPrize.getPrizeCode());
        prize.setAmount(amount.setScale(customerPrize.getRemainValue(), BigDecimal.ROUND_HALF_UP));
        return prize;
    }

    @Override
    public void sendMessage(String mobilePhone) {

        com.xianglin.cif.common.service.facade.model.Response<CustomersDTO> response1 = customersInfoServiceClient.selectByMobilePhone(mobilePhone);
        if (response1.isSuccess()) {
            List<RoleDTO> roleDTOs = response1.getResult().getRoleDTOs();
            if (roleDTOs != null && roleDTOs.size() > 0) {
                for (RoleDTO roleDTO : roleDTOs) {
                    if (Constants.APP_USER.equals(roleDTO.getRoleCode())) {
                        // 你已是APP用户，去APP中可每天投1票！
                        throw new BizException(ActPreconditions.ResponseEnum.VOTE_USER_REGISTERED);
                    }
                }
            }
        }

        // 发送短信校验码
        com.xianglin.xlStation.base.model.Response response =
                messageServiceClient.sendSmsCode(
                        mobilePhone,
                        MESSAGE_CONTENT,
                        String.valueOf(60 * 30));

        if (!(XLStationEnums.ResultSuccess.getCode() == response.getBussinessCode())) {
            logger.error("验证码发送失败：{}", JSON.toJSONString(response));
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
    }

    @Override
    public void checkMessage(String mobilePhone, String code, long toPartyId) {
        // 活动已结束，去APP查看更多活动！
        if (StringUtils.isAnyBlank(mobilePhone, code)) {
            logger.error("phone or code is null!");
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
        // 开户
        CustomersDTO customersDTO = new CustomersDTO();
        customersDTO.setMobilePhone(mobilePhone);
        customersDTO.setCreator(mobilePhone);
        com.xianglin.cif.common.service.facade.model.Response<CustomersDTO> openAccountResponse =
                customersInfoServiceClient.openAccount(customersDTO, Constants.SYSTEM_NAME);
        if (!openAccountResponse.isSuccess()) {
            logger.error("用户开户失败: {}", JSON.toJSONString(openAccountResponse));
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }

        SmsResponse smsResponse = messageServiceClient.checkSmsCode(mobilePhone, code, Boolean.TRUE);
        if (!(XLStationEnums.ResultSuccess.getCode() == smsResponse.getBussinessCode())) {
            throw new BizException(ActPreconditions.ResponseEnum.CHECK_MESSAGE_FAIL);
        }
        // 你已投过票，去乡邻APP,你可以每天投1票哟！
        ActVoteRel actVoteRel = ActVoteRel.builder()
                .partyId(openAccountResponse.getResult().getPartyId())
                .isDeleted("0").build();
        if (actVoteRelMapper.selectCount(actVoteRel) >= 1) {
            throw new BizException(ActPreconditions.ResponseEnum.VOTE_NEW_USER_VOTED);
        }

        updateVoteRecord(openAccountResponse.getResult().getPartyId(), UserType.VOTER_NEW, toPartyId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = RuntimeException.class)
    public void updateVoteRecord(long partyId, UserType userType, long toPartyId) {
        // 业务
        voteItemMapper.updateVoteNum(toPartyId);
        actVoteRelMapper.insertRecord(getCurrentVoteActivity().getActivityCode(), partyId, userType.name(), toPartyId);
    }

    @Override
    @ServiceMethod(description = "投票结算")
    public void calThisActivityAward(String activityCode, String userType) {

        if (StringUtils.isBlank(activityCode)) {
            logger.error("===========活动参数错误：结算的活动code不能为空===========");
            return;
        }
        if (StringUtils.isBlank(userType)) {
            logger.error("===========活动参数错误：结算的用户类型不能为空===========");
            return;
        }

        if (!isExpire()){
            logger.error("===========活动未结束：不可结算===========================");
            return;
        }

        ActVoteItem actVoteItem = ActVoteItem.builder()
                .activityCode(activityCode)
                .isDeleted("0")
                .build();
        int count = voteItemMapper.selectCount(actVoteItem);

        ActivityConfig activityConfig = queryActivityConfigByCode(getActivityCode(), com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.LEAST_PARTAKE.name());
        Integer integer = Integer.valueOf(activityConfig.getConfigValue());

        if (count < integer) {
            logger.info("===========活动参与人数小于{}，活动失败，不进行奖励结算===========", integer);
            return;
        }

        List<CustomerPrize> customerPrizes = customerPrizeMapper.selectCustomerPrize(activityCode, userType);
        //按顺序和奖励个数生成奖励列表
        ArrayList<CustomerPrize> allAwards = Lists.newArrayList();
        for (CustomerPrize customerPrize : customerPrizes) {
            int availableAmount = customerPrize.getAvailableAmount();
            //将奖励按个数复制到列表集合
            byte[] bytes = JSON.toJSONBytes(customerPrize);
            for (int i = 0; i < availableAmount; i++) {
                allAwards.add(JSON.parseObject(bytes, customerPrize.getClass()));
            }
        }
        //List<CustomerPrize> allAwards = customerPrizes.stream()
        //        .map(customerPrize -> {
        //            ArrayList<CustomerPrize> tempPrizes = Lists.newArrayList();
        //            int availableAmount = customerPrize.getAvailableAmount();
        //            //将奖励按个数复制到列表集合
        //            byte[] bytes = JSON.toJSONBytes(customerPrize);
        //            for (int i = 0; i < availableAmount; i++) {
        //                tempPrizes.add(JSON.parseObject(bytes, customerPrize.getClass()));
        //            }
        //            return tempPrizes;
        //        }).flatMap(Collection::stream)
        //        .collect(Collectors.toList());
        Example example = new Example(ActVoteItem.class);
        example.setOrderByClause("(BASE_VOTE_NUM + REAL_VOTE_NUM) DESC, ID ASC");
        example.and().andEqualTo(actVoteItem);
        List<ActVoteItem> actVoteItems = voteItemMapper.selectByExampleAndRowBounds(example, new RowBounds(0, allAwards.size()));

        Date now = new Date();
        Activity activity = getVoteActContext(activityCode);
        //遍历获奖人（获奖人可能少于奖励个数，所以有获奖人驱动奖励发放），发放奖励
        for (int i = 0; i < actVoteItems.size(); i++) {
            ActVoteItem item = actVoteItems.get(i);
            CustomerPrize customerPrize = allAwards.get(i);
            Long targetPartyId = item.getPartyId();

            UserVo userVo = null;
            try {
                Response<UserVo> userVoResponse = personalServiceClient.queryUser(targetPartyId);
                Optional<UserVo> userVoOptional = Response.checkResponse(userVoResponse);
                userVo = userVoOptional.get();
            } catch (Exception e) {
                logger.error("===========发放奖励时，获取用户信息错误。 prartyId -> [[ {} ]]===========", targetPartyId, e);
            }

            CustomerAcquire customerAcquireDO = new CustomerAcquire();
            customerAcquireDO.setAcquireDate(now);
            customerAcquireDO.setPartyId(targetPartyId);
            customerAcquireDO.setActivityCode(activityCode);
            customerAcquireDO.setUserType(userType);
            customerAcquireDO.setPrizeCode(customerPrize.getPrizeCode());
            customerAcquireDO.setPrizeValue(customerPrize.getMinValue());
            customerAcquireDO.setStatus(VoteAwardEnum.GRANT.name());
            customerAcquireDO.setMobilePhone(userVo.getLoginName());
            customerAcquireRecordMapper.insertCustomerAcquireRecord(customerAcquireDO);
            try {
                //发送消息
                sendAwardMessage(userVo, customerPrize, activity);
            } catch (Exception e) {
                logger.info("===========投票活动发送奖励消息失败 partyId -> [[ {} ]]===========", targetPartyId, e);
            }
        }
        logger.info("===========奖励结算完成：activityCode:[[ {} ]],userType:[[ {} ]]===========", activityCode, userType);
    }

    /**
     * 发送奖励消息
     *
     * @param targetUser
     * @param prize
     * @param currentVoteActivity
     */
    private void sendAwardMessage(UserVo targetUser, CustomerPrize prize, Activity currentVoteActivity) {

        if (targetUser == null) {
            logger.info("===========无法获取用户信息，跳过奖励通知环节：[[ {} ]]===========", JSON.toJSONString(prize, true));
            return;
        }
        Long targetPartyId = targetUser.getPartyId();
        String prizeCode = prize.getPrizeCode();
        //得到相关活动的相关奖励的消息模板
        Map<String, IVoteActAwardMsg> stringIVoteActAwardMsgMap = VOTE_ACT_AWARD_MSG.get(currentVoteActivity.getActivityCode());
        if (stringIVoteActAwardMsgMap == null) {
            logger.info("===========找不到消息模板：[[ {} ]]===========", JSON.toJSONString(prize, true));
            return;
        }
        IVoteActAwardMsg iVoteActAwardMsg = stringIVoteActAwardMsgMap.get(prizeCode);
        if (iVoteActAwardMsg == null) {
            logger.info("===========找不到消息模板：[[ {} ]]===========", JSON.toJSONString(prize, true));
            return;
        }
        Request<MsgVo> param = new Request<>();
        BigDecimal minValue = prize.getMinValue().divideToIntegralValue(prize.getUnitRmb());
        param.setReq(MsgVo.builder()
                .partyId(targetPartyId)
                .msgTitle("投票活动奖励")
                .isSave(Constant.YESNO.YES)
                .message(MessageFormat.format(iVoteActAwardMsg.getAppMessageTemp(), minValue.intValue()))
                .msgType(Constant.MsgType.CASHBONUS_TIP.name())
                .loginCheck(Constant.YESNO.NO.code)
                .passCheck(Constant.YESNO.NO.code).expiryTime(0)
                .isDeleted("N")
                .msgSource(Constant.MsgType.CASHBONUS_TIP.name()).build());
        //发送消息
        Exception tempEx = null;
        try {
            Response<Boolean> booleanResponse = messageServiceClient.sendMsg(param, Collections.singletonList(targetPartyId));
            if (!booleanResponse.isSuccess()) {
                throw new BizException("app奖励通知发送失败");
            }
        } catch (Exception e) {
            tempEx = e;
        }
        try {
            String shouldSendSms = baseConfiguration.getShouldSendSms();
            if (shouldSendSms == null || Objects.equals(shouldSendSms, "0")) {
                logger.info("===========[[ {短信发送标记关闭，不发送短信} ]]===========");
                return;
            }
            com.xianglin.xlStation.base.model.Response response = messageServiceClient.sendSmsByTemplate(targetUser.getLoginName(), iVoteActAwardMsg.getSmsMessageTemp(), new String[]{minValue.intValue() + ""});
            if (response.getBussinessCode() != 2000) {
                throw new BizException("短信奖励通知发送失败");
            }
        } catch (Exception e) {
            tempEx = e;
        }

        if (tempEx != null) {
            //只抛出最近一个异常
            throw new RuntimeException(tempEx);
        }
    }

    @Override
    public CustomerAcquire drawAward(long toPartyId) {
        CustomerAcquire acquire = customerAcquireRecordMapper.selectVoteRecord(toPartyId, VoteAwardEnum.GRANT.name(),getActivityCode());
        if (acquire == null) {
            logger.error("drawAward 奖品记录状态已异常, {}", toPartyId);
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }

        final Activity activity = VoteActivityContextV2.getCurrentVoteActivity();
        Prize prize = prizeMapper.selectActivityPrize(activity.getActivityCode(), acquire.getPrizeCode());
        acquire.setPrizeType(prize.getPrizeType());
        acquire.setPrizeName(prize.getPrizeName());
        // 实物跳到 填地址信息页面
        if (PrizeType.ENTITY.name().equals(prize.getPrizeType())) {
            return acquire;
        }
        prize.setAmount(acquire.getPrizeValue());
        prizeAwardUtils.award(Party.crateParty(toPartyId), prize);

        // update status
        acquire.setAcquireDate(new Date());
        acquire.setOldStatus(acquire.getStatus());
        acquire.setStatus(VoteAwardEnum.SHARED.name());
        acquire.setMemcCode(prize.getMemcCode());
        customerAcquireRecordMapper.updateAcquireRecord(acquire);
        return acquire;
    }

    @Override
    public void shareAward(long toPartyId) {

        CustomerAcquire acquire = customerAcquireRecordMapper.selectVoteRecord(toPartyId, VoteAwardEnum.SHARED.name(),getActivityCode());
        if (acquire == null) {
            logger.error("shareAward 奖品记录状态已异常, {}", toPartyId);
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }

        // update status
        acquire.setOldStatus(acquire.getStatus());
        acquire.setStatus(VoteAwardEnum.ENDED.name());
        customerAcquireRecordMapper.updateAcquireRecord(acquire);

        /*
         * 文字内容：
         * 实物类：
         * 图片：一张宣传图
         */
        ArticleVo articleVo = ArticleVo.builder()
                .groupIds(new Long[]{0L})
                .build();

        Prize prize = prizeMapper.selectActivityPrize(getCurrentVoteActivity().getActivityCode(), acquire.getPrizeCode());
        //获取活动的titile
        ActivityConfig activityConfig = queryActivityConfigByCode(com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityCode.HD001.name()
                ,com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.TITLE.name());
        // 实物跳到 填地址信息页面
        if (PrizeType.ENTITY.name().equals(prize.getPrizeType())) {
            articleVo.setArticle(String.format(
                    "我在乡邻%s中，获得%s奖励，感恩乡邻，会继续关注！",
                    activityConfig.getConfigValue(),
                    prize.getPrizeName()
            ));
        } else if (PrizeType.XL_GOLD_COIN.name().equals(prize.getPrizeType())){
            articleVo.setArticle(String.format(
                    "我在乡邻%s中，获得现金%s元奖励，感恩乡邻，会继续关注！",
                    activityConfig.getConfigValue(),
                    acquire.getPrizeValue().divide(prize.getUnitRmb()).stripTrailingZeros().toPlainString()));
        } else if (PrizeType.EC_PHONE_COUPON.name().equals(prize.getPrizeType())){
            articleVo.setArticle(String.format(
                    "我在乡邻%s中，获得%s元话费券奖励，感恩乡邻，会继续关注！",
                    activityConfig.getConfigValue(),
                    acquire.getPrizeValue().divide(prize.getUnitRmb()).stripTrailingZeros().toPlainString()));
        }else if (PrizeType.EC_COUPON.name().equals(prize.getPrizeType())){
            articleVo.setArticle(String.format(
                    "我在乡邻%s中，获得%s元购物券奖励，感恩乡邻，会继续关注！",
                    activityConfig.getConfigValue(),
                    acquire.getPrizeValue().divide(prize.getUnitRmb()).stripTrailingZeros().toPlainString()));
        }

        Boolean booleanResponse = appgwService.service(
                ArticleService.class,
                "publishArticleV1",
                Boolean.class,
                articleVo);

        logger.info("{} 发微博响应：{}", toPartyId, JSON.toJSONString(booleanResponse));
    }

    @Override
    public VoteItemVO myItem(Long partyId) {

        ActVoteItem build = ActVoteItem.builder().activityCode(getCurrentVoteActivity().getActivityCode()).isDeleted("0").build();
        int count = voteItemMapper.selectCount(build);
        ActVoteItem actVoteItem = voteItemMapper.myItem(partyId, getCurrentVoteActivity().getActivityCode());
        if (actVoteItem == null) {
            //是否限制报名人数
            isLimitPeopleNum(count);
            // 未发布
            throw new BizException(ActPreconditions.ResponseEnum.VOTE_NO_PUBLISH);
        }

        VoteItemVO voteItemVO = VoteItemVO.builder()
                .id(actVoteItem.getId())
                .serialNum(paddingId(actVoteItem.getOrderNumber().longValue()))
                .voteNum(actVoteItem.getBaseVoteNum() + actVoteItem.getRealVoteNum())
                .ranking(actVoteItem.getRanking())
                .imageUrl(actVoteItem.getImages())
                .awardEnum(VoteAwardEnum.parse(actVoteItem.getStatus()))
                .build();

        // 已过期
        if (isExpire()) {
            //获取活动人数限制
            ActivityConfig activityConfig = queryActivityConfigByCode(getActivityCode(), com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.LEAST_PARTAKE.name());
            if (StringUtils.isNotEmpty(activityConfig.getConfigValue())){
                String votePartakeCount = activityConfig.getConfigValue();
                Integer integer = Integer.valueOf(votePartakeCount);
                //如果未达到最少活动人数责直接活动失败!
                if (count < integer || voteItemVO.getAwardEnum() == VoteAwardEnum.VOTING) {
                    logger.info("===========活动参与人数小于{}，活动失败===========", integer);
                    voteItemVO.setAwardEnum(VoteAwardEnum.ENDED);
                }
            }
        }
        return voteItemVO;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void contactInfo(ContactInfo contactInfo) {
        // insert
        contactInfo.initDateOfInsert();
        Example example = new Example(ContactInfo.class);
        example.and().andEqualTo("partyId",contactInfo.getPartyId()).andEqualTo("isDeleted","0");

        ContactInfo contactInfo1 = customerAcquireContactinfoMapper.selectOne(ContactInfo.builder().isDeleted("0").partyId(contactInfo.getPartyId()).build());
        if (contactInfo1 != null){
            contactInfo1.setName(contactInfo.getName());
            contactInfo1.setMobilePhone(contactInfo.getMobilePhone());
            contactInfo1.setAddress(contactInfo.getAddress());
            customerAcquireContactinfoMapper.updateByPrimaryKeySelective(contactInfo1);
        }else {
            customerAcquireContactinfoMapper.insert(contactInfo);
        }
        CustomerAcquire acquire = customerAcquireRecordMapper.selectVoteRecord(contactInfo.getPartyId(), VoteAwardEnum.GRANT.name(),getActivityCode());
        if (acquire == null) {
            logger.error("shareAward 奖品记录状态已异常, {}", contactInfo.getPartyId());
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
        // update status
        acquire.setAcquireDate(new Date());
        acquire.setOldStatus(acquire.getStatus());
        acquire.setStatus(VoteAwardEnum.GRANTING.name());
        customerAcquireRecordMapper.updateAcquireRecord(acquire);
    }

    private boolean isVoteItem(String start,String end){
        Date startVoteTime = DateUtils.parse(DateUtils.DATETIME_FMT,start);
        Date endVoteTime = DateUtils.parse(DateUtils.DATETIME_FMT,end);
        Date now = new Date();
        if (now.after(startVoteTime) && now.before(endVoteTime)){
            return true;
        }
        return false;
    }

    private void isLimitPeopleNum(int count){
        //是否限制报名人数
        ActivityConfig voteLimit = queryActivityConfigByCode(getActivityCode(),com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.LIMIT_PEOPLE_TYPE.name());
        if (voteLimit != null && StringUtils.equals(voteLimit.getConfigValue(), com.xianglin.act.common.service.facade.constant.ActivityConfig.VoteType.B.name()) ){
            //获取限制投票最大人数
            ActivityConfig ticketLimit = queryActivityConfigByCode(getActivityCode(),com.xianglin.act.common.service.facade.constant.ActivityConfig.ActivityVote.MAX_PEOPLE_NUM.name());
            if (StringUtils.isNotEmpty(ticketLimit.getConfigValue()) && Integer.parseInt(ticketLimit.getConfigValue()) <= count){
                logger.info("投票人数上限!",ticketLimit.getConfigValue());
                throw new BizException(VOTE_MAX_LIMIT);
            }
        }
    }
}
