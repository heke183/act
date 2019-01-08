package com.xianglin.act.biz.shared.Impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import com.sun.tools.doclets.internal.toolkit.util.Group;
import com.xianglin.act.biz.shared.ActGroupSharedService;
import com.xianglin.act.common.dal.enums.PrizeType;
import com.xianglin.act.common.dal.mappers.*;
import com.xianglin.act.common.dal.model.*;
import com.xianglin.act.common.service.integration.GoldcoinServiceClient;
import com.xianglin.act.common.util.*;
import com.xianglin.appserv.common.service.facade.app.PersonalService;
import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.enums.Constant;
import com.xianglin.appserv.common.service.facade.model.enums.GroupStatus;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import com.xianglin.cif.common.service.facade.CustomersInfoService;
import com.xianglin.cif.common.service.facade.model.CustomersDTO;
import com.xianglin.cif.common.service.facade.vo.GoldcoinRecordVo;
import com.xianglin.core.model.enums.Constants;
import com.xianglin.core.model.enums.GroupEnum;
import com.xianglin.core.model.enums.UserType;
import com.xianglin.core.model.vo.*;
import com.xianglin.core.service.PrizeAwardUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.joda.time.DateTime;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN;
import static com.xianglin.act.common.util.ActPreconditions.ResponseEnum.*;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ROUND_UNNECESSARY;
import static java.util.stream.Collectors.toList;

/**
 * Describe :
 * Created by xingyali on 2018/12/19 9:23.
 * Update reason :
 */                                                                       
@org.springframework.stereotype.Service("actGroupSharedService")
public class ActGroupSharedServiceImpl implements ActGroupSharedService {

    private static final Logger logger = LoggerFactory.getLogger(ActGroupSharedServiceImpl.class);

    //private static final Long sysPartyId = 10000L;

    private static final List<String> redPackageTypes = Arrays.asList(GroupEnum.GroupTipsType.WITHDRAW.name(), GroupEnum.GroupTipsType.EXCHANGE.name());

    private static final List<String> entityPrizeCodes = Arrays.asList(PrizeType.ENTITY.name());

    private static final List<String> ecPrizeCodes = Arrays.asList(PrizeType.EC_COUPON.name(), PrizeType.EC_PHONE_COUPON.name());

    @Autowired
    private ActGroupUserMapper actGroupUserMapper;

    @Autowired
    private ActGroupInfoMapper actGroupInfoMapper;

    @Autowired
    private ActGroupBalanceTranMapper actGroupBalanceTranMapper;

    @Autowired
    private ActGroupTipsMapper actGroupTipsMapper;

    @Autowired
    private ActGroupInfoDetailMapper actGroupInfoDetailMapper;

    @Autowired
    private PrizeV2Mapper prizeV2Mapper;

    @Autowired
    private PrizeMapper prizeMapper;

    @Autowired
    private CustomerAcquireContactinfoMapper customerAcquireContactinfoMapper;

    @Autowired
    private EcApis ecApis;

    @Autowired
    private PrizeAwardUtils prizeAwardUtils;

    @Autowired
    private PersonalService personalService;

    @Autowired
    private CustomerAcquireRecordMapper customerAcquireRecordMapper;


    @Autowired
    private CustomerPrizeMapper customerPrizeMapper;

    @Autowired
    private CustomersInfoService customersInfoService;

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private SequenceMapper sequenceMapper;

    @Autowired
    private GoldcoinServiceClient goldcoinServiceClient;

    /**
     * 活动详情页
     *
     * @param currentPartyId
     * @return
     */
    @Override
    public ActGroupInfoVo queryGroupDetail(Long currentPartyId) {
        ActGroupInfoVo actGroupInfoVo = null;
        //查询当前用户是否加入过团
        Example example = new Example(ActGroupInfo.class);
        example.orderBy("createTime").desc();
        example.and().andEqualTo("partyId", currentPartyId).andEqualTo("isDeleted", Constant.Delete_Y_N.N.name());
        List<ActGroupInfo> actGroupInfos = actGroupInfoMapper.selectByExampleAndRowBounds(example, new RowBounds(0, 1));
        if (actGroupInfos.size() == 0) {//没有加入过，创建一个团
            initUserInfo(currentPartyId);
            actGroupInfoVo = createGroupInfo(currentPartyId);
            return actGroupInfoVo;
        }
        //加入过，返回最近的一个团    //查询团成员  
        actGroupInfoVo = queryActGroupInfo(currentPartyId, actGroupInfos.get(0));
        return actGroupInfoVo;
    }

    /**
     * @param partyId      当前登录用户
     * @param actGroupInfo 团
     * @return
     */
    private ActGroupInfoVo queryActGroupInfo(Long partyId, ActGroupInfo actGroupInfo) {
        actGroupInfo = actGroupInfoMapper.selectByPrimaryKey(actGroupInfo.getId());
        if(actGroupInfo == null){
            return new ActGroupInfoVo();
        }
        ActGroupInfoVo actGroupInfoVo = null;
        List<ActGroupInfoDetail> select = actGroupInfoDetailMapper.select(ActGroupInfoDetail.builder().infoId(actGroupInfo.getId()).isDeleted(Constant.Delete_Y_N.N.name()).build());
        //计算失效时间
        long expireTime = actGroupInfo.getExpireTime().getTime() - System.currentTimeMillis();
        if (expireTime < 0) {
            expireTime = 0;
        }
        logger.info("当前登录用户的partyId:"+partyId+" 失效时间为:"+expireTime);
        actGroupInfoVo = ActGroupInfoVo.builder().id(actGroupInfo.getId()).partyId(actGroupInfo.getPartyId()).expireTime(TimeUnit.MILLISECONDS.toSeconds(expireTime)).style(actGroupInfo.getStyle()).totalBalance(actGroupInfo.getTotalBalance().toString()).lackNumber(3 - select.size()).status(actGroupInfo.getStatus()).build();
        //查询团是否为进行中并且判断是否已经失效
        Boolean isFlag = isExpireTime(actGroupInfo);
        if (isFlag) {
            actGroupInfoVo.setStatus(GroupEnum.GroupInfoStatus.F.name());
            actGroupInfoVo.setExpireTime(0L);
        }
        logger.info("当前登录用户的partyId:"+partyId+" 返回状态为:"+actGroupInfoVo.getStatus());
        //当前用户是否是团长
        if (partyId.equals(actGroupInfo.getPartyId())) {
            actGroupInfoVo.setIsManager("Y");
        } else {
            actGroupInfoVo.setIsManager("N");
        }
        //当前用户分得多少钱
        if (actGroupInfo.getStatus().equals(GroupEnum.GroupInfoStatus.S.name())) {
            ActGroupInfoDetail actGroupInfoDetail = actGroupInfoDetailMapper.selectOne(ActGroupInfoDetail.builder().partyId(partyId).infoId(actGroupInfo.getId()).build());
            if (actGroupInfoDetail != null) {
                actGroupInfoVo.setCuBalance(actGroupInfoDetail.getBalance().toString());
            }
        }
        actGroupInfoVo.setActGroupInfoDetailVoList(queryActGroupInfoDetailVo(actGroupInfo.getPartyId(), select));
        return actGroupInfoVo;
    }

