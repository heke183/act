package com.xianglin.act.biz.service.implement;

import com.xianglin.act.common.service.facade.ActPlantService;
import com.xianglin.act.common.service.facade.GamePlaneService;
import com.xianglin.act.common.service.facade.SysConfigService;
import com.xianglin.act.common.service.facade.model.ActPlantPrizeDTO;
import com.xianglin.act.common.service.facade.model.ActPlantTaskDetailDTO;
import com.xianglin.act.common.service.facade.model.GamePlaneDTO;
import com.xianglin.act.common.service.facade.model.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Describe :
 * Created by xingyali on 2018/8/6 17:34.
 * Update reason :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/*.xml")
public class GamePlaneTest {
    
    @Autowired
    private GamePlaneService service;
    
    @Test
    public void queryActPlantPrize(){
        Response<List<GamePlaneDTO>> listResponse = service.weekRanking(7005850L);
        System.out.println("查询兑换的礼品:"+listResponse);
    }


    @Test
    public void reward(){
        Response<GamePlaneDTO> listResponse = service.reward(GamePlaneDTO.builder().id(11L).partyId(1000000000002429L).stage(1).shotCount(100).coinReward(11).build());
        System.out.println("查询兑换的礼品:"+listResponse);
    }
}
