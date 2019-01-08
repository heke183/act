package com.xianglin.act.biz.shared.Impl;

import com.xianglin.act.biz.shared.ActInviteSharedService;
import com.xianglin.act.common.service.facade.model.ActInviteDTO;
import com.xianglin.act.common.service.facade.model.PageParam;
import com.xianglin.act.common.service.facade.model.PageResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Describe :
 * Created by xingyali on 2018/8/23 16:36.
 * Update reason :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/*.xml")
public class ActInviteServiceImplTest {

    @Autowired
    private ActInviteSharedService actInviteSharedService;
    
    @Test
    public void queryInviteList(){
       PageParam<ActInviteDTO> pageParam = new PageParam<ActInviteDTO>();
        ActInviteDTO actInviteDTO = new ActInviteDTO();
        actInviteDTO.setStartDate("2018-08-27");
        actInviteDTO.setEndDate("2018-08-27");
        actInviteDTO.setUser("李锦文2");
        pageParam.setParam(actInviteDTO);
       // PageResult<ActInviteDTO> pageResult = actInviteSharedService.queryInviteList();
       // System.out.println("top端查询地推用户===================>>>"+pageResult);
        
    }
}
