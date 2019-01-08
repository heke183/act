package com.xianglin.act.biz.shared;

import com.xianglin.act.common.dal.support.pop.PopTipRequest;
import com.xianglin.act.common.service.facade.model.ActivityDTO;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;
import java.util.Optional;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/13 1:03.
 */
public interface PopTipAssembleService{

    Optional<List<ActivityDTO>> assemblePopTips(PopTipRequest request);
}
