package com.xianglin.act.biz.shared.Impl;

import com.xianglin.act.biz.shared.ActInviteSharedService;
import com.xianglin.act.biz.shared.GamePlaneSharedService;
import com.xianglin.act.biz.shared.StepSharedService;
import com.xianglin.act.common.dal.model.ActGamePlane;
import com.xianglin.act.common.dal.model.ActStepDetail;
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

/**
 * @author jiang yong tao
 * @date 2018/8/24  10:55
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/*.xml")
public class GamePlaneServiceImplTest {

    @Autowired
    private GamePlaneSharedService gamePlaneSharedService;

    @Test
    public void start(){
        ActGamePlane result = gamePlaneSharedService.start(11314244325L);
        System.out.println(result);
    }

    @Test
    public void queryWeekRanking(){
        List<ActGamePlane> result = gamePlaneSharedService.queryWeekRanking();
        System.out.println(result);
    }

    @Test
    public void reward(){
        ActGamePlane para = ActGamePlane.builder().id(10L).partyId(11314244325L).score(102212).stage(4).shotCount(234).build();
        ActGamePlane result = gamePlaneSharedService.reward(para);
        System.out.println(result);
    }
}