    /**
     * 转换团成员
     *
     * @param currentPartyId
     * @param actGroupInfoDetails
     * @return
     */
    private List<ActGroupInfoDetailVo> queryActGroupInfoDetailVo(Long currentPartyId, List<ActGroupInfoDetail> actGroupInfoDetails) {
        List<ActGroupInfoDetailVo> actGroupInfoDetailVos = actGroupInfoDetails.stream().map(vo -> {
            ActGroupInfoDetailVo actGroupInfoDetailVo = null;
            try {
                actGroupInfoDetailVo = DTOUtils.map(vo, ActGroupInfoDetailVo.class);
                ActGroupUser actGroupUser = actGroupUserMapper.selectOne(ActGroupUser.builder().partyId(actGroupInfoDetailVo.getPartyId()).build());
                if (actGroupUser != null) {
                    actGroupInfoDetailVo.setHeadImg(actGroupUser.getHeadImg());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (currentPartyId.equals(vo.getPartyId())) {
                actGroupInfoDetailVo.setType("Manager");
            } else {
                actGroupInfoDetailVo.setType("user");
            }
            return actGroupInfoDetailVo;
        }).collect(Collectors.toList());
        return actGroupInfoDetailVos;
    }


    //加入团
    private ActGroupInfoDetail joinGroupInfoDetail(ActGroupInfoDetail actGroupInfoDetail) {
        actGroupInfoDetailMapper.insertSelective(actGroupInfoDetail);
        return actGroupInfoDetail;
    }

    //创建团,并添加团成员
    private ActGroupInfoVo createGroupInfo(Long partyId) {
        int time = 4;
        String expire_time = configMapper.selectConfig("GROUP_EXPIRE_TIME");
        if(StringUtils.isNotEmpty(expire_time)){
            time = Integer.valueOf(expire_time); 
        }
        //查询系统用户的金额是否大于0，大于0才能开团
        ActGroupUser sysActGroupInfo = actGroupUserMapper.selectOne(ActGroupUser.builder().isDeleted(Constant.Delete_Y_N.N.name()).partyId(GroupEnum.GroupTranType.RAFFLE.getSysPartyId()).build());
        if (sysActGroupInfo != null && sysActGroupInfo.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException(GROUP_ACT_END);
        }
        //当前用户当天参团是否达到80
        Boolean aBoolean = queryGoupUpperLimit(partyId);
        if (aBoolean) {
            throw new BizException(GROUP_TODAY_UPPER_LIMIT);
        }
        ActGroupInfoVo actGroupInfoVo = null;
        //查询随机红包金额
        BigDecimal balance = queryRandomBalance();
        //开团
        ActGroupInfo actGroupInfo = ActGroupInfo.builder().partyId(partyId).style(GroupEnum.GroupInfoStyle.GXFC.name()).totalBalance(balance).status(GroupEnum.GroupInfoStatus.I.name()).expireTime(DateTime.now().plusHours(time).toDate()).partner(partyId + "").build();
        actGroupInfoMapper.insertSelective(actGroupInfo);
        //加入团
        ActGroupInfoDetail actGroupInfoDetail = joinGroupInfoDetail(ActGroupInfoDetail.builder().infoId(actGroupInfo.getId()).partyId(partyId).balance(BigDecimal.ZERO).status(GroupEnum.GroupInfoDetailStatus.I.name()).build());
        //返回团信息
        actGroupInfoVo = queryActGroupInfo(partyId, actGroupInfo);
        //成员信息
        actGroupInfoVo.setActGroupInfoDetailVoList(queryActGroupInfoDetailVo(partyId, Arrays.asList(actGroupInfoDetail)));
        return actGroupInfoVo;
    }

    //计算团的随机红包
    private BigDecimal queryRandomBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        int i = (int) (Math.random() * 100 + 1);
        String group_random_balance = configMapper.selectConfig("GROUP_RANDOM_BALANCE");
        Map map = JSON.parseObject(group_random_balance);
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (i <= Integer.valueOf(entry.getKey())) {
                logger.info("queryRandomBalance:"+i+" key= " + entry.getKey() + " and value= " + entry.getValue());
                balance = new BigDecimal(entry.getValue());
            }
        }
        return balance;
    }

