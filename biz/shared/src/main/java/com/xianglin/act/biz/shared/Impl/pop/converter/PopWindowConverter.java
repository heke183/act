package com.xianglin.act.biz.shared.Impl.pop.converter;

import com.xianglin.act.common.dal.model.PopWindow;
import com.xianglin.act.common.service.facade.constant.PopTipTypeEnum;
import com.xianglin.act.common.service.facade.model.ActivityDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * act_pop_window
 * 弹窗提示表DO映射DTO
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 10:16.
 */
@Component
public class PopWindowConverter extends IPopTipConverter<PopWindow> {

    @Override
    public ActivityDTO converter(PopWindow input) {

        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setId(input.getId());
        activityDTO.setShow(true);
        String templateCode = input.getTemplateCode();
        Integer showType = Integer.valueOf(templateCode);
        PopTipTypeEnum popTipType = PopTipTypeEnum.vauleOfCode(showType);
        activityDTO.setShowType(showType);
        activityDTO.setPopTipType(popTipType);
        activityDTO.setShowStartTime(input.getShowStartTime());
        activityDTO.setShowExpireTime(input.getShowExpireTime());
        activityDTO.setShowMessage(null);
        activityDTO.setShowStartTime(input.getShowStartTime());
        activityDTO.setShowExpireTime(input.getShowExpireTime());
        activityDTO.setActivityLogo(input.getActivityLogo());
        activityDTO.setActivityLogoDestUrl(input.getActivityLogoDestUrl());
        activityDTO.setLeftButtonUrl(input.getLeftButtonUrl());
        activityDTO.setRightButtonUrl(input.getRightButtonUrl());
        activityDTO.setFrequency(input.getFrequency());
        String orderNum = input.getOrderNum();
        activityDTO.setOrderNum(Integer.valueOf(orderNum));
        //活动产生的动态弹框，则取活动生成的showMessage
        String content = input.getContent();
        if (!StringUtils.isBlank(content)) {
            activityDTO.setShowMessage(content);
        }
        ////两秒跳转文案
        //activityDTO.setShowMessage(input.getActivityName());
        return activityDTO;
    }

}
