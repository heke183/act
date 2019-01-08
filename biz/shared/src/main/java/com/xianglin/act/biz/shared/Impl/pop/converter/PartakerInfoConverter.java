package com.xianglin.act.biz.shared.Impl.pop.converter;

import com.xianglin.act.biz.shared.PopTipTouchEvent;
import com.xianglin.act.common.dal.enums.PrizeEnum;
import com.xianglin.act.common.dal.model.redpacket.PartakerInfo;
import com.xianglin.act.common.service.facade.model.ActivityDTO;
import com.xianglin.act.common.util.SessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.MessageFormat;

import static com.xianglin.act.common.service.facade.constant.PopTipTypeEnum.POP_TIP_OF_ONE_BUTTON;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 21:48.
 */
@Component
public class PartakerInfoConverter extends IPopTipConverter<PartakerInfo> {

    public static final String YOU_HUI_QUAN_MESSAGE = "恭喜你，获得{0}元优惠券  去我的-优惠券中查看吧";

    public static final String JIN_BI_MESSAGE = "恭喜你，获得{0}元价值金币 ，可提现喔。去我的-金币中查看吧";

    private static final String PIC_URL = "https://cdn01.xianglin.cn/a07806ce2a0bf37077284f3f0b77b3b8-246683.png";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SessionHelper sessionHelper;

    @Override
    public ActivityDTO converter(PartakerInfo input) {

        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setShow(true);
        BigDecimal prizeValue = input.getPrizeValue().setScale(2, BigDecimal.ROUND_HALF_UP);
        if (PrizeEnum.EC_COUPON.name().equals(input.getPrizeCode())) {
            activityDTO.setShowMessage(MessageFormat.format(YOU_HUI_QUAN_MESSAGE, prizeValue.toPlainString()));
        } else if (PrizeEnum.XL_GOLD_COIN.name().equals(input.getPrizeCode())) {
            activityDTO.setShowMessage(MessageFormat.format(JIN_BI_MESSAGE, prizeValue.toPlainString()));
        }
        activityDTO.setActivityLogo(PIC_URL);
        Long partyId = sessionHelper.getCurrentPartyId();
        PopTipTouchEvent popTipColseEvent = new PopTipTouchEvent(this, partyId, POP_TIP_OF_ONE_BUTTON.getCode(), partyId);
        applicationContext.publishEvent(popTipColseEvent);

        return activityDTO;
    }
}
