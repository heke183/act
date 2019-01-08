package com.xianglin.act.biz.shared;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.common.util.GlobalRequestContext;
import com.xianglin.core.model.vo.VoteActivityBaseInfoVO;
import com.xianglin.core.model.vo.VoteRecord;
import com.xianglin.core.model.vo.VoterVO;
import com.xianglin.fala.session.MapSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author yefei
 * @date 2018-06-19 13:55
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/vote2.xml"})
public class VoteOfWorldCupServiceTest {

    @Resource
    private VoteOfWorldCupService voteOfWorldCupService;

    @Before
    public void before() {

        voteOfWorldCupService.getVoteActivity("ACT_VOTE_WORLD_CUP");
        MapSession session = new MapSession();
        GlobalRequestContext.setSession(session);
        session.setAttribute("partyId", 5199881L);
    }

    @Test
    public void index() {
        VoteActivityBaseInfoVO vo = voteOfWorldCupService.index();
        System.out.println(JSON.toJSONString(vo, true));
    }

    @Test
    public void vote() {
        VoterVO vote = voteOfWorldCupService.vote(11365);
        System.out.println(JSON.toJSONString(vote, true));
    }

    @Test
    public void voteSubmit() {
        voteOfWorldCupService.voteSubmit(11369, BigDecimal.valueOf(300));
    }

    @Test
    public void voteRecord() {
        List<VoteRecord> voteRecords = voteOfWorldCupService.voteRecord();
        System.out.println(JSON.toJSONString(voteRecords, true));
    }
}