    /**
     * 加入团
     *
     * @param partyId
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public ActGroupInfoVo joinGroup(Long partyId, Long currentPartyId) {
        logger.info("joinGroup start:团长的partyID：" + partyId+"当前登录用户的partyID："+currentPartyId );
        ActGroupInfoVo actGroupInfoVo = null;
        Example exampleUser = Example.builder(ActGroupUser.class)
                .where(Sqls.custom().andEqualTo("partyId", partyId)
                        .andEqualTo("isDeleted", Constant.Delete_Y_N.N.name())).forUpdate(). build();
        List<ActGroupUser> actGroupUsers = actGroupUserMapper.selectByExample(exampleUser);
        if(CollectionUtils.isEmpty(actGroupUsers)){
            throw new BizException(GROUP_USER_NOT_EXIST);
        }
        initUserInfo(currentPartyId);
        ActGroupInfo actGroupInfo = queryUserNewGroupInfo(partyId);
        if (actGroupInfo == null) {
            throw new BizException(GROUP_ERROR);
        }
        
        //查询团是否在进行中，如果在进行中才能加入团  
        if (actGroupInfo.getStatus().equals(GroupEnum.GroupInfoStatus.S.name()) || actGroupInfo.getStatus().equals(GroupEnum.GroupInfoStatus.F.name())) {
            actGroupInfoVo = queryActGroupInfo(currentPartyId, actGroupInfo);
            if(actGroupInfo.getStatus().equals(GroupEnum.GroupInfoStatus.S.name()) && (actGroupInfoVo.getCuBalance() == null || new BigDecimal(actGroupInfoVo.getCuBalance()).compareTo(BigDecimal.ZERO)<=0)){
                actGroupInfoVo.setStatus(GroupEnum.UserJoinGroupStatus.FULL.name());
            }
            return actGroupInfoVo;
        }

            //判断团时间是否失效  人未满 组团失败
            Boolean expireTime = isExpireTime(actGroupInfo);
            if (expireTime) {
                actGroupInfoVo = queryActGroupInfo(partyId, actGroupInfo);
                return actGroupInfoVo;
            }

            //判断用户是否已经加入过这个团  用户已经在这个团了
            ActGroupInfoDetail actGroupInfoDetail = actGroupInfoDetailMapper.selectOne(ActGroupInfoDetail.builder().infoId(actGroupInfo.getId()).partyId(currentPartyId).isDeleted(Constant.Delete_Y_N.N.name()).build());
            if (actGroupInfoDetail != null) {
                return queryActGroupInfo(currentPartyId, actGroupInfo);
            }

            //团里面最后一个用户参团，查询是否有新用户，如果有就能成团，没有就判断是否是新用户
            List<ActGroupInfoDetail> groupInfoDetails = actGroupInfoDetailMapper.select(ActGroupInfoDetail.builder().infoId(actGroupInfo.getId()).build());
            if (groupInfoDetails.size() == 2) {
                AtomicReference<Boolean> isNewUser = new AtomicReference<>(false);
                groupInfoDetails.stream().forEach(vo -> {
                    Response<UserVo> userVoResponse = personalService.queryUser(vo.getPartyId());
                    Boolean falg = isGroupNewUser(vo.getPartyId(),actGroupInfo.getId());
                    if ((userVoResponse == null || userVoResponse.getResult() == null) && falg) {
                        isNewUser.set(true);
                    }
                });

                if (!isNewUser.get()) {//没有新用户，查询新加的用户是否是新用户，不是不能加入
                    //查询用户是否参过团或开过团
                    Boolean falg = isGroupNewUser(currentPartyId,actGroupInfo.getId());
                    Response<UserVo> userVoResponse = personalService.queryUser(currentPartyId);
                    if (!falg || userVoResponse.getResult() != null) { //不是新用户 没有新用户不能成团
                       actGroupInfoVo = queryActGroupInfo(currentPartyId, actGroupInfo);
                        actGroupInfoVo.setStatus(GroupEnum.UserJoinGroupStatus.NEW.name());
                        return actGroupInfoVo;
                    }
                }
            }

            //查询是否有用户参与的团
            List<ActGroupInfoDetail> select = actGroupInfoDetailMapper.select(ActGroupInfoDetail.builder().partyId(currentPartyId).status(GroupEnum.GroupInfoStatus.I.name()).isDeleted(Constant.Delete_Y_N.N.name()).build());
            List<ActGroupInfoDetail> infoDetails = new ArrayList<>();
            select.stream().forEach(vo -> {
                ActGroupInfo actGroupInfo1 = actGroupInfoMapper.selectOne(ActGroupInfo.builder().id(vo.getInfoId()).isDeleted(Constant.Delete_Y_N.N.name()).build());
                if (actGroupInfo1 != null && !actGroupInfo1.getPartyId().equals(currentPartyId)) {
                    infoDetails.add(vo);
                }
            });

            if (infoDetails.size() > 0) {
                //查询团是否已经失效，已经失效就修改成失效的状态
                ActGroupInfo actGroupInfo2 = actGroupInfoMapper.selectOne(ActGroupInfo.builder().isDeleted(Constant.Delete_Y_N.N.name()).id(select.get(0).getInfoId()).build());
                if(actGroupInfo2 != null){
                    Boolean isFlag = isExpireTime(actGroupInfo2);
                    if (!isFlag) {//已经有参与的团了
                        actGroupInfoVo = queryActGroupInfo(currentPartyId, actGroupInfo);
                        actGroupInfoVo.setStatus(GroupEnum.UserJoinGroupStatus.JOIN.name());
                        return actGroupInfoVo;
                    }
                }
            }


            //一个用户一天成团上限80次
            Boolean isLimit = queryGoupUpperLimit(currentPartyId);
            if(isLimit){
                actGroupInfoVo = queryActGroupInfo(currentPartyId,actGroupInfo);
                actGroupInfoVo.setStatus(GroupEnum.UserJoinGroupStatus.END.name());
                return actGroupInfoVo;
            }

            //判断是否两两成团过
            String partener = currentPartyId + "";
            for (ActGroupInfoDetail groupInfoDetail : groupInfoDetails) {
                String partyIds = currentPartyId + "," + groupInfoDetail.getPartyId();
                partener = partener + "," + groupInfoDetail.getPartyId();
                //排序
                partyIds = sortPartener(partyIds).replace(",", "%");
                partener = sortPartener(partener);
                Example example = new Example(ActGroupInfo.class);
                example.and().andLike("partner", "%" + partyIds + "%").andEqualTo("isDeleted", Constant.Delete_Y_N.N.name()).andNotEqualTo("status", GroupEnum.GroupInfoStatus.F.name());
                List<ActGroupInfo> actGroupInfos = actGroupInfoMapper.selectByExample(example);
                if (actGroupInfos.size() > 0) {
                    actGroupInfoVo = queryActGroupInfo(currentPartyId, actGroupInfo);
                    actGroupInfoVo.setStatus(GroupEnum.UserJoinGroupStatus.ONE.name());
                    return actGroupInfoVo;
                }
            }

            //用户加入团
            ActGroupInfoDetail build = ActGroupInfoDetail.builder().infoId(actGroupInfo.getId()).balance(BigDecimal.ZERO).partyId(currentPartyId).status(GroupEnum.GroupInfoDetailStatus.I.name()).build();
            joinGroupInfoDetail(build);

            //修改链接partyID
            actGroupInfo.setPartner(partener);
            actGroupInfo.setUpdateTime(new Date());
            actGroupInfo.setVersion(actGroupInfo.getVersion());
            actGroupInfoMapper.updateByPrimaryKeySelective(actGroupInfo);

            //查询团成员是否为3人
            if (groupInfoDetails.size() == 2) {  //发放奖励
                //修改群状态
                ActGroupInfo actGroupInfo1 = actGroupInfoMapper.selectByPrimaryKey(actGroupInfo.getId());
                actGroupInfo1.setStatus(GroupEnum.GroupInfoStatus.S.name());
                actGroupInfo1.setUpdateTime(new Date());
                actGroupInfo1.setVersion(actGroupInfo1.getVersion());
                actGroupInfoMapper.updateByPrimaryKeySelective(actGroupInfo1);
                successGroup(actGroupInfo1, groupInfoDetails, build);
            }
            //返回团信息
        actGroupInfoVo = queryActGroupInfo(currentPartyId, actGroupInfo);
        logger.info("joinGroup end:团长的partyID：" + partyId+"当前登录用户的partyID："+currentPartyId +"返回群的状态"+ actGroupInfoVo.getStatus());
        return actGroupInfoVo;
    }

    private Boolean isGroupNewUser(Long partyId,Long groupId) {
        Boolean flag = false;
        
        //将该用户失效的团改为失效状态
        Example example = new Example(ActGroupInfoDetail.class);
        example.and().andEqualTo("isDeleted", Constant.Delete_Y_N.N.name()).andEqualTo("status", GroupEnum.GroupInfoStatus.I.name()).andNotEqualTo("infoId",groupId).andEqualTo("partyId",partyId);
        List<ActGroupInfoDetail> actGroupInfoDetails = actGroupInfoDetailMapper.selectByExample(example);
        actGroupInfoDetails.stream().forEach(vo->{
           ActGroupInfo actGroupInfo = actGroupInfoMapper.selectByPrimaryKey(vo.getInfoId());
           if(actGroupInfo != null){
               isExpireTime(actGroupInfo);
           }
        });
        example.clear();
        //查询是否有参与的团状态为成功或进行中的
        example.and().andEqualTo("isDeleted", Constant.Delete_Y_N.N.name()).andNotEqualTo("status",GroupEnum.GroupInfoStatus.F.name()).andNotEqualTo("infoId",groupId).andEqualTo("partyId",partyId);
        List<ActGroupInfoDetail> details = actGroupInfoDetailMapper.selectByExample(example);
        if(details.size()==0){
            return true;
        }
        /*if(details.size()==1){ //是否自己是组的团 
            ActGroupInfo actGroupInfo = actGroupInfoMapper.selectByPrimaryKey(details.get(0).getInfoId());
            if(actGroupInfo != null && actGroupInfo.getPartyId().equals(actGroupInfo.getPartyId())){
              return true;  
            }
        }*/
        return flag;
    }

    /**
     * 成团发红包
     *
     * @param actGroupInfo
     * @param groupInfoDetails
     * @param actGroupInfoDetail
     */
    private void successGroup(ActGroupInfo actGroupInfo, List<ActGroupInfoDetail> groupInfoDetails, ActGroupInfoDetail actGroupInfoDetail) {
        //总红包数
        AtomicReference<BigDecimal> atomicDouble = new AtomicReference<>(new BigDecimal(actGroupInfo.getTotalBalance().intValue()));
        //总人数
        AtomicReference<BigDecimal> remainSize = new AtomicReference<>(new BigDecimal(3));
        //交易号
        String tranId = getTranId();
        //给用户随机发放红包
        groupInfoDetails.stream().forEach(vo -> {
            //计算红包
            BigDecimal balance = queryUserRandomBalance(atomicDouble.get().doubleValue());
            getRandMoney(remainSize.get().doubleValue(),balance.doubleValue());
            //发红包和提示消息
            sendRedPacket(actGroupInfo, vo, tranId, balance);
            //总红包数减去当前用户的红包
            atomicDouble.set(atomicDouble.get().subtract(balance));
            //总人数减1
            remainSize.set(atomicDouble.get().subtract(new BigDecimal("1")));
        });
        //最后一个用户发用户红包和提示消息
        actGroupInfoDetail = actGroupInfoDetailMapper.selectByPrimaryKey(actGroupInfoDetail.getId());
        sendRedPacket(actGroupInfo, actGroupInfoDetail, tranId, atomicDouble.get());
        
    }

    /**
     * partyID排序
     * @param partner
     * @return
     */
    private String sortPartener(String partner) {
        String[] str1 = partner.split(",");
        Long[] str2 = new Long[str1.length];
        for (int i = 0; i < str1.length; i++) {
            str2[i] = Long.valueOf(str1[i]);
        }
        Arrays.sort(str2);
        partner = StringUtils.join(str2, ",");
        return partner;
    }

    private Boolean queryGoupUpperLimit(Long currentPartyId) {
        Example example = new Example(ActGroupInfoDetail.class);
        example.and().andEqualTo("isDeleted", Constant.Delete_Y_N.N.name()).andEqualTo("partyId", currentPartyId).andEqualTo("status",GroupEnum.GroupInfoStatus.S.name());
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00";
        example.and().andGreaterThanOrEqualTo("createTime", today);
        int i = actGroupInfoDetailMapper.selectCountByExample(example);
        String group_limit = configMapper.selectConfig("GROUP_LIMIT");
        if (i >= Integer.valueOf(group_limit)) { //toast提示：你今天红包已领完！欢迎明天再来！
            return true;
        }
        return false;
    }

    /**
     * 判断团是否失效，将失效的团和团员状态修改为失效，并发提示消息
     *
     * @param actGroupInfo
     * @return
     */
    private Boolean isExpireTime(ActGroupInfo actGroupInfo) {
        Boolean flag = false;
        Date date = DateUtils.skipDateTime(actGroupInfo.getExpireTime(), Calendar.SECOND, 3);
        if (actGroupInfo.getStatus().equals(GroupEnum.GroupInfoStatus.I.name()) && date.compareTo(new Date()) <= 0) {  //已过期
            actGroupInfo.setStatus(GroupEnum.GroupInfoStatus.F.name());
            actGroupInfo.setUpdateTime(new Date());
            actGroupInfoMapper.updateByPrimaryKeySelective(actGroupInfo);
            ActGroupUser actGroupUser = actGroupUserMapper.selectOne(ActGroupUser.builder().partyId(actGroupInfo.getPartyId()).build());
            //查询群成员，并将群成员修改为失效状态
            List<ActGroupInfoDetail> select = actGroupInfoDetailMapper.select(ActGroupInfoDetail.builder().infoId(actGroupInfo.getId()).isDeleted(Constant.Delete_Y_N.N.name()).build());
            select.stream().forEach(vo -> {
                String name = null;
                name = "**" + actGroupUser.getName().substring(actGroupUser.getName().length() - 2);
                vo.setStatus(GroupEnum.GroupInfoStatus.F.name());
                vo.setUpdateTime(new Date());
                actGroupInfoDetailMapper.updateByPrimaryKeySelective(vo);
                //发提示消息
                if (actGroupInfo.getPartyId().equals(vo.getPartyId())) {//你发红包组团失败
                    actGroupTipsMapper.insertSelective(ActGroupTips.builder().partyId(vo.getPartyId()).type(GroupEnum.GroupTipsType.FAIL.name()).tips("你发红包组团失败").build());

                } else { //您参与的**的团失败
                    actGroupTipsMapper.insertSelective(ActGroupTips.builder().partyId(vo.getPartyId()).type(GroupEnum.GroupTipsType.FAIL.name()).tips("你参与的" + name + "的团失败").build());
                }
            });
            return true;
        }
        return flag;
    }

    /**
     * 发红包
     *
     * @param vo
     * @param tranId
     * @param balance
     */
    private void sendRedPacket(ActGroupInfo actGroupInfo, ActGroupInfoDetail vo, String tranId, BigDecimal balance) {
        //发红包,修改团成员状态
        vo.setStatus(GroupEnum.GroupInfoDetailStatus.S.name());
        vo.setBalance(balance);
        vo.setUpdateTime(new Date());
        actGroupInfoDetailMapper.updateByPrimaryKeySelective(vo);
        actGroupBalanceTranMapper.insertSelective(ActGroupBalanceTran.builder().partyId(vo.getPartyId()).changeValue(balance).tranId(tranId).remark(GroupEnum.GroupTipsType.SENDREDPACKET.desc).type(GroupEnum.GroupTipsType.SENDREDPACKET.name()).status(GroupEnum.GroupInfoStatus.S.name()).build());
        actGroupBalanceTranMapper.insertSelective(ActGroupBalanceTran.builder().partyId(GroupEnum.GroupTranType.RAFFLE.getSysPartyId()).remark(GroupEnum.GroupTranType.RAFFLE.getDesc()).changeValue(balance.negate()).tranId(tranId).type(GroupEnum.GroupTipsType.SENDREDPACKET.name()).status(GroupEnum.GroupInfoStatus.S.name()).build());
        //调整用户的账户余额
        ActGroupUser n = actGroupUserMapper.selectOne(ActGroupUser.builder().partyId(vo.getPartyId()).isDeleted(Constant.Delete_Y_N.N.name()).build());
        if(n == null){
            throw new BizException(ERROR);
        }
        n.setBalance(n.getBalance().add(balance));
        n.setUpdateTime(new Date());
        actGroupUserMapper.updateByPrimaryKeySelective(n);
        ActGroupUser sysActGroupUser = actGroupUserMapper.selectOne(ActGroupUser.builder().partyId(GroupEnum.GroupTranType.RAFFLE.getSysPartyId()).isDeleted(Constant.Delete_Y_N.N.name()).build());
        sysActGroupUser.setBalance(sysActGroupUser.getBalance().subtract(balance).setScale(2, BigDecimal.ROUND_HALF_UP));
        sysActGroupUser.setUpdateTime(new Date());                                                  
        actGroupUserMapper.updateByPrimaryKeySelective(sysActGroupUser);

        //得到用户的昵称
        String name = null;
        name = "**" + n.getName().substring(n.getName().length() - 2);
        //给团长发消息
        ActGroupUser actGroupUser = actGroupUserMapper.selectOne(ActGroupUser.builder().partyId(actGroupInfo.getPartyId()).isDeleted(Constant.Delete_Y_N.N.name()).build());
        //发消息
        actGroupTipsMapper.insertSelective(ActGroupTips.builder().partyId(vo.getPartyId()).tips(name + "获得" + balance + "元").changeValue(balance).type(GroupEnum.GroupTipsType.SENDREDPACKET.name()).build());
        if (actGroupInfo.getPartyId().equals(vo.getPartyId())) {  //团长 ，你发的红包组团成功
            actGroupTipsMapper.insertSelective(ActGroupTips.builder().partyId(vo.getPartyId()).tips("你发的红包组团成功").type(GroupEnum.GroupTipsType.SUCCESS.name()).changeValue(balance).build());
        } else {//你领取了***的红包
            actGroupTipsMapper.insertSelective(ActGroupTips.builder().partyId(actGroupInfo.getPartyId()).tips(name+"-领取了你的红包").type(GroupEnum.GroupTipsType.GET.name()).changeValue(balance).build());
            actGroupTipsMapper.insertSelective(ActGroupTips.builder().partyId(vo.getPartyId()).tips("你领取了" + actGroupUser.getName() + "的红包").type(GroupEnum.GroupTipsType.SUCCESS.name()).changeValue(balance).build());
        }
    }

    /**
     * 发用户的随机红包
     *
     * @param i
     * @return
     */
    private BigDecimal queryUserRandomBalance(double i) {
        return BigDecimal.valueOf((Math.random() * i-1)+1).setScale(2, BigDecimal.ROUND_HALF_UP);
        
        
        
    }

    /**
     * 发用户的随机红包
     *
     * @param 
     * @return
     */
    private BigDecimal getRandMoney(double remainSize,double remainMoney) {
        //remainSize 剩余红包数量   
        //remainMoney 剩余红包的钱
        if(remainSize == 1){
            return BigDecimal.valueOf(remainMoney).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        Random r = new Random();
        double min   = 0.01;
        double max = remainMoney/remainSize*2;
        double money = r.nextDouble() * max;
        money = money <= min ? 0.01: money;
        money = Math.floor(money * 100) / 100;
        remainSize--;
        remainMoney -= money;
        return BigDecimal.valueOf(remainMoney).setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    

    /**
     * 根据用户的partyID可查询用户参与的团
     *
     * @param currentPartyId
     * @return
     */
    @Override
    public List<ActGroupInfoVo> groupListByPartyId(Long currentPartyId) {
        List<ActGroupInfoDetail> groupInfoDetails = actGroupInfoDetailMapper.select(ActGroupInfoDetail.builder().partyId(currentPartyId).status(GroupEnum.GroupInfoStatus.I.name()).isDeleted(Constant.Delete_Y_N.N.name()).build());
        List<ActGroupInfoVo> actGroupInfos = groupInfoDetails.stream().map(vo -> {
            ActGroupInfo actGroupInfo = actGroupInfoMapper.selectOne(ActGroupInfo.builder().id(vo.getInfoId()).build());
            return queryActGroupInfo(actGroupInfo.getPartyId(), actGroupInfo);
        }).filter(vo -> !(vo.getExpireTime() > new Date().getTime())).collect(Collectors.toList());
        return actGroupInfos;
    }

    /**
     * 查询100条滚动消息
     *
     * @return
     */
    @Override
    public List<ActGroupTipsVo> queryScrollMessage() {
        Example example = new Example(ActGroupTips.class);
        example.and().andEqualTo("isDeleted", "N").andEqualTo("type", GroupEnum.GroupTipsType.SENDREDPACKET.name());
        example.orderBy("createTime").desc();
        List<ActGroupTips> actGroupTips = actGroupTipsMapper.selectByExampleAndRowBounds(example, new RowBounds(0, 100));
        List<ActGroupTipsVo> actGroupTipsVos = actGroupTips.stream().map(vo -> {
            ActGroupTipsVo actGroupTipsVo = null;
            try {
                actGroupTipsVo = DTOUtils.map(vo, ActGroupTipsVo.class);
            } catch (Exception e) {
                logger.info("conver ActGroupTipsVo:", e);
            }
            return actGroupTipsVo;
        }).collect(Collectors.toList());
        return actGroupTipsVos;
    }

    /**
     * 个人团信息分享查询
     *
     * @param partyId
     * @return
     */
    @Override
    public ActGroupInfoVo groupDetailShare(Long partyId) {
        ActGroupInfo actGroupInfo = queryUserNewGroupInfo(partyId);
        ActGroupInfoVo actGroupInfoVo = queryActGroupInfo(partyId, actGroupInfo);
        return actGroupInfoVo;
    }

    /**
     * 查用户最新的一个团
     *
     * @param partyId
     * @return
     */
    private ActGroupInfo queryUserNewGroupInfo(Long partyId) {
        ActGroupInfo actGroupInfo = null;
        Example example = new Example(ActGroupInfo.class);
        example.and().andEqualTo("isDeleted", Constant.Delete_Y_N.N.name()).andEqualTo("partyId", partyId);
        example.orderBy("id").desc();
        List<ActGroupInfo> actGroupInfos = actGroupInfoMapper.selectByExample(example);
        if (actGroupInfos.size() > 0) {
            actGroupInfo = actGroupInfos.get(0);
        }
        return actGroupInfo;
    }


    /**
     * 分享信息查询
     *
     * @param currentPartyId
     * @return
     */
    @Override
    public ActGroupShareVo share(Long currentPartyId) {
        if(currentPartyId == null){
            throw new BizException(GROUP_NotPARTYID);
        }
        ActGroupUser actGroupUser = actGroupUserMapper.selectOne(ActGroupUser.builder().partyId(currentPartyId).build());
        if (actGroupUser == null) {
            throw new BizException(ERROR);
        }
        ActGroupShareVo actGroupShareVo = ActGroupShareVo.builder().content("送你一个现金红包，秒提现赶紧长按识别小程序领取吧!")
                .headImg(actGroupUser.getHeadImg())
                .qrCode(actGroupUser.getQrCode())
                .title("乡邻福利送")
                .posterUrl(actGroupUser.getPosterUrl())
                .programImg(configMapper.selectConfig("GROUP_PROGRAM_IMG"))
                .wxContent(actGroupUser.getName() + "喊你白拿钱，最高88元").build();
        return actGroupShareVo;
    }

    @Override
    public List<PrizeVo> queryPrizeListByActivityCode(String activityCode) {
        Example example = Example.builder(PrizeV2.class).andWhere(Sqls.custom().andEqualTo("activityCode", activityCode).andEqualTo("isDeleted", Constant.Delete_1_0.N.code)).build();
        List<PrizeV2> prizeV2s = prizeV2Mapper.selectByExample(example);
        List<PrizeVo> prizeVoList = Convert.toList(PrizeVo.class, prizeV2s);
        return prizeVoList;
    }

    @Override
    public String exchangePrize(Long partyId, String prizeCode, String activityCode) {

        ActGroupUser actGroupUser = ActGroupUser.builder().partyId(partyId).build();
        try {
            //获取用户当前余额
            actGroupUser = actGroupUserMapper.selectOne(actGroupUser);
            //获取兑换礼品需要的金额
            Prize prize = prizeMapper.selectActivityPrize(activityCode, prizeCode);
            if (actGroupUser.getBalance().compareTo(prize.getUnitRmb()) == -1) {
                logger.info("用户当前余额", actGroupUser.getBalance());
                throw new BizException(GROUP_USER_BLANCE_NOT_ENOUGH);
            }
            //获取用户当天兑换次数
            Date start = DateUtil.beginOfDay(new Date());
            Example example = Example.builder(CustomerAcquire.class).andWhere(Sqls.custom().andEqualTo("partyId", partyId).andEqualTo("isDeleted", Constant.Delete_1_0.N.code).andGreaterThanOrEqualTo("createDate", DateUtil.format(start,NORM_DATETIME_PATTERN))).build();
            int count = customerAcquireRecordMapper.selectCountByExample(example);
            //获取兑换次数上限设置
            String limit = configMapper.selectConfig("GROUP_EXCHANGE_LIMIT");
            if (count >= Integer.valueOf(limit)) {
                logger.info("用户今日兑换次数", count);
                throw new BizException(GROUP_EXCHANGE_LIMIT);
            }

            //实物不需要实名认证，若兑换礼品为实物，需要和地址一起提交
            if (prize.getPrizeType().equals(PrizeType.ENTITY.name())) {
                return actGroupUser.getMobilePhone();
            }
            //保存金额变动记录
            saveDetail(actGroupUser, prize, activityCode);
        } catch (BizException e) {
            logger.warn("exchange error ", e);
            if (e.getResponseEnum().code.equals(GROUP_USER_BLANCE_NOT_ENOUGH.code)) {
                throw new BizException(GROUP_USER_BLANCE_NOT_ENOUGH);
            }
            if (e.getResponseEnum().code.equals(GROUP_EXCHANGE_LIMIT.code)) {
                throw new BizException(GROUP_EXCHANGE_LIMIT);
            }
        } catch (Exception e) {
            logger.warn("exchange failed ", partyId);
        }
        return actGroupUser.getMobilePhone();
    }

    @Override
    public boolean commitAddress(ContactInfoVO contactInfoVO) {
        boolean flag = false;
        try {
            //获取当前用户信息
            ActGroupUser actGroupUser = ActGroupUser.builder().partyId(contactInfoVO.getPartyId()).build();
            actGroupUser = actGroupUserMapper.selectOne(actGroupUser);
            //获取兑换礼品信息
            Prize prize = prizeMapper.selectActivityPrize(contactInfoVO.getActivityCode(), contactInfoVO.getPrizeCode());

            if (actGroupUser.getBalance().compareTo(prize.getUnitRmb()) == -1) {
                logger.info("commitAddress error balance = {}", actGroupUser.getBalance());
                throw new BizException(GROUP_USER_BLANCE_NOT_ENOUGH);
            }


            //获取用户当天兑换次数
            Example exa = Example.builder(CustomerAcquire.class).andWhere(Sqls.custom().andEqualTo("partyId", contactInfoVO.getPartyId()).andEqualTo("isDeleted", Constant.Delete_1_0.N.name()).andGreaterThanOrEqualTo("createDate", DateUtil.beginOfDay(new Date()))).build();
            int count = customerAcquireRecordMapper.selectCountByExample(exa);
            //获取兑换次数上限设置
            String limit = configMapper.selectConfig("GROUP_EXCHANGE_LIMIT");
            if (count >= Integer.valueOf(limit)) {
                logger.info("用户今日兑换次数", count);
                throw new BizException(GROUP_EXCHANGE_LIMIT);
            }

            //获取当前用户地址信息
            Example example = Example.builder(ContactInfo.class).andWhere(Sqls.custom().andEqualTo("partyId", contactInfoVO.getPartyId()).andEqualTo("isDeleted", Constant.Delete_1_0.N.code)).build();
            ContactInfo contactInfo = customerAcquireContactinfoMapper.selectOneByExample(example);

            if (contactInfo != null) {
                contactInfo.setUpdateDate(LocalDateTime.now());
                contactInfo.setName(contactInfoVO.getName());
                contactInfo.setAddress(contactInfoVO.getAddress());
                contactInfo.setMobilePhone(contactInfoVO.getMobilePhone());
                contactInfo.setPartyId(contactInfoVO.getPartyId());
                contactInfo.setCreator(String.valueOf(contactInfoVO.getPartyId()));
                contactInfo.setUpdater(String.valueOf(contactInfoVO.getPartyId()));
                customerAcquireContactinfoMapper.updateByPrimaryKeySelective(contactInfo);
            } else {
                contactInfo = new ContactInfo();
                contactInfo.initDateOfInsert();
                contactInfo.setName(contactInfoVO.getName());
                contactInfo.setAddress(contactInfoVO.getAddress());
                contactInfo.setMobilePhone(contactInfoVO.getMobilePhone());
                contactInfo.setPartyId(contactInfoVO.getPartyId());
                contactInfo.setCreator(String.valueOf(contactInfoVO.getPartyId()));
                contactInfo.setUpdater(String.valueOf(contactInfoVO.getPartyId()));
                customerAcquireContactinfoMapper.insertSelective(contactInfo);
            }
            saveDetail(actGroupUser, prize, contactInfoVO.getActivityCode());
            flag = true;
        } catch (Exception e) {
            logger.warn("commitAddress error", e);
        }
        return flag;
    }

    @Override
    public boolean withDraw(Long partyId) {
        //获取当前用户信息
        Example example = Example.builder(ActGroupUser.class).andWhere(Sqls.custom().andEqualTo("partyId", partyId).andEqualTo("isDeleted", Constant.Delete_Y_N.N.name())).build();
        ActGroupUser actGroupUser = actGroupUserMapper.selectOneByExample(example);
        if (actGroupUser.getBalance().compareTo(new BigDecimal(0)) == -1) {
            logger.warn("withDraw error ! balance = {}", actGroupUser.getBalance());
            throw new BizException(GROUP_NO_BALANCE);
        }

        //获取最低提现额度
        String draw = configMapper.selectConfig("GROUP_WITHDRAW_MIN");
        if (actGroupUser.getBalance().compareTo(new BigDecimal(draw)) == -1) {
            logger.warn("withDraw error balance = {}", actGroupUser.getBalance());
            throw new BizException(GROUP_BALANCE_NOT_ENOUGH);
        }

        com.xianglin.cif.common.service.facade.model.Response<GoldcoinRecordVo> goldcoinRecordVoResponse =
                goldcoinServiceClient.doRecord(GoldcoinRecordVo.builder()
                        .system("act")
                        .amount(actGroupUser.getBalance().intValue() * 1000)
                        .fronPartyId(Constants.GOLD_SYS_ACCOUNT)
                        .toPartyId(partyId)
                        .remark("春节红包提现")
                        .type("ACT_GROUP")
                        .requestId(GoldSequenceUtil.getSequence(partyId, sequenceMapper
                                .getSequence())).build());
        if (!com.google.common.base.Objects.equal(goldcoinRecordVoResponse.getCode(), 1000)) {
            logger.error("添加金币失败！", com.alibaba.fastjson.JSON.toJSONString(goldcoinRecordVoResponse));
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }

        BigDecimal bal = actGroupUser.getBalance();
        actGroupUser.setBalance(new BigDecimal(0));
        actGroupUser.setUpdateTime(new Date());
        actGroupUserMapper.updateByPrimaryKeySelective(actGroupUser);
        //保存提现消息
        actGroupTipsMapper.insertSelective(ActGroupTips.builder().partyId(partyId).type(GroupEnum.GroupTipsType.WITHDRAW.name()).tips(GroupEnum.GroupTipsType.WITHDRAW.getDesc()).changeValue(actGroupUser.getBalance().negate()).build());
        //保存用户余额交易明细
        actGroupBalanceTranMapper.insertSelective(ActGroupBalanceTran.builder().tranId(getTranId()).partyId(partyId).changeValue(bal.negate()).type(GroupEnum.GroupTranType.WITHDRAW.name()).remark(GroupEnum.GroupTranType.WITHDRAW.getDesc()).build());
        //保存系统用户余额交易明细
        actGroupBalanceTranMapper.insertSelective(ActGroupBalanceTran.builder().tranId(getTranId()).partyId(GroupEnum.GroupTranType.WITHDRAW.getSysPartyId()).changeValue(bal).type(GroupEnum.GroupTranType.WITHDRAW.name()).remark(GroupEnum.GroupTranType.WITHDRAW.getDesc()).build());
        //更新系统用户余额
        ActGroupUser sys = actGroupUserMapper.selectOne(ActGroupUser.builder().partyId(GroupEnum.GroupTranType.WITHDRAW.getSysPartyId()).build());
        sys.setBalance(sys.getBalance().add(bal));
        actGroupUserMapper.updateByPrimaryKeySelective(sys);

        return Boolean.TRUE;
    }

    @Override
    public List<CustomerAcquireRecordVO> queryExchangeDetail(Long partyId, String activityCode, String type) {
        List<CustomerAcquireRecordVO> result = Lists.newArrayList();
        //查询实物订单
        if (type.equals(GroupEnum.GroupExchangeType.O.name())) {
            //查询实物的礼品code
            List<String> prizeCodes = queryPrizeCodes(type, activityCode);
            Example example = Example.builder(CustomerAcquire.class).andWhere(Sqls.custom().andEqualTo("partyId", partyId).andEqualTo("activityCode", activityCode).andEqualTo("isDeleted", Constant.Delete_1_0.N.code).andIn("prizeCode", prizeCodes)).build();
            List<CustomerAcquire> entitys = customerAcquireRecordMapper.selectByExample(example).stream().peek(v -> {
                if (StringUtils.isNotEmpty(v.getPrizeCode())) {
                    Example prize = Example.builder(PrizeV2.class).andWhere(Sqls.custom().andEqualTo("activityCode", activityCode).andEqualTo("prizeCode", v.getPrizeCode()).andEqualTo("isDeleted", Constant.Delete_1_0.N.code)).build();
                    PrizeV2 prizeV2 = prizeV2Mapper.selectOneByExample(prize);
                    v.setPrizeName(prizeV2.getPrizeName());
                    v.setPrizeImage(prizeV2.getPrizeImage());
                }
            }).collect(toList());
            result = Convert.toList(CustomerAcquireRecordVO.class, entitys);
        }
        //查询优惠券
        if (type.equals(GroupEnum.GroupExchangeType.E.name())) {
            List list = Lists.newArrayList();
            JSONArray array = EcApis.queryCouponList(partyId);
            logger.info("array = {}", array.toJSONString());
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                Example prize = Example.builder(PrizeV2.class).andWhere(Sqls.custom()
                        .andEqualTo("activityCode", activityCode)
                        .andEqualTo("couponName", object.getString("cpns_name")).andEqualTo("isDeleted", Constant.Delete_1_0.N.code)).build();
                PrizeV2 prizeV2 = prizeV2Mapper.selectOneByExample(prize);
                CustomerAcquireRecordVO customerAcquireRecordVO = CustomerAcquireRecordVO.builder()
                        .id(object.getLong("cpns_id"))
                        .prizeName(prizeV2.getPrizeName())
                        .toTime(object.getString("to_time"))
                        .fromTime(object.getString("from_time"))
                        .prizeImage(prizeV2.getPrizeImage())
                        .couponListUrl(EcApis.queryCouponListUrl())
                        .build();
                list.add(customerAcquireRecordVO);
            }
            result = list;
        }
        return result;
    }

    @Override
    public RedPackageVo queryRedPack(Long partyId, String type, String activityCode) {

        RedPackageVo redPackageVo = null;
        try {
            //获取红包累计金额
            Example balanceExample = Example.builder(ActGroupInfoDetail.class).andWhere(Sqls.custom()
                    .andEqualTo("partyId", partyId)
                    .andEqualTo("isDeleted", Constant.Delete_Y_N.N.name())).build();
            BigDecimal balanceSum = actGroupInfoDetailMapper.selectByExample(balanceExample).stream().map(ActGroupInfoDetail::getBalance).reduce(new BigDecimal(0), BigDecimal::add);

            //获取当前用户信息
            Example userBalance = Example.builder(ActGroupUser.class).andWhere(Sqls.custom()
                    .andEqualTo("partyId", partyId)
                    .andEqualTo("isDeleted", Constant.Delete_Y_N.N.name())).build();
            ActGroupUser actGroupUser = actGroupUserMapper.selectOneByExample(userBalance);

            //获取发放的红包数量
            Example actGroupInfo = Example.builder(ActGroupInfo.class).andWhere(Sqls.custom()
                    .andEqualTo("partyId", partyId)
                    .andEqualTo("status", GroupEnum.GroupInfoStatus.S.name())
                    .andEqualTo("isDeleted", Constant.Delete_Y_N.N.name())).build();
            int groupCount = actGroupInfoMapper.selectCountByExample(actGroupInfo);

            //收到的红包数量
            Example join = Example.builder(ActGroupInfoDetail.class).andWhere(Sqls.custom()
                    .andEqualTo("partyId", partyId)
                    .andEqualTo("status", GroupEnum.GroupInfoStatus.S.name())
                    .andEqualTo("isDeleted", Constant.Delete_Y_N.N.name())).build();
            int joinCount = actGroupInfoDetailMapper.selectCountByExample(join);

            //失效的红包数量
            Example loosed = Example.builder(ActGroupInfoDetail.class).andWhere(Sqls.custom()
                    .andEqualTo("partyId", partyId)
                    .andEqualTo("status", GroupEnum.GroupInfoStatus.F.name())
                    .andEqualTo("isDeleted", Constant.Delete_Y_N.N.name())).build();
            int loosedCount = actGroupInfoDetailMapper.selectCountByExample(loosed);

            //获取优惠券数量
            JSONArray result = EcApis.queryCouponList(partyId);
            logger.info("result ={}", result.toJSONString());
            //获取订单数量
            List<String> entityCodes = queryPrizeCodes(GroupEnum.GroupExchangeType.O.name(), activityCode);
            int entityCount = queryOrderCount(entityCodes, v -> {
                Example ec = Example.builder(CustomerAcquire.class).andWhere(Sqls.custom()
                        .andEqualTo("activityCode", activityCode)
                        .andIn("prizeCode", v)
                        .andEqualTo("partyId", partyId)
                        .andEqualTo("isDeleted", Constant.Delete_1_0.N.code)).build();
                int en = customerAcquireRecordMapper.selectCountByExample(ec);
                return en;
            });

            //获取提示消息列表
            List<ActGroupTipsVo> actGroupTipsVoList = queryGroupTipsByPartyId(partyId, type);
            redPackageVo = RedPackageVo.builder().balanceSum(NumberUtil.decimalFormat("0.00",balanceSum.doubleValue())).couponCount(result.size()).balance(NumberUtil.decimalFormat("0.00",actGroupUser.getBalance().doubleValue())).loosedCount(loosedCount).actGroupTipsVoList(actGroupTipsVoList).groupCount(groupCount).joinCount(joinCount).entityCount(entityCount).build();
        } catch (Exception e) {
            logger.warn("queryRedPack error", e);
        }
        return redPackageVo;
    }

    @Override
    public List<ActGroupTipsVo> queryGroupTipsByPartyId(Long partyId, String type) {
        List<ActGroupTips> actGroupTips = Lists.newArrayList();
        Example example = new Example(ActGroupTips.class);
        example.and().andEqualTo("partyId", partyId)
                .andEqualTo("isDeleted", Constant.Delete_Y_N.N.name());
        example.orderBy("createTime").desc();
        if (StringUtils.equals(type,GroupEnum.GroupExchangeType.R.name())){
            example.and().andNotIn("type",Arrays.asList(GroupEnum.GroupTipsType.GET.name(),GroupEnum.GroupTipsType.SENDREDPACKET.name()));
            actGroupTips = actGroupTipsMapper.selectByExampleAndRowBounds(example, new RowBounds(0, 10));
        }else if (StringUtils.isNotEmpty(type)) {
            example.and().andEqualTo("type", type);
            actGroupTips = actGroupTipsMapper.selectByExample(example);
        }
        List<ActGroupTipsVo> actGroupTipsVos = Convert.toList(ActGroupTipsVo.class, actGroupTips);
        return actGroupTipsVos;
    }

    private Integer queryOrderCount(List<String> prizeTypes, Function<List<String>, Integer> func) {
        return func.apply(prizeTypes);
    }

    private List<String> queryPrizeCodes(String type, String activityCode) {
        List<String> prizeCodes = Lists.newArrayList();
        if (type.equals(GroupEnum.GroupExchangeType.O.name())) {
            Example ec = Example.builder(PrizeV2.class).andWhere(Sqls.custom()
                    .andEqualTo("activityCode", activityCode)
                    .andIn("prizeType", entityPrizeCodes)
                    .andEqualTo("isDeleted", Constant.Delete_1_0.N.code)).build();
            prizeCodes = prizeV2Mapper.selectByExample(ec).stream().map(PrizeV2::getPrizeCode).collect(toList());
        }

        if (type.equals(GroupEnum.GroupExchangeType.E.name())) {
            Example ec = Example.builder(PrizeV2.class).andWhere(Sqls.custom()
                    .andEqualTo("activityCode", activityCode)
                    .andIn("prizeType", ecPrizeCodes)
                    .andEqualTo("isDeleted", Constant.Delete_1_0.N.code)).build();
            prizeCodes = prizeV2Mapper.selectByExample(ec).stream().map(PrizeV2::getPrizeCode).collect(toList());
        }
        return prizeCodes;
    }

    /**
     * 修改团样式
     *
     * @param id
     * @param style
     * @return
     */
    @Override
    public Boolean updateGroupStyle(Long partyId, Long id, String style) {
        ActGroupInfo actGroupInfo = actGroupInfoMapper.selectOne(ActGroupInfo.builder().id(id).isDeleted(Constant.Delete_Y_N.N.name()).build());
        if (actGroupInfo == null) {
            //异常，团不存在
            throw new BizException(GROUP_NOT_EXIST);
        }
        //不是团长
        if (!partyId.equals(actGroupInfo.getPartyId())) {
            throw new BizException(GROUP_NOT_Mangaer);
        }
        actGroupInfo.setStyle(style);
        actGroupInfo.setUpdateTime(new Date());
        return actGroupInfoMapper.updateByPrimaryKeySelective(actGroupInfo) == 1;
    }

    /**
     * 开团
     *
     * @param currentPartyId
     * @return
     */
    @Override
    public ActGroupInfoVo createGroup(Long currentPartyId) {
        ActGroupInfoVo groupInfo = null;
        //查询当前用户是否有在进行中的团
        Example example = new Example(ActGroupInfo.class);
        example.and().andEqualTo("isDeleted", Constant.Delete_Y_N.N.name()).andEqualTo("status", GroupEnum.GroupInfoStatus.I.name()).andEqualTo("partyId", currentPartyId);
        ActGroupInfo actGroupInfo = actGroupInfoMapper.selectOneByExample(example);
        if (actGroupInfo != null) {//判断这个进行中的团是否失效
            Boolean isFlag = isExpireTime(actGroupInfo);
            if (!isFlag) { //没有失效，返回这个团
                groupInfo = queryActGroupInfo(currentPartyId, actGroupInfo);
                return groupInfo;
            }
        }
        //开团
        groupInfo = createGroupInfo(currentPartyId);
        return groupInfo;
    }

    @Override
    public Long wxAppletLogin(Map<String, String> loginInfo) {
        /*
         loginInfo  mobilePhone,nickName,avatarUrl,openid,unionid
        1,是否已存在用户，若是则同步openId，unionId信息一致，若有变化则更新用户海报信息
        2，新用户则知己注册cif并同步相关信息，
         */
        AtomicLong result = new AtomicLong();
        ActGroupUser sysUser = actGroupUserMapper.selectOne(ActGroupUser.builder().mobilePhone(loginInfo.get("mobilePhone")).build());
        if (sysUser != null) {
            //判断用户openId和unionId
            Optional.ofNullable(loginInfo.get("unionid"))
                    .filter(v -> StringUtils.isBlank(sysUser.getUnionId()) || StringUtils.equals(v, sysUser.getUnionId()))
                    .orElseThrow(() -> new BizException("unionId 不一致，无法使用"));
            Optional.ofNullable(loginInfo.get("openid"))
                    .filter(v ->  StringUtils.isBlank(sysUser.getOpenId()) || StringUtils.equals(v, sysUser.getOpenId()))
                    .orElseThrow(() -> new BizException("openid 不一致，无法使用"));

            Optional.of(sysUser).filter(v -> !StringUtils.equalsIgnoreCase(v.getHeadImg(), loginInfo.get("nickName")) || !StringUtils.equalsIgnoreCase(v.getHeadImg(), loginInfo.get("avatarUrl")))
                    .ifPresent(v -> {
                        v.setOpenId(loginInfo.get("openid"));
                        v.setUnionId(loginInfo.get("unionid"));
                        v.setName(loginInfo.get("nickName"));
                        v.setHeadImg(loginInfo.get("avatarUrl"));
                        v.setUpdateTime(new Date());
                        actGroupUserMapper.updateByPrimaryKeySelective(v);
                        // 更新海报信息
                        CompletableFuture.runAsync(() -> {
                            initUserInfo(v.getPartyId());
                        });
                    });
            result.set(sysUser.getPartyId());
        } else {
            //新用户
            CustomersDTO customer = new CustomersDTO();
            customer.setMobilePhone(loginInfo.get("mobilePhone"));
            customer.setCreator(customer.getMobilePhone());
            customer = customersInfoService.openAccount(customer, "act-g").getResult();
            Optional.ofNullable(customer).ifPresent(v -> {
                ActGroupUser user = new ActGroupUser();
                user.setPartyId(v.getPartyId());
                user.setMobilePhone(v.getMobilePhone());
                user.setOpenId(loginInfo.get("openid"));
                user.setUnionId(loginInfo.get("unionid"));
                user.setName(loginInfo.get("nickName"));
                user.setHeadImg(loginInfo.get("avatarUrl"));
                user.setQrCode(QRUtils.qrCreate(configMapper.selectConfig("GROUP_USER_QR_URL") + "?partyId=" + user.getPartyId()));
                actGroupUserMapper.insertSelective(user);
                //更新海报信息
                CompletableFuture.runAsync(() -> {
                    initUserInfo(v.getPartyId());
                });
                result.set(user.getPartyId());
            });
        }
        return result.get();
    }

    /**
     * 初始化用户信息，基本信息，二维码，海报
     *
     * @param partyId
     */
    private void initUserInfo(Long partyId) {
        try {
            ActGroupUser sysUser = actGroupUserMapper.selectOne(ActGroupUser.builder().partyId(partyId).build());
            if (sysUser == null) {
                UserVo vo = personalService.queryUser(partyId).getResult();
                if (vo != null) {
                    sysUser = ActGroupUser.builder()
                            .version(1)
                            .partyId(partyId)
                            .mobilePhone(vo.getLoginName())
                            .name(vo.getShowName())
                            .qrCode(QRUtils.qrCreate(configMapper.selectConfig("GROUP_USER_QR_URL") + "?partyId=" + partyId))
                            .headImg(vo.getHeadImg()).build();
                    actGroupUserMapper.insertSelective(sysUser);
                }
            }
            Optional.ofNullable(sysUser).ifPresent(v -> {
                v.setPosterUrl(Html2ImageUtil.createImage(configMapper.selectConfig("GROUP_USER_POSTER_URL") + "?partyId=" + partyId));
                v.setUpdateTime(new Date());
                actGroupUserMapper.updateByPrimaryKeySelective(v);

            });
        } catch (Exception e) {
            logger.warn("初始化用户信息失败", e);
        }
    }

    @Override
    public Map<String, String> queryPeopleNum() {
        Map<String, String> result = Maps.newConcurrentMap();
        //拆红包人数
        String open1 = configMapper.selectConfig("GROUP_OPEN_PEOPLE");
        Example example = Example.builder(ActGroupBalanceTran.class).andWhere(Sqls.custom().andNotEqualTo("partyId",GroupEnum.GroupTranType.DISMANTLE.getSysPartyId()).andEqualTo("type",GroupEnum.GroupTranType.DISMANTLE.name()).andEqualTo("isDeleted",Constant.Delete_Y_N.N.name())).build();
        int openCount = actGroupBalanceTranMapper.selectCountByExample(example);
        String open = String.valueOf(Integer.valueOf(open1) + openCount);
        //兑换人数
        Example exchangeCout = Example.builder(ActGroupBalanceTran.class).andWhere(Sqls.custom().andNotEqualTo("partyId",GroupEnum.GroupTranType.EXCHANGE.getSysPartyId()).andEqualTo("type",GroupEnum.GroupTranType.EXCHANGE.name()).andEqualTo("isDeleted",Constant.Delete_Y_N.N.name())).build();
        int exCount = actGroupBalanceTranMapper.selectCountByExample(exchangeCout);
        String exNum = configMapper.selectConfig("GROUP_EXCHANGE_PEOPLE");
        String ex = String.valueOf(Integer.valueOf(exNum) + exCount);
        result.put("open", open);
        result.put("ex", ex);
        return result;
    }

    @Override
    public Map<String, BigDecimal> queryDismantleBalance(final Long partyId) {
        return CompletableFuture.supplyAsync(() -> {
            return actGroupUserMapper.selectOne(ActGroupUser.builder().partyId(partyId).build()).getBalance();
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            Example example = Example.builder(ActGroupBalanceTran.class)
                    .select("changeValue")
                    .where(Sqls.custom().andEqualTo("partyId", partyId).andEqualTo("type", GroupEnum.GroupTranType.DISMANTLE_IN.name())
                            .andEqualTo("isDeleted", Constant.Delete_Y_N.N.name())).build();
            return actGroupBalanceTranMapper.selectByExample(example).stream()
                    .reduce(BigDecimal.ZERO
                            , (v1, v2) -> v1.add(v2.getChangeValue())
                            , (r1, r2) -> r1.add(r2));
        }), (v1, v2) -> {
            return new HashMap<String, BigDecimal>() {{
                put("balance", v1);
                put("dismantleBalance", v2);
            }};
        }).join();
    }

    @Override
    public ActGroupUser queryGroupUser(Long partyId) {
        return Optional.ofNullable(actGroupUserMapper.selectOne(ActGroupUser.builder().partyId(partyId).build())).orElseThrow(() -> new BizException("用户不存在"));
    }

    @Override
    public BigDecimal dismantlePacket(Long currentPartyId, String code) {
        /*
        1,判断余额是否足够
        2，账户计算
        3，消息生成
         */
        GroupEnum.DismantlePacketType subType = Optional.ofNullable(GroupEnum.DismantlePacketType.valueOf(code)).orElseThrow(() -> new BizException("红包类型不存在"));
        ActGroupUser groupUser = queryGroupUser(currentPartyId);
        if (groupUser.getBalance().intValue() < subType.getValue()) {
            throw new BizException(GROUP_USER_BLANCE_NOT_ENOUGH);
        }
        BigDecimal changeValue = new BigDecimal(subType.getValue());
        String tranId = getTranId();
        changeValue(groupUser, GroupEnum.GroupTranType.DISMANTLE, tranId, changeValue.negate(), subType);
        //记录消息
        actGroupTipsMapper.insertSelective(ActGroupTips.builder().partyId(groupUser.getPartyId()).type(GroupEnum.GroupTipsType.RAFFLE.name())
                .tips(GroupEnum.GroupTipsType.RAFFLE.getDesc()).changeValue(changeValue.negate()).build());
        //判断奖金
        int count = actGroupBalanceTranMapper.selectCount(ActGroupBalanceTran.builder().type(GroupEnum.GroupTranType.DISMANTLE.name())
                .subType(subType.name()).changeValue(new BigDecimal(subType.getValue())).build());
        changeValue = new BigDecimal(subType.getBasicValue());
        if (count != 0) {
            changeValue = queryRandomPacket(subType).orElse(changeValue);
        }
        groupUser = queryGroupUser(currentPartyId);
        changeValue(groupUser, GroupEnum.GroupTranType.DISMANTLE_IN, tranId, changeValue, subType);
        actGroupTipsMapper.insertSelective(ActGroupTips.builder().partyId(groupUser.getPartyId()).type(GroupEnum.GroupTipsType.RAFFLESUCCEFUL.name())
                .tips(GroupEnum.GroupTipsType.RAFFLESUCCEFUL.getDesc()).changeValue(changeValue).build());
        return changeValue;
    }

    /**
     * 返回团主题样式
     * @param partyId
     * @return
     */
    @Override
    public String groupStyleByPartyId(Long partyId) {
        ActGroupInfo actGroupInfo = queryUserNewGroupInfo(partyId);
        if(actGroupInfo != null){
            return actGroupInfo.getStyle();
        }
        return null;
    }

    /**
     * 记录流水
     *
     * @param fromUser    用户
     * @param tranType    交易类型
     * @param tranId      交易号
     * @param changeValue 用户账户加减类型 大于0为加
     * @param subType     子类型
     */
    private void changeValue(ActGroupUser fromUser, GroupEnum.GroupTranType tranType, String tranId, BigDecimal changeValue, GroupEnum.DismantlePacketType subType) {

        actGroupUserMapper.updateByPrimaryKeySelective(ActGroupUser.builder().id(fromUser.getId()).version(fromUser.getVersion())
                .balance(fromUser.getBalance().add(changeValue)).updateTime(new Date()).build());
        actGroupBalanceTranMapper.insertSelective(ActGroupBalanceTran.builder().partyId(fromUser.getPartyId()).tranId(tranId)
                .changeValue(changeValue).type(tranType.name())
                .remark(tranType.getDesc()).status(GroupEnum.GroupInfoDetailStatus.S.name())
                .subType(Optional.ofNullable(subType).map(GroupEnum.DismantlePacketType::name).orElse("")).build());

        ActGroupUser sysUser = queryGroupUser(tranType.getSysPartyId());

        actGroupUserMapper.updateByPrimaryKeySelective(ActGroupUser.builder().id(sysUser.getId()).version(sysUser.getVersion())
                .balance(sysUser.getBalance().add(changeValue.negate())).updateTime(new Date()).build());
        actGroupBalanceTranMapper.insertSelective(ActGroupBalanceTran.builder().partyId(sysUser.getPartyId()).tranId(tranId)
                .changeValue(changeValue.negate()).type(tranType.name())
                .remark(tranType.getDesc()).status(GroupEnum.GroupInfoDetailStatus.S.name())
                .subType(Optional.ofNullable(subType).map(GroupEnum.DismantlePacketType::name).orElse("")).build());
    }

    /**查询随机奖励
     * @param subType
     * @return
     */
    private Optional<BigDecimal> queryRandomPacket(GroupEnum.DismantlePacketType subType) {
        List<CustomerPrize> prizes = customerPrizeMapper.selectCustomerPrize("GROUP_DISMANTLE", "GROUP_DISMANTLE_" + subType.name());
        if (CollectionUtils.isNotEmpty(prizes)) {
            BigDecimal random = new BigDecimal(ThreadLocalRandom.current().nextDouble());
            return prizes.stream().filter(v -> v.getInitialProbability().compareTo(random) < 0 && v.getProbability().compareTo(random) > 0)
                    .findFirst().map(v -> {
                        if(v.getMaxValue().equals(v.getMinValue())){
                            return v.getMinValue();
                        }else {
                            return new BigDecimal(ThreadLocalRandom.current().nextInt(v.getMinValue().intValue(),v.getMaxValue().intValue()));
                        }
                        });
        }else {
            return Optional.empty();
        }
    }

    /**
     * 生成订单号
     *
     * @return
     */
    private String getTranId() {
        return DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_FORMAT) + sequenceMapper.getSequence();
    }

    @Override
    public Map<String, String> queryGroupRule() {

        Map<String, String> result = Maps.newConcurrentMap();

        String wxRule = configMapper.selectConfig("GROUP_RULE_WX");
        String appRule = configMapper.selectConfig("GROUP_RULE_APP");

        result.put("wxRule", wxRule);//小程序
        result.put("appRule", appRule);//app H5
        return result;
    }


    private void saveDetail(ActGroupUser actGroupUser, Prize prize, String activityCode) {

        BigDecimal change = prize.getUnitRmb();
        CustomerPrize customerPrize = customerPrizeMapper.selectCustomerPrizeUnique(activityCode, UserType.GROUP_PARTAKE.name(), prize.getPrizeCode());
        //发放礼品
        prize = randomPrizeValue(customerPrize, activityCode);
        prizeAwardUtils.award(Party.crateParty(actGroupUser.getPartyId()), prize);
        //扣除余额
        AtomicDouble atomicDouble = new AtomicDouble(actGroupUser.getBalance().doubleValue());
        double bal = atomicDouble.addAndGet(-change.doubleValue());
        actGroupUser.setBalance(BigDecimal.valueOf(bal));
        actGroupUser.setUpdateTime(new Date());
        actGroupUserMapper.updateByPrimaryKeySelective(actGroupUser);
        ActGroupUser system = actGroupUserMapper.selectOneByExample(Example.builder(ActGroupUser.class).andWhere(Sqls.custom().andEqualTo("partyId",GroupEnum.GroupTranType.EXCHANGE.getSysPartyId())).build());
        system.setBalance(change);
        system.setUpdateTime(new Date());
        //更新系统账户余额
        actGroupUserMapper.updateByPrimaryKeySelective(system);
        //保存余额变动明细
        String uuid = String.valueOf(UUID.randomUUID());
        actGroupBalanceTranMapper.insertSelective(ActGroupBalanceTran.builder().tranId(uuid).changeValue(change).partyId(actGroupUser.getPartyId()).type(GroupEnum.GroupTranType.EXCHANGE.name()).build());
        //保存系统用户变动记录
        actGroupBalanceTranMapper.insertSelective(ActGroupBalanceTran.builder().tranId(uuid).changeValue(change.negate()).partyId(GroupEnum.GroupTranType.EXCHANGE.getSysPartyId()).type(GroupEnum.GroupTranType.EXCHANGE.name()).remark(GroupEnum.GroupTranType.EXCHANGE.getDesc()).build());
        //保存余额变动消息
        actGroupTipsMapper.insertSelective(ActGroupTips.builder().partyId(actGroupUser.getPartyId()).tips(GroupEnum.GroupTipsType.EXCHANGE.getDesc()).type(GroupEnum.GroupTipsType.EXCHANGE.name()).changeValue(change.negate()).build());

        //保存兑换记录
        customerAcquireRecordMapper.insertSelective(CustomerAcquire.builder().partyId(actGroupUser.getPartyId())
                .userName(actGroupUser.getName()).activityCode(activityCode)
                .userType(UserType.GROUP_PARTAKE.name())
                .mobilePhone(actGroupUser.getMobilePhone())
                .isDeleted(Constant.Delete_1_0.N.code).acquireDate(new Date())
                .prizeCode(prize.getPrizeCode()).prizeValue(change)
                .build());
    }


    private Prize randomPrizeValue(CustomerPrize customerPrize, String activityCode) {

        BigDecimal add = new BigDecimal(Math.random()).multiply(customerPrize.getMaxValue().subtract(customerPrize.getMinValue()));
        BigDecimal amount = customerPrize.getMinValue().add(add);

        Prize prize = prizeMapper.selectActivityPrize(activityCode, customerPrize.getPrizeCode());
        prize.setAmount(amount.setScale(customerPrize.getRemainValue(), ROUND_HALF_UP));
        return prize;
    }
}
