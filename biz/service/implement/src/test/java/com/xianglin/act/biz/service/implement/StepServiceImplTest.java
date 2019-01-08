package com.xianglin.act.biz.service.implement;

import com.xianglin.act.common.service.facade.StepService;
import com.xianglin.act.common.service.facade.constant.StepDetailEnum;
import com.xianglin.act.common.service.facade.model.ActStepDetailDTO;
import com.xianglin.act.common.service.facade.model.ActStepDetailShareInfo;
import com.xianglin.act.common.service.facade.model.ActStepTotal;
import com.xianglin.act.common.service.facade.model.Response;
import com.xianglin.act.common.util.SessionHelper;
import com.xianglin.appserv.common.service.facade.model.AppSessionConstants;
import com.xianglin.cif.common.service.facade.constant.SessionConstants;
import com.xianglin.fala.session.MapSession;
import com.xianglin.fala.session.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe :
 * Created by xingyali on 2018/7/20 14:58.
 * Update reason :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/*.xml")
public class StepServiceImplTest {
    @Autowired
    private StepService stepService;
    
    @Autowired
    private SessionHelper sessionHelper;

   @Before
    public void initSession() {
        Session session = new MapSession();
        session.setAttribute(SessionConstants.PARTY_ID, 1000000000002926L);
       session.setAttribute(AppSessionConstants.SYSTEM_TYPE,"Android");
        sessionHelper.saveLocalSesson(session);
    }
   
    @Test
    public void synchStepDetail(){

        /*MapSession session = new MapSession();
        session.setAttribute("partyId", 1000000000002458L);
        GlobalRequestContext.setSession(session);*/
        
        List<ActStepDetailDTO> details = new ArrayList<>();
        ActStepDetailDTO actStepDetailDTO = ActStepDetailDTO.builder().partyId(1000000000002458L).type(StepDetailEnum.FIRST.name()).stepNumber(100).build();
        ActStepDetailDTO actStepDetailDTO1 = ActStepDetailDTO.builder().partyId(1000000000002458L).type(StepDetailEnum.SECOND.name()).stepNumber(100).build();
        ActStepDetailDTO actStepDetailDTO2 = ActStepDetailDTO.builder().partyId(1000000000002458L).type(StepDetailEnum.THIRD.name()).stepNumber(1000).build();
        ActStepDetailDTO actStepDetailDTO3 = ActStepDetailDTO.builder().partyId(1000000000002458L).type(StepDetailEnum.FOURTH.name()).stepNumber(200).build();
        details.add(actStepDetailDTO);              
        details.add(actStepDetailDTO1);
        details.add(actStepDetailDTO2);
        details.add(actStepDetailDTO3);
        Response<List<ActStepDetailDTO>> listResponse = stepService.synchStepDetail(details,"20180725");
        System.out.print("同步客户端数据：" + listResponse);
    }
    
    @Test
    public void queryContentShare(){
        Response<ActStepDetailShareInfo> actStepDetailShareInfoResponse = stepService.queryContentShare();
        System.out.print("查询分享文案内容：" + actStepDetailShareInfoResponse);
        
    }

    @Test
    public void queryActStepDetailShare(){
       // Response<List<ActStepDetailDTO>> actStepDetailShareInfoResponse = stepService.queryActStepDetailShare();
       // System.out.print("查询分享明细：" + actStepDetailShareInfoResponse);

    }
    
    @Test
    public void queryRanking(){
        Response<List<ActStepDetailDTO>> listResponse = stepService.queryRanking();
        System.out.print("查询排行榜：" + listResponse);
    }

    @Test
    public void queryRewardList(){
        Response<List<ActStepDetailDTO>> listResponse = stepService.queryRewardList(0L);
        System.out.print("查询排行榜：" + listResponse);
    }

    @Test
    public void reward(){
        Response<Integer> reward = stepService.reward(StepDetailEnum.SECOND.name(),"20181109");
        System.out.print("兑换：" + reward);
    }

    public static void main(String[] args) {
        Integer a=1000;
        int i = a.compareTo(new Integer(1000));
        System.out.print("兑换：" + i);
        if(i<0){
            System.out.print("兑换1：" + 111); 
        }else{
            System.out.print("兑换2：" + 222);  
        }
    }
    
    @Test
    public void queryStepTotail(){
        Response<ActStepTotal> actStepTotalResponse = stepService.queryStepTotail();
        System.out.print("兑换：" + actStepTotalResponse);
        
    }
    
}
