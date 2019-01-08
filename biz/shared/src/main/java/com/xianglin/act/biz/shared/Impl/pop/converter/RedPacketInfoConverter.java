package com.xianglin.act.biz.shared.Impl.pop.converter;

import com.xianglin.act.common.dal.model.redpacket.RedPacketInfo;
import com.xianglin.act.common.service.facade.model.ActivityDTO;

/**
 * act_pop_window
 * 弹窗提示表DO映射DTO
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 10:16.
 */
public class RedPacketInfoConverter extends IPopTipConverter<RedPacketInfo> {

    private final static String MESSAGE = "您有活动未完成！\n马上进入";

    private String url;

    @Override
    public ActivityDTO converter(RedPacketInfo input) {

        ActivityDTO activityDTO = new ActivityDTO();

        activityDTO.setId(null);
        activityDTO.setShow(true);
        activityDTO.setPopTipType(null);
        activityDTO.setShowType(null);
        activityDTO.setShowMessage(MESSAGE);
        activityDTO.setShowStartTime(null);
        activityDTO.setShowExpireTime(null);
        activityDTO.setActivityLogo(null);
        activityDTO.setActivityLogoDestUrl(url);
        activityDTO.setLeftButtonUrl(null);
        activityDTO.setRightButtonUrl(null);
        activityDTO.setFrequency(null);
        activityDTO.setOrderNum(null);
        return activityDTO;
    }

    public void setUrl(String url) {

        this.url = url;
    }
}
