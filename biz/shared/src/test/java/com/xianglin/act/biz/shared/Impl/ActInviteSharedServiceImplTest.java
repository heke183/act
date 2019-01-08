package com.xianglin.act.biz.shared.Impl;

import com.xianglin.act.biz.shared.ActInviteSharedService;
import com.xianglin.act.biz.shared.StepSharedService;
import com.xianglin.act.common.dal.model.ActInvite;
import com.xianglin.act.common.dal.model.ActStepDetail;
import com.xianglin.act.common.service.integration.MessageServiceClient;
import com.xianglin.core.model.vo.ActInviteHomePageVo;
import com.xianglin.core.model.vo.ActInviteVo;
import com.xianglin.core.model.vo.ActShareVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author jiang yong tao
 * @date 2018/8/24  10:55
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/*.xml")
public class ActInviteSharedServiceImplTest {

    @Autowired
    private ActInviteSharedService actInviteSharedService;
    
    @Autowired
    private StepSharedService stepSharedService;

    @Autowired
    private MessageServiceClient messageServiceClient;

    @Test
    public void queryInviteList() {
    }

    @Test
    public void userApply() {
    }

    @Test
    public void homePageInfo() {

        ActInviteHomePageVo actInviteHomePageVo =actInviteSharedService.homePageInfo(1000076847L);
        System.out.println("actInviteHomePageVo =====================" + actInviteHomePageVo.toString());
    }

    @Test
    public void selectActInvites() {
    }

    @Test
    public void shareInfo(){
       ActShareVo actShareVo = actInviteSharedService.actShareInfo();
        System.out.println("actInviactShareVoteHomePageVo =====================" + actShareVo.toString());
    }


    @Test
    public void selectByPartyId(){
        ActInviteVo actInviteVo = actInviteSharedService.selectByPartyId(1000000000002926L);
        System.out.println("actInviteVo =====================" + actInviteVo.toString());
    }

    @Test
    public void myApplyInfo(){
        ActInviteVo actInviteVo = actInviteSharedService.selectApplyInfo(666666672393L);
        System.out.println("actInviteVo =====================" + actInviteVo.toString());
    }

    @Test
    public void selectRankingList(){
        List<ActInviteVo> actInviteVos = actInviteSharedService.queryActRankList();
        actInviteVos.forEach(vo ->{
            System.out.println(vo.toString());
        });
    }

    @Test
    public void actShareInfo(){
        ActShareVo actShareVo = actInviteSharedService.actShareInfo();
        System.out.println(actShareVo.toString());
    }

    @Test
    public void homePageInfoTwo(){
        ActInviteHomePageVo actInviteHomePageVo = actInviteSharedService.homePageInfoTwo(1000076847L);
        System.out.println(actInviteHomePageVo.toString());
    }

    @Test
    public void myApplyInfoTwo(){
        ActInviteVo actInviteVo = actInviteSharedService.queryApplyInfoTwo(1000076847L);
        System.out.println(actInviteVo.toString());
    }

    @Test
    public void actShareInfoTwo(){
        ActShareVo actShareVo = actInviteSharedService.actShareInfoTwo();
        System.out.println(actShareVo.toString());
    }
    
    @Test
    public void syncInviteList(){
        Boolean aBoolean = actInviteSharedService.syncInviteList();
        System.out.println(aBoolean);
    }
    
    @Test
    public void synchStepDetail(){
        List<ActStepDetail> details = new ArrayList<>();
        List<ActStepDetail> actStepDetails = stepSharedService.synchStepDetail(details,1000076847L);
    }

    @Test
    public void sendSmsMessage(){
        messageServiceClient.sendSmsCode(
                "18963581986",
                "恭喜您，戒得 500 票达成速度第一名，奖励现金 100 元，争霸赛活劢结束后以金币形式发放至乡邻账户。再接再厉，赢取更多现金。",
                String.valueOf(60));
    }
}
