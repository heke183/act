package com.xianglin.act.biz.service.implement;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.common.service.facade.ActPlantService;
import com.xianglin.act.common.service.facade.SysConfigService;
import com.xianglin.act.common.service.facade.model.ActPlantPrizeDTO;
import com.xianglin.act.common.service.facade.model.ActPlantTaskDetailDTO;
import com.xianglin.act.common.service.facade.model.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Describe :
 * Created by xingyali on 2018/8/6 17:34.
 * Update reason :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/*.xml")
public class ActPlantTest {
    
    @Autowired
    private ActPlantService plantService;
    
    @Autowired
    private SysConfigService sysConfigService;
    
    @Test
    public void insertActPlantTaskDetail(){
        ActPlantTaskDetailDTO actPlantTaskDetailDTO = new ActPlantTaskDetailDTO();
        actPlantTaskDetailDTO.setPartyId(1000000000002458L);
        actPlantTaskDetailDTO.setCode("005");
        actPlantTaskDetailDTO.setRefId("1255446211");
        actPlantTaskDetailDTO.setType("PHONE");
        actPlantTaskDetailDTO.setStatus("S");
        Response<Boolean> booleanResponse = plantService.insertActPlantTaskDetail(actPlantTaskDetailDTO);
        System.out.println("booleanResponse:"+booleanResponse);
    }

    public static void main(String[] args) {
        int diff = 181;
        //long seconds = Duration.between(LocalDate.now().plusDays(1), LocalDate.now()).getSeconds();
        System.out.println(diff+ " = " + diff);
        if(diff<=5*60){ //可以
            int a= diff / 60;
           int b= diff % 60;
            System.out.println(diff+ " = 00:" + (a>10?a:"0"+a)+":"+(b>10?b:"0"+b));
        }
        
    }
    
    
    @Test
    public void queryActPlantPrize(){
        Response<List<ActPlantPrizeDTO>> listResponse = plantService.queryActPlantPrize();
        System.out.println("查询兑换的礼品:"+listResponse);
    }
    
    @Test
    public void querySysConfigVaule(){
        Response<String> plant_prize = sysConfigService.querySysConfigVaule("PLANT_PRIZE");
        System.out.println("根据code查询参数配置:"+plant_prize);
    }
    
}
