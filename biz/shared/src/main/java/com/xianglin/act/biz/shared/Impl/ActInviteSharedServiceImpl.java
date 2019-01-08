package com.xianglin.act.biz.shared.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.common.utils.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xianglin.act.biz.shared.ActInviteSharedService;
import com.xianglin.act.common.dal.mappers.ActInviteDetailMapper;
import com.xianglin.act.common.dal.mappers.ActInviteMapper;
import com.xianglin.act.common.dal.mappers.ConfigMapper;
import com.xianglin.act.common.dal.mappers.SequenceMapper;
import com.xianglin.act.common.dal.model.ActInvite;
import com.xianglin.act.common.dal.model.ActInviteDetail;
import com.xianglin.act.common.service.facade.model.ActInviteDTO;
import com.xianglin.act.common.service.facade.model.PageParam;
import com.xianglin.act.common.service.facade.model.PageResult;
import com.xianglin.act.common.service.integration.*;
import com.xianglin.act.common.util.*;
import com.xianglin.appserv.common.service.facade.MessageService;
import com.xianglin.act.common.service.integration.MessageServiceClient;
import com.xianglin.appserv.common.service.facade.app.GoldService;
import com.xianglin.appserv.common.service.facade.app.LogService;
import com.xianglin.appserv.common.service.facade.app.PersonalService;
import com.xianglin.appserv.common.service.facade.model.Request;
import com.xianglin.appserv.common.service.facade.model.enums.Constant;
import com.xianglin.appserv.common.service.facade.model.vo.ClientLoginLogVo;
import com.xianglin.appserv.common.service.facade.model.vo.MsgVo;
import com.xianglin.appserv.common.service.facade.model.vo.UserVo;
import com.xianglin.cif.common.service.facade.CustomersInfoService;
import com.xianglin.cif.common.service.facade.model.CustomersDTO;
import com.xianglin.cif.common.service.facade.model.Response;
import com.xianglin.cif.common.service.facade.model.RoleDTO;
import com.xianglin.cif.common.service.facade.vo.GoldcoinAccountVo;
import com.xianglin.cif.common.service.facade.vo.GoldcoinRecordVo;
import com.xianglin.core.model.enums.ActPlantEnum;
import com.xianglin.core.model.enums.ActivityEnum;
import com.xianglin.core.model.enums.Constants;
import com.xianglin.core.model.vo.*;
import com.xianglin.xlStation.base.enums.XLStationEnums;
import com.xianglin.xlStation.base.model.SmsResponse;
import com.xianglin.xlnodecore.common.service.facade.NodeService;
import com.xianglin.xlnodecore.common.service.facade.req.NodeReq;
import com.xianglin.xlnodecore.common.service.facade.resp.NodeResp;
import com.xianglin.xlnodecore.common.service.facade.vo.NodeVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Describe :
 * Created by xingyali on 2018/8/23 14:21.
 * Update reason :
 */
@org.springframework.stereotype.Service("actInviteSharedService")
@Service
public class ActInviteSharedServiceImpl implements ActInviteSharedService {

    private final static String MESSAGE_CONTENT = "你的验证码是#{XXX}，如非本人操作，请忽略本短信";

    private static final Logger logger = LoggerFactory.getLogger(ActInviteSharedServiceImpl.class);

    @Autowired
    private ActInviteMapper actInviteMapper;

    @Autowired
    private CustomersInfoServiceClient customersInfoServiceClient;

    @Autowired
    private PersonalService personalService;

    @Autowired
    private ConfigMapper configMapper;

    @Resource
    private RedissonClient redissonClient;

    @Autowired
    private NodeService nodeService;

    @Resource
    private MessageServiceClient messageServiceClient;

    @Autowired
    private MessageService messageService;

    @Autowired
    private CustomersInfoService customersInfoService;

    @Autowired
    private ActInviteDetailMapper actInviteDetailMapper;

    @Autowired
    private GoldcoinServiceClient goldcoinServiceClient;

    @Autowired
    private LogService logService;

    @Autowired
    protected SequenceMapper sequenceMapper;

