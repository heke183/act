package com.xianglin.act.biz.service.implement;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.biz.shared.ActPlantSharedService;
import com.xianglin.act.common.service.facade.ActPlantService;
import com.xianglin.act.common.service.facade.model.ActPlantNoticeDTO;
import com.xianglin.act.common.service.facade.model.PageParam;
import com.xianglin.act.common.service.facade.model.PageResult;
import com.xianglin.act.common.service.facade.model.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author jiang yong tao
 * @date 2018/10/29  15:12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/*.xml")
public class ActPlantServiceImplTest {

    @Autowired
    private ActPlantService actPlantService;

    @Test
    public void queryActPlantNotice() {
        PageParam pageParam = new PageParam();
        pageParam.setCurPage(1);
        pageParam.setPageSize(10);
        Response<PageResult<ActPlantNoticeDTO>> pageResult = actPlantService.queryActPlantNotice(pageParam);
        System.out.println("公告消息列表 = " + JSON.toJSONString(pageResult));
    }

    @Test
    public void updateActPlantNotice() {
        ActPlantNoticeDTO actPlantNoticeDTO = new ActPlantNoticeDTO();
        actPlantNoticeDTO.setId(1L);
        actPlantNoticeDTO.setNotice("环保消息");
        actPlantNoticeDTO.setLink("测试地址");
        actPlantNoticeDTO.setCreator("幸雅丽");
        actPlantNoticeDTO.setStartTime(new Date());
        actPlantNoticeDTO.setEndTime(new Date());
        actPlantNoticeDTO.setCreateTime(new Date());
        actPlantNoticeDTO.setUpdateTime(new Date());
        actPlantNoticeDTO.setIsDeleted("Y");
        Response<Boolean> resp = actPlantService.updateActPlantNotice(actPlantNoticeDTO);
        System.out.println("更新公告消息" + JSON.toJSONString(resp));
    }

    @Test
    public void inserActPlantNotice() {
        ActPlantNoticeDTO actPlantNoticeDTO = new ActPlantNoticeDTO();
        actPlantNoticeDTO.setNotice("环保消息");
        actPlantNoticeDTO.setLink("测试地址");
        actPlantNoticeDTO.setCreator("姜永涛");
        actPlantNoticeDTO.setStartTime(new Date());
        actPlantNoticeDTO.setEndTime(new Date());
        actPlantNoticeDTO.setCreateTime(new Date());
        actPlantNoticeDTO.setUpdateTime(new Date());
        actPlantNoticeDTO.setIsDeleted("Y");
        Response<Boolean> resp = actPlantService.inserActPlantNotice(actPlantNoticeDTO);
        System.out.println("新增公告消息" + JSON.toJSONString(resp));
    }
}