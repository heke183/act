package com.xianglin.act.biz.shared.Impl;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.biz.shared.ActPlantSharedService;
import com.xianglin.act.common.dal.mappers.ActPlantLvMapper;
import com.xianglin.act.common.dal.mappers.ConfigMapper;
import com.xianglin.act.common.dal.model.ActPlantLv;
import com.xianglin.act.common.dal.model.ActPlantNotice;
import com.xianglin.act.common.service.facade.model.ActPlantLvTranDTO;
import com.xianglin.act.common.service.facade.model.ActPlantLvTranPageDTO;
import com.xianglin.act.common.service.facade.model.PageParam;
import com.xianglin.act.common.util.DTOUtils;
import com.xianglin.core.model.CheckMessageVO;
import com.xianglin.core.model.vo.ActPlantLvTranVo;
import com.xianglin.core.model.vo.ActPlantLvVo;
import com.xianglin.core.model.vo.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiang yong tao
 * @date 2018/8/7  16:08
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/*.xml")
public class ActPlantSharedServiceImplTest {


    @Autowired
    private ActPlantLvMapper actPlantLvMapper;
    @Autowired
    private ActPlantSharedService actPlantSharedService;

    @Autowired
    private ConfigMapper configMapper;

    @Test
    public void actIsStop() {
        String str = "PLANT_TREE_ACTIVITY_TIME";
        String configValue = actPlantSharedService.selectActConfigValue(str);
        Map map = JSON.parseObject(configValue);
        String val = map.get("stopTime").toString();
        System.out.println("val===================>>>"+val);

    }

    @Test
    public void queryPrizeList() {
//        List<ActPlantPrizeVo> actPlantPrizeVos = actPlantSharedService.queryPrizeList();
//        System.out.println("actPlantPrizeVos>>>>" + actPlantPrizeVos.toString());
    }

    @Test
    public void userCertification(){
        Long partyId = 1000000000002900L;
        boolean flag = actPlantSharedService.userCertification(partyId);
        System.out.println("用户是否实名认证>>>>>" + flag);

    }

    @Test
    public void exchangePrize(){
        Long partyId = 7000174L;
        Long flag = actPlantSharedService.exchangePrize(partyId,"1001");
        System.out.println(flag);
    }

    @Test
    public void obtainLv(){
        Example example = new Example(ActPlantLv.class);
        example.and().andEqualTo("partyId",7000174L);
        ActPlantLv actPlantLv = actPlantLvMapper.selectByExample(example).get(0);
        ActPlantLvVo actPlantLvVo = null;
        try {
            actPlantLvVo = DTOUtils.map(actPlantLv,ActPlantLvVo.class);
        }catch (Exception e){
            e.printStackTrace();
        }
//        ActPlantLvObtainVo sum = actPlantSharedService.obtainLv(actPlantLvVo,7000174L,1000000000002518L);
//        System.out.println("收获能量》》》》》》》" + sum.getObtainLv());
    }

    @Test
    public void commitAdress(){
        ActPlantLvTranVo actPlantLvTranVo = new ActPlantLvTranVo();
        actPlantLvTranVo.setUserName("小明");
        actPlantLvTranVo.setAddress("上海市松江区");
        Long primakey = actPlantSharedService.addressCommit(actPlantLvTranVo);

        System.out.println("主键id=============》》》》"+primakey);
    }

    @Test
    public void queryRankingList(){
        ActPlantRankingVo actPlantRankingVo = actPlantSharedService.queryRankingList(1000000000002458L);
        System.out.println("排行榜前10=============》》》》"+actPlantRankingVo.toString());

    }

    @Test
    public void messageDetailsList(){
        List<ActPlantMessageDetailVo> actPlantMessageDetailVoList = actPlantSharedService.messageDetailsList(1000000000002458L);
        System.out.println("查近三天的消息明细列表=============》》》》"+actPlantMessageDetailVoList.toString());
    }

    @Test
    public void u (){
        List<ActPlantTaskVo> task = actPlantSharedService.task(1000000000002458L);
        System.out.println("查用户的任务表=============》》》》"+task.toString());
    }

   @Test
    public void showLv(){
       List<ActPlantLvVo> actPlantLvs = actPlantSharedService.showLv(1000000000002185L);

       actPlantLvs.forEach(actPlantLvVo -> {
           Long time = (actPlantLvVo.getMatureTime().getTime() - System.currentTimeMillis()) / 1000;
           if (time > 0) {
               actPlantLvVo.setCanCollect(false);
               actPlantLvVo.setRencentTime(time);
           } else {
               actPlantLvVo.setCanCollect(true);
               actPlantLvVo.setRencentTime(0L);
           }
       });

       System.out.println("该用户的爱心值有"+actPlantLvs.size()+"个!");
       System.out.println("该用户的爱心值有"+actPlantLvs.toString());
   }
   
   @Test
    public void register(){
       CheckMessageVO checkMessageVO = new CheckMessageVO();
      // Boolean register = actPlantSharedService.register(checkMessageVO);
       //System.out.println("新用户注册，并领取树苗和爱心值奖励"+register);
   }

   @Test
    public void findActCode(){
       String actCode = actPlantSharedService.findActCode();
       System.out.println(actCode);
   }

   @Test
   public void listPlantLvVo(){
       List<ActPlantLvVo> actPlantLvVos = actPlantSharedService.showLv(1000000000002518L);
       for (ActPlantLvVo actPlantLvVo:actPlantLvVos) {
           Long time = actPlantLvVo.getMatureTime().getTime()-actPlantLvVo.getShouTime().getTime();
           actPlantLvVo.setRencentTime(time);
           System.out.println(time);
       }
   }

   @Test
    public void actPlantTips(){
      List<ActPlantTipVo> actPlantTipVos = actPlantSharedService.findByPartyId(1000000000002518L);
       System.out.println("用户弹窗的个数为》》》》"+actPlantTipVos.size());
   }

   @Test
   public void test(){
       ActPlantLv actPlantLv = ActPlantLv.builder().lv(10).totalLv(10).partyId(9123L).shouTime(new Date()).type("").build();
       actPlantLvMapper.insertSelective(actPlantLv);
       System.out.println("主键id为："+actPlantLv.getId());
   }


   @Test
    public void userHomePageInfo(){
//      ActPlantHomePageVo actPlantHomePageVo = actPlantSharedService.userHomePageInfo(null,1000076847L);
//       System.out.println(actPlantHomePageVo.toString());
   }
   
   @Test
    public void isRegisterOrisReceiveTree(){
       Map<String, Boolean> registerOrisReceiveTree = actPlantSharedService.isRegisterOrisReceiveTree("13333444444");
       System.out.println("判断用户是否注册App或领取了树苗："+registerOrisReceiveTree);
   }

    @Test
    public void getRandomPrize(){
        Map<String,Object> map = actPlantSharedService.getRandomPrize(1000000000002934L);
        System.out.println("恭喜你》》》》》》》》》》》》》》" + map.get("randomPrize"));
    }
    
    @Test
    public void shareLv(){
        Boolean aBoolean = actPlantSharedService.shareLv(1000000000002458L);
        System.out.println("分享爱心》》》》》》》》》》》》》》" + aBoolean);
    }
    
    @Test
    public void queryPlantExchange(){
        List<ActPlantLvTranDTO> actPlantLvTranDTOS = actPlantSharedService.queryPlantExchange(ActPlantLvTranPageDTO.builder().pageSize(10).startPage(1).build());
        System.out.println("分享爱心》》》》》》》》》》》》》》" + actPlantLvTranDTOS
        );
    }

    @Test
    public void queryActPlantNotices(){
        PageParam pageParam = new PageParam();
        pageParam.setCurPage(1);
        pageParam.setPageSize(10);
        List<ActPlantNotice> actPlantNoticeList = actPlantSharedService.queryActPlantNotices(pageParam,true);
        System.out.println(" 查询轮播消息 "+ actPlantNoticeList);
    }

}
