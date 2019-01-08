package com.xianglin.act.biz.service.implement;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.xianglin.act.common.service.facade.ActService;
import com.xianglin.act.common.service.facade.StepService;
import com.xianglin.act.common.service.facade.constant.ActivityConfig;
import com.xianglin.act.common.service.facade.model.ActStepDetailDTO;
import com.xianglin.act.common.service.facade.model.ActivityDTO;
import com.xianglin.act.common.service.facade.model.Response;
import com.xianglin.act.common.util.SessionHelper;
import com.xianglin.gateway.common.service.spi.util.GatewayRequestContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 22:35.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/*.xml")
public class ActServiceImplTest {

    @Autowired
    private ActService actService;

    @Autowired
    private SessionHelper sessionHelper;

    @Test
    public void testOne() throws Exception {

        //GatewayRequestContext.setSessionId(UUID.randomUUID().toString());
        //sessionHelper.getSession().setAttribute("partyId", 1000000000002185L);
        Response<List<ActivityDTO>> listResponse = actService.selectAct();
        System.out.println(JSON.toJSONString(listResponse, true));

    }

    @Test
    public void insertActivityConfig(){
        String activityCode = ActivityConfig.ActivityCode.HD001.name();
        Map<String,String> config = Maps.newHashMap();
        config.put(ActivityConfig.ActivityVote.ACTIVITY_CONTENT.name(),"投票活动");
        config.put(ActivityConfig.ActivityVote.ACT_RULER_TYPE.name(),"活动规则Type");
        config.put(ActivityConfig.ActivityVote.ACT_RULER.name(),"活动规则");
        config.put(ActivityConfig.ActivityVote.TITLE.name(),"活动标题");
        config.put(ActivityConfig.ActivityVote.LEAST_PARTAKE.name(),"最少活动人数");
        config.put(ActivityConfig.ActivityVote.EDIT_STYLE_BANNER.name(),"活动banner图");
        config.put(ActivityConfig.ActivityVote.COUNT_DOWN_TYPE.name(),"活动倒计时Type");
        config.put(ActivityConfig.ActivityVote.COUNT_DOWN.name(),"活动倒计时");
        config.put(ActivityConfig.ActivityVote.BACKGROUND_IMG_TYPE.name(),"编辑背景Type");
        config.put(ActivityConfig.ActivityVote.BACKGROUND_IMG.name(),"编辑背景");
        config.put(ActivityConfig.ActivityVote.REGISTER_BUTTON_IMG.name(),"报名按钮");
        config.put(ActivityConfig.ActivityVote.MAIN_BUTTON_IMG.name(),"我的按钮");
        config.put(ActivityConfig.ActivityVote.VOTE_BUTTON_IMG.name(),"投票按钮");
        config.put(ActivityConfig.ActivityVote.ORDER.name(),"排序规则");
        config.put(ActivityConfig.ActivityVote.PUSH_REGISTER.name(),"开启报名");
        config.put(ActivityConfig.ActivityVote.REGISTER_START_TIME.name(),"活动报名开始时间");
        config.put(ActivityConfig.ActivityVote.REGISTER_END_TIME.name(),"活动报名截止时间");
        config.put(ActivityConfig.ActivityVote.REGISTER_CONTENT.name(),"报名须知");
        config.put(ActivityConfig.ActivityVote.LIMIT_PEOPLE_TYPE.name(),"是否限制人数");
        config.put(ActivityConfig.ActivityVote.MAX_PEOPLE_NUM.name(),"最大报名人数");
        config.put(ActivityConfig.ActivityVote.VOTE_START_TIME.name(),"投票开始时间");
        config.put(ActivityConfig.ActivityVote.VOTE_END_TIME.name(),"投票结束时间");
        config.put(ActivityConfig.ActivityVote.VOTE_TYPE.name(),"投票类型");
        config.put(ActivityConfig.ActivityVote.VOTE_PEOPLE_NUM.name(),"每人每日可以为几名选手投票");
        config.put(ActivityConfig.ActivityVote.VOTE_CONTENT.name(),"投票须知");
        config.put(ActivityConfig.ActivityVote.VOTE_REWARD_TYPE.name(),"投票是否发放金币");
        config.put(ActivityConfig.ActivityVote.VOTE_MIN_GOLD.name(),"投票最少获得金币");
        config.put(ActivityConfig.ActivityVote.VOTE_MAX_GOLD.name(),"投票最多获得金币");
        config.put(ActivityConfig.ActivityVote.LOGO_QR_TYPE.name(),"是否显示二维码/LOGO");
        config.put(ActivityConfig.ActivityVote.LOGO_CODE.name(),"二维码/LOGO");
        config.put(ActivityConfig.ActivityVote.WECHAT_SHARED_IMG.name(),"微信分享图标");
        config.put(ActivityConfig.ActivityVote.WECHAT_TITLE.name(),"微信分享标题");
        config.put(ActivityConfig.ActivityVote.WECHAT_CONTENT.name(),"微信分享内容");
        config.put(ActivityConfig.ActivityVote.SHARED_TYPE.name(),"分享类型(路径)");
        config.put(ActivityConfig.ActivityVote.NEED_USER_REGISTER.name(),"非注册用户是否需要注册才可投票");
        config.put(ActivityConfig.ActivityVote.VOTE_IMG.name(),"投票按钮图片");
        config.put(ActivityConfig.ActivityVote.JOIN_IMG.name(),"参与按钮图片");
        Response<Boolean>  response = actService.insertActivityConfig(activityCode,config);
        System.out.println("新增参数配置 " + JSON.toJSONString(response));
    }

    @Test
    public void queryActConfig(){
        String activityCode = ActivityConfig.ActivityCode.HD001.name();
        Response<Map<String,String>> result = actService.queryActConfig(activityCode);
        System.out.println("根据活动code查询配置参数 " + JSON.toJSONString(result));
    }
}