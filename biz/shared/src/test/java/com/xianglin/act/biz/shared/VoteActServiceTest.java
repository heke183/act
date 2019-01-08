package com.xianglin.act.biz.shared;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xianglin.act.common.dal.model.Activity;
import com.xianglin.act.common.dal.model.CustomerAcquire;
import com.xianglin.act.common.dal.model.VoteActivity;
import com.xianglin.act.common.service.facade.constant.ActivityConfig;
import com.xianglin.act.common.service.facade.model.ActVoteDTO;
import com.xianglin.act.common.service.facade.model.PageParam;
import com.xianglin.act.common.service.facade.model.PageResult;
import com.xianglin.act.common.service.facade.model.VoteAcquireRecordDTO;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.core.model.enums.OrderTypeEnum;
import com.xianglin.core.model.vo.VoteItemVO;
import com.xianglin.core.service.VoteActivityContext;
import com.xianglin.core.service.VoteActivityContextV2;
import com.xianglin.fala.session.MapSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yefei
 * @date 2018-06-05 16:11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/biz-shared-vote-test.xml")
public class VoteActServiceTest {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(VoteActServiceTest.class);

    @Autowired
    private VoteActService voteActService;

    @Autowired
    private void before() {
        Activity currentActivity = voteActService.getVoteActContext("HD001");
        VoteActivityContextV2.setCurrentVoteActivity(currentActivity);

        MapSession session = new MapSession();
        GlobalRequestContext.setSession(session);
    }

    /**
     * 投票
     */
    @Test
    public void voteItemTest() {
        voteActService.voteItem(1000000000002465L, 5199881L);
    }

    /**
     * 我的
     */
    @Test
    public void myItem() {
        VoteItemVO voteItemVO = voteActService.myItem(1000000000002465L);
        logger.info("------------ myItem() : {}", JSON.toJSONString(voteItemVO));
    }

    /**
     * 领取奖励
     */
    @Test
    public void drawAward() {
        CustomerAcquire acquire = voteActService.drawAward(1000000000002269L);
        logger.info("------------ drawAward() : {}", JSON.toJSONString(acquire));
    }

    /**
     * 晒单
     */
    @Test
    public void shareAward() {
        voteActService.shareAward(1000000000002269L);
    }

    /**
     * 参数查询
     */
    @Test
    public void queryActivityConfigByCode() {

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

        Map<String,String> result = voteActService.queryActivityConfigByCode(ActivityConfig.ActivityCode.HD001.name(),list);
        logger.info("------------ drawAward() : {}", JSON.toJSONString(result));
    }
    
    @Test
    public void endActivity(){
        Boolean aBoolean = voteActService.updateActivity(ActivityConfig.ActivityCode.HD001.name(),"CLEAR");
        logger.info("------------ endActivity() : {}", JSON.toJSONString(aBoolean));
    }
    
    @Test
    public void queryVoteActivityList(){
        ActVoteDTO actVoteDTO = voteActService.queryVoteActivityList();
        logger.info("------------ queryVoteActivityList() : {}", JSON.toJSONString(actVoteDTO));
        
    }
    
    @Test
    public void queryAcquireRecordList(){
        PageParam<VoteAcquireRecordDTO> pageParam = new PageParam<>();
        PageResult<VoteAcquireRecordDTO> pageResult = voteActService.queryAcquireRecordList(pageParam);
        logger.info("------------ queryAcquireRecordList() : {}", JSON.toJSONString(pageResult));
        
    }
    
    @Test
    public void updateAcquireRecord(){
        voteActService.updateAcquireRecord(VoteAcquireRecordDTO.builder().id(5015L).memcCode("335").build());
    }

    @Test
    public void queryItemList(){
        Integer curPage = 1;
        Integer pageSize = 10;
        Long lastId = 0L;
        List<VoteItemVO> voteItemVOS =  voteActService.queryItemList(OrderTypeEnum.ASC,curPage,pageSize,lastId);
        System.out.println(voteItemVOS);
    }

    @Test
    public void randomVote(){
        voteActService.randomVote("HD001");
    }
}