    /**
     * top端查询地推用户
     *
     * @param pageParam
     * @return
     */
    @Override
    public PageResult<ActInviteDTO> queryInviteListByParam(PageParam<ActInviteDTO> pageParam) {
        PageResult<ActInviteDTO> pageResult = null;
        try {
            pageResult = new PageResult<>();
            Example example = new Example(ActInvite.class);
            if (pageParam.getParam() != null) {
                if (StringUtils.isNotEmpty(pageParam.getParam().getUser())) {
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("mobilePhone", pageParam.getParam().getUser()).orEqualTo("name", pageParam.getParam().getUser());
                }
                if (StringUtils.isNotEmpty(pageParam.getParam().getProvince())) {
                    example.and().andEqualTo("province", pageParam.getParam().getProvince());
                }
                if (StringUtils.isNotEmpty(pageParam.getParam().getCity())) {
                    example.and().andEqualTo("city", pageParam.getParam().getCity());
                }
                if (StringUtils.isNotEmpty(pageParam.getParam().getCounty())) {
                    example.and().andEqualTo("county", pageParam.getParam().getCounty());
                }
                if (StringUtils.isNotEmpty(pageParam.getParam().getTown())) {
                    example.and().andEqualTo("town", pageParam.getParam().getTown());
                }
                if (StringUtils.isNotEmpty(pageParam.getParam().getVillage())) {
                    example.and().andEqualTo("village", pageParam.getParam().getVillage());
                }
                if (StringUtils.isNotEmpty(pageParam.getParam().getUserType())) {
                    example.and().andEqualTo("userType", pageParam.getParam().getUserType());
                }
                if (StringUtils.isNotEmpty(pageParam.getParam().getGender())) {
                    example.and().andEqualTo("gender", pageParam.getParam().getGender());
                }
                if (StringUtils.isNotEmpty(pageParam.getParam().getStatus())) {
                    example.and().andEqualTo("status", pageParam.getParam().getStatus());
                }
                if (pageParam.getParam().getStartAge() > 0) {
                    example.and().andGreaterThanOrEqualTo("age", pageParam.getParam().getStartAge());
                }
                if (pageParam.getParam().getEndAge() > 0) {
                    example.and().andLessThanOrEqualTo("age", pageParam.getParam().getEndAge());
                }
                if (StringUtils.isNotEmpty(pageParam.getParam().getStartDate())) {
                    example.and().andGreaterThanOrEqualTo("createTime", pageParam.getParam().getStartDate() + " 00:00:00");
                }
                if (StringUtils.isNotEmpty(pageParam.getParam().getEndDate())) {
                    example.and().andLessThanOrEqualTo("createTime", pageParam.getParam().getEndDate() + " 23:59:59");
                }
                if (StringUtils.isNotEmpty(pageParam.getParam().getSource())) {
                    example.and().andEqualTo("source", pageParam.getParam().getSource());
                }
            }
            example.and().andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name());
            example.orderBy("createTime").desc();
            List<ActInvite> actInvites = actInviteMapper.selectByExampleAndRowBounds(example, new RowBounds((pageParam.getCurPage() - 1) * pageParam.getPageSize(), pageParam.getPageSize()));

            int count = actInviteMapper.selectCountByExample(example);
            List<ActInviteDTO> actInviteDto = DTOUtils.map(actInvites, ActInviteDTO.class);
            actInviteDto = actInviteDto.stream().map(vo -> {
                if (StringUtils.isNotEmpty(vo.getNodeMobile())) {
                    Response<CustomersDTO> customersDTOResponse = customersInfoServiceClient.selectByMobilePhone(vo.getNodeMobile());
                    if (customersDTOResponse.getResult() != null) {
                        vo.setNodeManagerName(customersDTOResponse.getResult().getCustomerName());
                        NodeReq nodeReq = new NodeReq();
                        com.xianglin.xlnodecore.common.service.facade.vo.NodeVo nodeVo = new NodeVo();
                        nodeVo.setNodeManagerPartyId(customersDTOResponse.getResult().getPartyId());
                        nodeReq.setVo(nodeVo);
                        NodeResp nodeResp = nodeService.queryNodeInfoByNodeManagerPartyId(nodeReq);
                        if (nodeResp.getVo() != null && StringUtils.isNotEmpty(nodeResp.getVo().getNodeAddress())) {
                            vo.setNodeAddress(nodeResp.getVo().getNodeAddress());
                        }
                    }
                }
                return vo;
            }).collect(Collectors.toList());
            pageResult.setResult(actInviteDto);
            pageResult.setCount(count);
        } catch (Exception e) {
            logger.warn("queryInviteList warn :" + e);
        }
        return pageResult;
    }


    @Override
    public Long userApply(ActInviteVo actInviteVo, Long partyId) {

        RLock rLock = redissonClient.getLock("ACT:INVITE:partyId" + partyId);
        ActInvite actInvite = new ActInvite();
        try {
            if (!rLock.tryLock()) {
                throw new BizException(ActPreconditions.ResponseEnum.REPEAT);
            }
            Example example = new Example(ActInvite.class);
            example.and().andEqualTo("partyId", partyId);


            List<ActInvite> actInvites = actInviteMapper.selectByExample(example);
            if (actInvites.size() > 0) {
                throw new BizException(ActPreconditions.ResponseEnum.ACT_INVITE_USER_APPLYED);
            }
            UserVo userVo = personalService.queryUser(partyId).getResult();
            CustomersDTO customersDTO = customersInfoServiceClient.selectByPartyId(partyId).getResult();
            String year = "";
            String month = "";
            String day = "";
            if (customersDTO.getCredentialsNumber().length() == 18) {
                year = customersDTO.getCredentialsNumber().substring(6, 10);
                month = customersDTO.getCredentialsNumber().substring(10, 12);
                day = customersDTO.getCredentialsNumber().substring(12, 14);
                if (Integer.parseInt(customersDTO.getCredentialsNumber().substring(16, 17)) % 2 == 0) {
                    actInvite.setGender("女");
                } else {
                    actInvite.setGender("男");
                }
            } else {
                year = customersDTO.getCredentialsNumber().substring(6, 8);
                year = "19" + year;
                month = customersDTO.getCredentialsNumber().substring(8, 10);
                day = customersDTO.getCredentialsNumber().substring(10, 12);
                if (Integer.parseInt(customersDTO.getCredentialsNumber().substring(14, 15)) % 2 == 0) {
                    actInvite.setGender("女");
                } else {
                    actInvite.setGender("男");
                }
            }

            LocalDate today = LocalDate.now();
            LocalDate birthDate = LocalDate.of(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));
            Period p = Period.between(birthDate, today);
            int age = p.getYears();
            actInvite.setAge(age);

            actInvite.setProvince(userVo.getProvince());
            actInvite.setCity(userVo.getCity());
            actInvite.setCounty(userVo.getCounty());
            actInvite.setTown(userVo.getTown());
            actInvite.setVillage(userVo.getVillage());
            actInvite.setUserType(actInviteVo.getUserType());
            actInvite.setName(customersDTO.getCustomerName());
            actInvite.setMobilePhone(userVo.getLoginName());
            actInvite.setSource(actInviteVo.getSource());
            actInvite.setRegisterNum(0);
            actInvite.setInviteNum(0);
            actInvite.setExpectNum(actInviteVo.getExpectNum());
            actInvite.setPartyId(partyId);
            actInvite.setWxInfo(actInviteVo.getWxInfo());
            if (StringUtils.isNotEmpty(actInviteVo.getNodeMobile())){
                actInvite.setNodeMobile(actInviteVo.getNodeMobile());
            }
            actInvite.setStatus(ActPlantEnum.StatusType.I.name());

            logger.info("actInviteVo = {}", actInviteVo);
            //保存用户报名信息
            actInviteMapper.insertSelective(actInvite);
        } catch (BizException b) {
            throw new BizException(ActPreconditions.ResponseEnum.ACT_INVITE_USER_APPLYED);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return actInvite.getId();
    }

    @Override
    public ActInviteHomePageVo homePageInfo(Long partyId) {

        ActInviteHomePageVo actInviteHomePageVo = new ActInviteHomePageVo();
        ActInvite actInvite = null;

        //未登录用户 和 已登录未报名用户 显示我要报名
        if (partyId != null) {
            actInviteHomePageVo.setIsLogin(true);
            actInvite = actInviteMapper.selectOne(ActInvite.builder().partyId(partyId).build());
        }else {
            actInviteHomePageVo.setIsLogin(false);
        }
        String applyStartTime = configMapper.selectConfig("ACT_INVITE_START_TIME");
        Date startTime = DateUtils.parse("yyyy-MM-dd HH:mm:ss", applyStartTime);

        String applyStopTime = configMapper.selectConfig("ACT_INVITE_STOP_TIME");
        Date stopTime = DateUtils.parse("yyyy-MM-dd HH:mm:ss", applyStopTime);

        //报名时间
        String applyTime = configMapper.selectConfig("ACT_INVITE_APPLY_TIME");
        //活动时间
        String inviteActTime = configMapper.selectConfig("ACT_INVITE_ACT_TIME");
        //已报名的名单列表
        List<ActInviteVo> actInviteVos = selectActInvites();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        actInviteVos.forEach(actInviteVo -> {
            String creatTime = sdf.format(actInviteVo.getCreateTime());
            actInviteVo.setApplyTime(creatTime);
            actInviteVo.setMobilePhone(StringUtils.substring(actInviteVo.getMobilePhone(), 0, 3) + "****" + StringUtils.substring(actInviteVo.getMobilePhone(), 7));
        });
        //活动报名倒计时
        Long recentTime = startTime.getTime() - System.currentTimeMillis();
        if (recentTime <= 0) {
            recentTime = 0L;
        } else {
            recentTime = TimeUnit.MILLISECONDS.toSeconds(recentTime);
        }

        actInviteHomePageVo.setApplyTime(applyTime);
        actInviteHomePageVo.setActInviteVos(actInviteVos);
        actInviteHomePageVo.setRecentApplyTime(recentTime);
        actInviteHomePageVo.setActivityTime(inviteActTime);
        //报名是否开始
        if (new Date().before(startTime)) {
            actInviteHomePageVo.setStatus(ActPlantEnum.InviteStatus.APPLY_NOSTART.getCode());
        } else {
            if (actInvite==null) {
                actInviteHomePageVo.setIsApply(false);
                actInviteHomePageVo.setStatus(ActPlantEnum.InviteStatus.NOT_APPLY.getCode());
            } else {
                actInviteHomePageVo.setIsApply(true);
                actInviteHomePageVo.setStatus(ActPlantEnum.InviteStatus.APPLYED.getCode());
            }
        }
        if (new Date().after(stopTime)) {
            actInviteHomePageVo.setStatus(ActPlantEnum.InviteStatus.APPLY_END.getCode());
        }
        return actInviteHomePageVo;
    }


    @Override
    public List<ActInviteVo> selectActInvites() {

        Example example = new Example(ActInvite.class);
        example.and().andEqualTo("status", ActPlantEnum.StatusType.S.name()).andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name());
        List<ActInvite> actInvites = actInviteMapper.selectByExample(example);
        List<ActInviteVo> actInviteVos = null;
        try {
            actInviteVos = DTOUtils.map(actInvites, ActInviteVo.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("actInvites = {}", actInvites);
        }
        return actInviteVos;
    }


    @Override
    public ActShareVo actShareInfo() {
        ActShareVo actShareVo = new ActShareVo();

        String shareUrl = configMapper.selectConfig("ACT_INVITE_SHARE_URL");
        String shareImg = configMapper.selectConfig("ACT_INVITE_IMG");

        actShareVo.setContent("天天邀好友，争霸排行榜，赢万元大奖！赶紧和我一起来PK吧~");
        actShareVo.setTitle("乡邻争霸赛开始报名啦！");
        actShareVo.setImage(shareImg);
        actShareVo.setShareUrl(shareUrl);

        return actShareVo;
    }

    @Override
    public Boolean updateInvite(ActInvite actInvite) {
        ActInvite actInvite1 = actInviteMapper.selectByPrimaryKey(actInvite.getId());
        if (actInvite1.getAudditTime() == null && actInvite.getStatus().equals("S")) { //审核时间为空，设置新的审核时间
            actInvite.setAudditTime(new Date());
            //查询审核时间不为空的条数
            Example example = new Example(ActInvite.class);
            example.and().andIsNotNull("audditTime");
            example.and().andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name());
            int count = actInviteMapper.selectCountByExample(example);
            if(count <= 2){
                Long partyId = actInvite1.getPartyId();
                String message = "恭喜您，获得邀请好友争霸赛报名速度第%s名，奖励现金%s元，争霸赛活动结束后以金币形式发至乡邻账号。中间若退出，奖励资格会取消哦~";
                String amt = "";
                String rank= "";
                switch (count){
                    case 0:
                        rank = "一";
                        amt = "100";
                        break;
                    case 1:
                        rank = "二";
                        amt = "50";
                        break;
                    case 2:
                        rank = "三";
                        amt = "20";
                }
                final String fsms = String.format(message,rank,amt);
                final String fmessage = String.format(message,rank,amt);
                Optional.ofNullable(customersInfoService.selectByPartyId(partyId).getResult()).ifPresent(u ->{
                    messageServiceClient.sendSmsCode(
                            u.getMobilePhone(),
                            fsms,
                            String.valueOf(60 * 30));

                    Request<MsgVo> request = new Request<>();
                    MsgVo msgVo = new MsgVo();
                    msgVo.setMsgTitle("邀请好友争霸赛活动");
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
            }
        }
        actInvite.setUpdateTime(new Date());
        Boolean flag = actInviteMapper.updateByPrimaryKeySelective(actInvite)==1;
        return flag;
    }

    @Override
    public ActInviteVo selectByPartyId(Long partyId) {

        ActInviteVo actInviteVo = new ActInviteVo();
        UserVo userVo = personalService.queryUser(partyId).getResult();
        CustomersDTO customersDTO = customersInfoServiceClient.selectByPartyId(partyId).getResult();
        List<String> roleCodeList = Lists.newArrayList();
        customersDTO.getRoleDTOs().forEach(vo->{
            roleCodeList.add(vo.getRoleCode());
        });

        if (roleCodeList.contains(ActPlantEnum.UserType.APP_USER.name()) || roleCodeList.size()==0) {
            actInviteVo.setUserType(ActPlantEnum.UserType.APP_USER.name());
        }

        if (roleCodeList.contains(ActPlantEnum.UserType.EMPLOYEE.name())) {
            actInviteVo.setUserType(ActPlantEnum.UserType.EMPLOYEE.name());
        }

        if (roleCodeList.contains(ActPlantEnum.UserType.APP_USER.name()) && roleCodeList.contains(ActPlantEnum.UserType.EMPLOYEE.name())) {
            actInviteVo.setUserType(ActPlantEnum.UserType.EMPLOYEE.name());
        }

        if (roleCodeList.contains(ActPlantEnum.UserType.NODE_MANAGER.name())){
            actInviteVo.setUserType(ActPlantEnum.UserType.NODE_MANAGER.name());
        }
        if (roleCodeList.contains(ActPlantEnum.UserType.NODE_MANAGER.name()) && roleCodeList.contains(ActPlantEnum.UserType.EMPLOYEE.name())){
            actInviteVo.setUserType(ActPlantEnum.UserType.NODE_MANAGER.name());
        }



        String year = "";
        String month = "";
        String day = "";
        if (customersDTO.getCredentialsNumber().length() == 18) {
            year = customersDTO.getCredentialsNumber().substring(6, 10);
            month = customersDTO.getCredentialsNumber().substring(10, 12);
            day = customersDTO.getCredentialsNumber().substring(12, 14);
            if (Integer.parseInt(customersDTO.getCredentialsNumber().substring(16, 17)) % 2 == 0) {
                actInviteVo.setGender("女");
            } else {
                actInviteVo.setGender("男");
            }
        } else {
            year = customersDTO.getCredentialsNumber().substring(6, 8);
            year = "19" + year;
            month = customersDTO.getCredentialsNumber().substring(8, 10);
            day = customersDTO.getCredentialsNumber().substring(10, 12);
            if (Integer.parseInt(customersDTO.getCredentialsNumber().substring(14, 15)) % 2 == 0) {
                actInviteVo.setGender("女");
            } else {
                actInviteVo.setGender("男");
            }
        }

        LocalDate today = LocalDate.now();
        LocalDate birthDate = LocalDate.of(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));
        Period p = Period.between(birthDate, today);
        int age = p.getYears();
        actInviteVo.setAge(age);
        actInviteVo.setName(customersDTO.getCustomerName());
        actInviteVo.setMobilePhone(customersDTO.getMobilePhone());

        buildHomeAdrees(actInviteVo,userVo);

        logger.info("actInviteVo = {}",actInviteVo);
        return actInviteVo;
    }


    @Override
    public Boolean updateActInviteById(ActInviteVo actInviteVo) {
        Boolean flag = actInviteMapper.updateByPrimaryKeySelective(ActInvite.builder()
                .id(actInviteVo.getId())
                .expectNum(actInviteVo.getExpectNum())
                .wxInfo(actInviteVo.getWxInfo())
                .nodeMobile(actInviteVo.getNodeMobile())
                .source(actInviteVo.getSource())
                .updateTime(new Date())
                .build()) == 1;
        return flag;
    }


    @Override
    public ActInviteVo selectApplyInfo(Long partyId) {
        ActInvite actInvite = actInviteMapper.selectOne(ActInvite.builder().partyId(partyId).build());
        ActInviteVo actInviteVo = null;
        try {
            if (actInvite!=null) {
                actInviteVo = DTOUtils.map(actInvite, ActInviteVo.class);
                UserVo userVo = personalService.queryUserByPhone(actInviteVo.getMobilePhone()).getResult();
                if (StringUtils.isNotEmpty(actInviteVo.getNodeMobile())) {
                    CustomersDTO customersDTO = customersInfoServiceClient.selectByMobilePhone(actInviteVo.getNodeMobile()).getResult();
                    if (customersDTO != null) {
                        actInviteVo.setNodeManagerName(customersDTO.getCustomerName());
                        NodeReq nodeReq = new NodeReq();
                        com.xianglin.xlnodecore.common.service.facade.vo.NodeVo nodeVo = new NodeVo();
                        nodeVo.setNodeManagerPartyId(customersDTO.getPartyId());
                        nodeReq.setVo(nodeVo);
                        NodeResp nodeResp = nodeService.queryNodeInfoByNodeManagerPartyId(nodeReq);
                        if (nodeResp.getVo() != null) {
                            actInviteVo.setNodeAddress(nodeResp.getVo().getNodeAddress());
                            actInviteVo.setNodeManagerName(nodeResp.getVo().getNodeManagerName());
                        }
                    }
                }
                if (actInvite.getProvince() != null) {
                    actInviteVo.setHomeAddress(actInvite.getProvince());
                }
                if (actInvite.getCity() != null) {
                    actInviteVo.setHomeAddress(actInviteVo.getHomeAddress() + actInvite.getCity());
                }
                if (actInvite.getCounty() != null) {
                    actInviteVo.setHomeAddress(actInviteVo.getHomeAddress() + actInvite.getCounty());
                }
                if (actInvite.getTown() != null) {
                    actInviteVo.setHomeAddress(actInviteVo.getHomeAddress() + actInvite.getTown());
                }
                if (actInvite.getVillage() != null) {
                    actInviteVo.setHomeAddress(actInviteVo.getHomeAddress() + actInvite.getVillage());
                }
            } else {
                throw new BizException(ActPreconditions.ResponseEnum.ACT_INVITE_USER_NOAPPLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("actInvites.size()",actInvite);
            logger.info("actInviteVo = {}", actInviteVo);
        }
        return actInviteVo;
    }

    @Override
    public List<ActInviteVo> queryActRankList() {

        List<ActInvite> actInvites = actInviteMapper.selectActIviteList();
        List<ActInviteVo> actInviteVos = null;
        try {
            actInviteVos = DTOUtils.map(actInvites, ActInviteVo.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("actInvites = {}", actInvites);
        }
        actInviteVos.parallelStream().forEach(actInviteVo -> {
            UserVo userVo = personalService.queryUser(actInviteVo.getPartyId()).getResult();
            if (userVo.getHeadImg() != null) {
                actInviteVo.setHeadImg(userVo.getHeadImg());
            } else {
                String defaultImg = configMapper.selectConfig("default_user_headimg");
                actInviteVo.setHeadImg(defaultImg);
            }
        });
        return actInviteVos;
    }

    @Override
    public ActInviteHomePageVo homePageInfoTwo(Long partyId) {

        ActInviteHomePageVo actInviteHomePageVo = new ActInviteHomePageVo();

        //排行榜
        actInviteHomePageVo.setActInviteVos(this.queryActRankList());
        //判断用户是否登陆
        if (partyId != null) {
            actInviteHomePageVo.setIsLogin(true);
            Example example = new Example(ActInvite.class);
            example.and().andEqualTo("partyId", partyId).andEqualTo("status",ActPlantEnum.StatusType.S.name()).andEqualTo("isDeleted", ActPlantEnum.DeleteTypeEnum.N.name());

            List<ActInvite> actInviteList = actInviteMapper.selectByExample(example);
            if (actInviteList.size() > 0) {
                actInviteHomePageVo.setIsApply(true);
            } else {
                actInviteHomePageVo.setIsApply(false);
            }
        } else {
            actInviteHomePageVo.setIsLogin(false);
        }
        //计算活动剩余天数
        String configValue = configMapper.selectConfig("ACT_INVITE_TIME");
        Map json = JSONObject.parseObject(configValue);
        String stopDate = json.get("stopTime").toString();
        Date stopTime = DateUtils.formatStr(stopDate, DateUtils.DATETIME_FMT);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY,1);
        Date date = calendar.getTime();
        Long recentDay = TimeUnit.MILLISECONDS.toDays(stopTime.getTime() - date.getTime());
        if (recentDay==0L){
            recentDay = 1L;
        }
        actInviteHomePageVo.setRecentDays(recentDay);

        return actInviteHomePageVo;
    }

    @Override
    public ActShareVo actShareInfoTwo() {

        ActShareVo actShareVo = new ActShareVo();

        String shareImg = configMapper.selectConfig("ACT_INVITE_SHARE_IMG_TWO");
        String shareOutUrl = configMapper.selectConfig("ACT_INVITE_SHARE_OUT_URL");
        String shareInUrl = configMapper.selectConfig("ACT_INVITE_SHARE_IN_URL");

        actShareVo.setTitle("乡邻争霸赛，助力万元大奖！");
        actShareVo.setContent("上天入地寻找你，争霸排行等你助力！");
        actShareVo.setShareUrl(shareInUrl);
        actShareVo.setSharOutUrl(shareOutUrl);
        actShareVo.setImage(shareImg);
        return actShareVo;
    }

    @Override
    public Map<String, Object> selectRule() {
        Map<String, Object> resultMap = Maps.newConcurrentMap();
        resultMap.put("rule", configMapper.selectConfig("ACT_INVITE_RULE"));
        resultMap.put("actDate", configMapper.selectConfig("ACT_INVITE_ACT_TIME"));
        return resultMap;
    }

    @Override
    public Boolean userRegister(PlantRegisterVo plantRegisterVo) {
        RLock rLock = redissonClient.getLock("ACT:INVITE:REGISTER:phone" + plantRegisterVo.getMobilePhone());

        Boolean flag = false;
        try {
            if (!rLock.tryLock()) {
                throw new BizException(ActPreconditions.ResponseEnum.REPEAT);
            }
            Response<CustomersDTO> resp = customersInfoServiceClient.selectByMobilePhone(plantRegisterVo.getMobilePhone());
            if (resp.getResult() != null) {
                return true;
            }else {

                SmsResponse smsResponse = messageServiceClient.checkSmsCode(plantRegisterVo.getMobilePhone(), plantRegisterVo.getCode(), Boolean.TRUE);
                if (!(XLStationEnums.ResultSuccess.getCode() == smsResponse.getBussinessCode())) {
                    throw new BizException(ActPreconditions.ResponseEnum.CHECK_MESSAGE_FAIL);
                }

                CustomersDTO customersDTO = new CustomersDTO();
                customersDTO.setMobilePhone(plantRegisterVo.getMobilePhone());
                customersDTO.setCreator(plantRegisterVo.getMobilePhone());
                com.xianglin.cif.common.service.facade.model.Response<CustomersDTO> openAccountResponse =
                        customersInfoServiceClient.openAccount(customersDTO, Constants.SYSTEM_NAME);
                if (!openAccountResponse.isSuccess()) {
                    logger.error("用户开户失败: {}", JSON.toJSONString(openAccountResponse));
                    throw new BizException(ActPreconditions.ResponseEnum.ERROR);
                } else {
                    //如果活动结束，可以邀请，但是不计算邀请数量
                    String str = configMapper.selectConfig("ACT_INVITE_TIME");
                    Map result = JSON.parseObject(str);
                    Date stop = DateUtils.formatStr(result.get("stopTime").toString(), "yyyy-MM-dd HH:mm:ss");

                    if (new Date().after(stop)) {
                        sendReward(openAccountResponse.getResult(),plantRegisterVo.getFromPartyId());
                        return true;
                    }

                    ActInvite actInvite = actInviteMapper.selectOne(ActInvite.builder().partyId(plantRegisterVo.getFromPartyId()).build());
                    if (actInvite!=null) {
                        //更新被推荐人的邀请数量
                        actInvite.setInviteNum(actInvite.getInviteNum() + 1);
                        actInvite.setUpdateTime(new Date());
                        actInviteMapper.updateByPrimaryKeySelective(actInvite);

                        //保存邀请记录
                        actInviteDetailMapper.insertSelective(ActInviteDetail.builder()
                                .partyId(openAccountResponse.getResult().getPartyId())
                                .recPartyId(plantRegisterVo.getFromPartyId())
                                .status(ActPlantEnum.StatusType.I.name())
                                .createTime(new Date())
                                .updateTime(new Date())
                                .build());
                        //发放用户注1000册金币
                        com.xianglin.cif.common.service.facade.model.Response<GoldcoinRecordVo> goldcoinRecordVoResponse =
                                goldcoinServiceClient.doRecord(GoldcoinRecordVo.builder()
                                        .system("act")
                                        .amount(1000)
                                        .fronPartyId(Constants.GOLD_SYS_ACCOUNT)
                                        .toPartyId(openAccountResponse.getResult().getPartyId())
                                        .remark("注册")
                                        .type("REGISTER")
                                        .requestId(GoldSequenceUtil.getSequence(openAccountResponse.getResult().getPartyId(), sequenceMapper
                                                .getSequence())).build());
                        if (!com.google.common.base.Objects.equal(goldcoinRecordVoResponse.getCode(), 1000)) {
                            logger.error("添加金币失败！", com.alibaba.fastjson.JSON.toJSONString(goldcoinRecordVoResponse));
                            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
                        }
                    } else {
                        throw new BizException(ActPreconditions.ResponseEnum.ACT_INVITE_USER_NOAPPLY);
                    }
                }
            }

            flag = true;
        } catch (BizException b) {
            if (b.getResponseEnum().code.equals(ActPreconditions.ResponseEnum.ACT_INVITE_USER_REGISTED.code)) {
                throw new BizException(ActPreconditions.ResponseEnum.ACT_INVITE_USER_REGISTED);
            }
            if (b.getResponseEnum().code.equals(ActPreconditions.ResponseEnum.CHECK_MESSAGE_FAIL.code)) {
                throw new BizException(ActPreconditions.ResponseEnum.CHECK_MESSAGE_FAIL);
            }
            if (b.getResponseEnum().code.equals(ActPreconditions.ResponseEnum.ACT_INVITE_USER_NOAPPLY.code)) {
                throw new BizException(ActPreconditions.ResponseEnum.ACT_INVITE_USER_NOAPPLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return flag;
    }

    @Override
    public ReferralVo referralCode(Long partyId) {
        UserVo userVo = personalService.queryUser(partyId).getResult();
        CustomersDTO customersDTO = customersInfoServiceClient.selectByPartyId(partyId).getResult();
        return ReferralVo.builder()
                .code(customersDTO.getInvitationCode())
                .headImg(userVo.getHeadImg())
                .name(customersDTO.getCustomerName()).build();
    }

    @Override
    public ActInviteVo queryApplyInfoTwo(Long partyId) {
        ActInviteVo actInviteVo = this.selectApplyInfo(partyId);
        List<ActInviteVo> actInvites = this.queryActRankList();
        actInvites.forEach(vo -> {
            if (vo.getPartyId().equals(partyId)) {
                actInviteVo.setRank(actInvites.indexOf(vo) + 1);
            }
        });
        return actInviteVo;
    }

    @Override
    public Boolean sendMsg(String phone) {
        Boolean flag = false;
        com.xianglin.appserv.common.service.facade.model.Response<UserVo> userVoResponse = personalService.queryUserByPhone(phone);
        if (userVoResponse.getResult() != null) {
            flag = true;
        } else {
            com.xianglin.xlStation.base.model.Response response =
                    messageServiceClient.sendSmsCode(
                            phone,
                            MESSAGE_CONTENT,
                            String.valueOf(60 * 30));

            if (!(XLStationEnums.ResultSuccess.getCode() == response.getBussinessCode())) {
                logger.error("验证码发送失败：{}", JSON.toJSONString(response));
                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
            }
        }
        return flag;
    }

    @Override
    public List<ActInvite> queryInviteList(ActInvite actInvite) {
        return actInviteMapper.select(actInvite);
    }

    @Override
    public int queryInviteDetailCount(ActInviteDetail actInviteDetail) {
        return actInviteDetailMapper.selectCount(actInviteDetail);
    }

    @Override
    public List<ActInviteDetail> queryInviteDetailList(ActInviteDetail actInviteDetail) {
        return actInviteDetailMapper.select(actInviteDetail);
    }

    @Override
    public Boolean syncInviteList() {
        try {
            //查询所有的地推用户
            List<ActInvite> actInvites = actInviteMapper.select(ActInvite.builder().isDeleted("N").build());
            actInvites.stream().forEach(vo->{
                final Set<String> pastActiveDeviceList= new HashSet<>(); //活跃设备集合
                AtomicInteger activeNumCount = new AtomicInteger(0);//邀请活跃用户数
                AtomicInteger pastActiveUserCount = new AtomicInteger(0);//昨日活跃用户
                //邀请数
                List<ActInviteDetail>  inviteList= actInviteDetailMapper.select(ActInviteDetail.builder().isDeleted("N").recPartyId(vo.getPartyId()).build());
                //注册数
                List<ActInviteDetail> registerList = actInviteDetailMapper.select(ActInviteDetail.builder().isDeleted("N").recPartyId(vo.getPartyId()).status("S").build());
                registerList.stream().forEach(reg->{
                    //查邀请活跃用户数，金币账户是否大于5，大于5 activeNumCount+1
                    GoldcoinRecordVo req = new GoldcoinRecordVo();
                    req.setToPartyId(reg.getPartyId());
                    req.setStatus("S");
                    req.setStartPage(1);
                    req.setPageSize(10);
                    Response<List<GoldcoinRecordVo>> listResponse1 = goldcoinServiceClient.queryRecord(req);
                    if (listResponse1.getResult() != null && listResponse1.getResult().size()>1) {
                        activeNumCount.getAndAdd(1);
                    }
                    //查昨日活跃用户
                    //昨天的开始时间和结束时间
                    String startTime=LocalDate.now().plusDays(-1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+" 00:00:00";
                    String endTime=LocalDate.now().plusDays(-1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+" 23:59:59";
                    com.xianglin.appserv.common.service.facade.model.Response<List<ClientLoginLogVo>> listResponse = logService.queryUserLoginList(reg.getPartyId(), DateUtils.formatStr(startTime, "yyyy-MM-dd HH:mm:ss"), DateUtils.formatStr(endTime, "yyyy-MM-dd HH:mm:ss"));
                    if(listResponse.getResult() != null && listResponse.getResult().size()>0){
                        pastActiveUserCount.getAndAdd(1);
                        //查昨日活跃设备
                        listResponse.getResult().stream().limit(1).forEach(pastAct->{
                            pastActiveDeviceList.add(pastAct.getDeviceId());
                        });
                    }
                });
                actInviteMapper.updateByPrimaryKeySelective(ActInvite.builder().id(vo.getId()).inviteNum(inviteList.size()).registerNum(registerList.size()).activeNum(activeNumCount.get()).pastActiveUser(pastActiveUserCount.get()).pastActiveDevice(pastActiveDeviceList.size()).build());
            });
        } catch (Exception e) {
            logger.warn("syncInviteList", e);
        }
        return true;
    }


    //拼接家乡地址
    private ActInviteVo buildHomeAdrees(ActInviteVo actInviteVo,UserVo userVo){
        if (userVo.getProvince() != null) {
            actInviteVo.setHomeAddress(userVo.getProvince());
        }
        if (userVo.getCity() != null) {
            actInviteVo.setHomeAddress(actInviteVo.getHomeAddress() + userVo.getCity());
        }
        if (userVo.getCounty() != null) {
            actInviteVo.setHomeAddress(actInviteVo.getHomeAddress() + userVo.getCounty());
        }
        if (userVo.getTown() != null) {
            actInviteVo.setHomeAddress(actInviteVo.getHomeAddress() + userVo.getTown());
        }
        if (userVo.getVillage() != null) {
            actInviteVo.setHomeAddress(actInviteVo.getHomeAddress() + userVo.getVillage());
        }
        return actInviteVo;
    }

    private void sendReward(CustomersDTO customersDTO,Long fromPartyId){
        //给邀请人发放金币奖励
        com.xianglin.cif.common.service.facade.model.Response<GoldcoinRecordVo> goldcoinRecordVoResponse =
                goldcoinServiceClient.doRecord(GoldcoinRecordVo.builder()
                        .system("act")
                        .amount(1000)
                        .fronPartyId(Constants.GOLD_SYS_ACCOUNT)
                        .toPartyId(fromPartyId)
                        .remark("邀请好友注册")
                        .type("INVITE")
                        .requestId(GoldSequenceUtil.getSequence(fromPartyId, sequenceMapper
                                .getSequence())).build());
        if (!com.google.common.base.Objects.equal(goldcoinRecordVoResponse.getCode(), 1000)) {
            logger.error("添加金币失败！", com.alibaba.fastjson.JSON.toJSONString(goldcoinRecordVoResponse));
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }

        //发放用户注1000册金币
        com.xianglin.cif.common.service.facade.model.Response<GoldcoinRecordVo> goldcoinRecordVoResponseInvited =
                goldcoinServiceClient.doRecord(GoldcoinRecordVo.builder()
                        .system("act")
                        .amount(1000)
                        .fronPartyId(Constants.GOLD_SYS_ACCOUNT)
                        .toPartyId(customersDTO.getPartyId())
                        .remark("注册")
                        .type("REGISTER")
                        .requestId(GoldSequenceUtil.getSequence(customersDTO.getPartyId(), sequenceMapper
                                .getSequence())).build());
        if (!com.google.common.base.Objects.equal(goldcoinRecordVoResponseInvited.getCode(), 1000)) {
            logger.error("添加金币失败！", com.alibaba.fastjson.JSON.toJSONString(goldcoinRecordVoResponseInvited));
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
    }
}
