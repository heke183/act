package com.xianglin.act.biz.shared;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.common.dal.enums.PrizeType;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.act.common.util.SessionHelper;
import com.xianglin.cif.common.service.facade.constant.SessionConstants;
import com.xianglin.core.model.vo.*;
import com.xianglin.fala.session.MapSession;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author jiang yong tao
 * @date 2018/12/20  10:32
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/*.xml")
public class ActGroupShardServiceTest {

    private static final String ACT_CODE = "ACT_GROUP";

    @Autowired
    private ActGroupSharedService actGroupSharedService;

    @Before
    public void setUp(){
        MapSession session = new MapSession();
        GlobalRequestContext.setSession(session);
        session.setAttribute("partyId",1000000000002926L);
    }


    @Test
    public void queryPrizeListByActivityCode(){
        List<PrizeVo> prizeList = actGroupSharedService.queryPrizeListByActivityCode(ACT_CODE);
        System.out.println(JSON.toJSONString(prizeList));
    }

    @Test
    public void exchangePrize(){
        String prizeCode = "FIRST_PRIZE";
        String flag = actGroupSharedService.exchangePrize(GlobalRequestContext.currentPartyId(),prizeCode,ACT_CODE);
        System.out.println(flag);
    }

    @Test
    public void withDraw(){
        boolean flag = actGroupSharedService.withDraw(GlobalRequestContext.currentPartyId());
        System.out.println(flag);
    }

    @Test
    public void queryGroupDetail(){
        ActGroupInfoVo actGroupInfoVo = actGroupSharedService.queryGroupDetail(1000000000002458L);
        System.out.println(actGroupInfoVo);

    }

    @Test
    public void joinGroup(){
        ActGroupInfoVo actGroupInfoVo= actGroupSharedService.joinGroup(1000000000002458L,1000000000002100L);
        System.out.println("add:  "+actGroupInfoVo);
    }
    
    @Test
    public void share(){
        ActGroupShareVo share = actGroupSharedService.share(1000000000002926L);
        System.out.println("add:  "+share);
        
    }
    
    @Test
    public void queryScrollMessage(){
        List<ActGroupTipsVo> actGroupTipsVos = actGroupSharedService.queryScrollMessage();
        System.out.println("add:  "+actGroupTipsVos);
    }
    
    @Test
    public void groupDetailShare(){
        ActGroupInfoVo actGroupInfoVo = actGroupSharedService.groupDetailShare(1000000000002458L);
        System.out.println("add:  "+actGroupInfoVo);
    }
    
    @Test                         
    public void groupListByPartyId(){
        List<ActGroupInfoVo> actGroupInfoVos = actGroupSharedService.groupListByPartyId(1000000000002458L);
        System.out.println("add:  "+actGroupInfoVos);
    }

    public static void main(String[] args) {
        BigDecimal bigDecimal =new BigDecimal("0.49");
        BigDecimal bigDecimal1 = bigDecimal.setScale(2, BigDecimal.ROUND_DOWN);
        System.out.println(bigDecimal1);
        BigDecimal bigDecimal2 = BigDecimal.ZERO.add(bigDecimal).setScale(2, BigDecimal.ROUND_HALF_UP);
        System.out.println(bigDecimal2);
    }

    @Test
    public void commitAddress(){
        ContactInfoVO contactInfoVO = ContactInfoVO.builder().partyId(GlobalRequestContext.currentPartyId()).name("姜永涛").activityCode(ACT_CODE).address("上海市浦东新区").mobilePhone("15129477326").prizeCode("FIRST_PRIZE").build();
        boolean flag = actGroupSharedService.commitAddress(contactInfoVO);
        System.out.println(flag);
    }

    @Test
    public void queryExchangeDetail(){
        List<CustomerAcquireRecordVO>  result = actGroupSharedService.queryExchangeDetail(GlobalRequestContext.currentPartyId(),ACT_CODE,"E");
        System.out.println(result);
    }

    @Test
    public void queryRedPack(){
        RedPackageVo flag = actGroupSharedService.queryRedPack(GlobalRequestContext.currentPartyId(),"SENDREDPACKET",ACT_CODE);
        System.out.println(JSON.toJSONString(flag));
    }
    
    @Test
    public void createGroup(){
        ActGroupInfoVo group = actGroupSharedService.createGroup(1000000000002458L);
        System.out.println("createGroup"+group);
    }

    @Test
    public void queryPeopleNum(){
        Map<String,String> flag = actGroupSharedService.queryPeopleNum();
        System.out.println(JSON.toJSONString(flag));
    }

    @Test
    public void dismantlePacket(){
        BigDecimal resp = actGroupSharedService.dismantlePacket(1000000000002458L,"FIVE");
        System.out.println(resp);
    }

    @Test
    public void queryGroupTipsByPartyId(){
        List<ActGroupTipsVo>  resp = actGroupSharedService.queryGroupTipsByPartyId(GlobalRequestContext.currentPartyId(),"R");
        System.out.println(resp);
    }


}
