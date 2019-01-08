package com.xianglin.core.service.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.xianglin.act.common.dal.mappers.ActivityMapper;
import com.xianglin.act.common.dal.mappers.DynamicPopWindowMapper;
import com.xianglin.act.common.dal.model.Activity;
import com.xianglin.act.common.dal.model.DynamicPopWindow;
import com.xianglin.act.common.util.EcApis;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.act.common.util.annotation.MqListener;
import com.xianglin.core.model.enums.CifPopTipEvent;
import com.xianglin.act.common.util.BizException;
import com.xianglin.core.model.enums.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 电商打卡奖励优惠券活动
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/17 15:39.
 */
@Component
public class EcRegisterAwardActivity {

    private static final Logger logger = LoggerFactory.getLogger(EcRegisterAwardActivity.class);

    private static final String KEY_PARTY_ID = "partyId";

    private static final String KEY_ROLE_CODE = "roleCode";

    private static final String ROLE_APP_USER = Constants.APP_USER;

    private static final String THIS_ACTIVITY_CODE = "EC_REGISTER_AWARD";

    private static final String SYSTEM = "system";

    @Autowired
    private EcApis ecApis;

    @Autowired
    private DynamicPopWindowMapper dynamicPopWindowMapper;

    @Autowired
    private ActivityMapper activityMapper;

    /**
     * 发放奖励，生成弹窗
     *
     * @param msgs
     * @param context
     * @param body
     * @return
     */
    @MqListener(topic = "CIF_TOPIC", tag = "BINDING_ROLE")
    public boolean resolveRegisterAward(MessageExt msgs, ConsumeConcurrentlyContext context, String body) {

        if (checkIfPopWindowExpire()) {
            logger.info("===========不在电商优惠券活动有效时间内，无法生成奖励 [[ {} ]]===========", body);
            return true;
        }
        try {
            if (body == null || StringUtils.isBlank(body)) {
                return true;
            }
            JSONObject jsonObject = JSON.parseObject(body);
            Long partyId = jsonObject.getLong(KEY_PARTY_ID);
            String roleCode = jsonObject.getString(KEY_ROLE_CODE);
            if (ROLE_APP_USER.equals(roleCode)) {
                doEcAward(partyId);
            }
        } catch (EcAwardException e) {
            logger.info("===========获取电商奖励异常：[[ {} ]]===========", body, e);
        } catch (Exception e) {
            logger.error("===========获取电商奖励异常：[[ {} ]]===========", body, e);
        }
        return true;
    }

    /**
     * 获取电商奖励
     * 插入弹框模板
     * INSERT INTO xlactdb.act_pop_window (ID, ACTIVITY_NAME, POP_WINDOW_CODE, TYPE, TEMPLATE_CODE, SHOW_START_TIME, SHOW_EXPIRE_TIME, ACTIVITY_LOGO, ACTIVITY_LOGO_DEST_URL, LEFT_BUTTON_URL, RIGHT_BUTTON_URL, FREQUENCY, ORDER_NUM, STATUS, IS_DELETED, CREATOR, UPDATER, CREATE_DATE, UPDATE_DATE, COMMENTS)
     * VALUES (15, '电商注册送优惠券', 'EC_REGISTER_AWARD', 'FROM_ACT', '0', NULL, NULL,
     * 'https://cdn02.xianglin.cn/923bd5decf2fde14b59003975e4bbc78-10386.jpg', '跳转地址', NULL, NULL, 1, '1', '0',
     * '0', 'system', 'system', '2018-05-18 17:26:59', '2018-05-18 17:26:59', '电商注册送优惠券');
     *
     * @param partyId
     */
    private void doEcAward(Long partyId) {

        String registerUserAward = null;
        try {
            registerUserAward = ecApis.getRegisterUserAward(partyId.toString());
        } catch (Exception e) {
            logger.error("===========获取用户电商奖励优惠券异常，partyId:[[ {} ]]===========", partyId, e);
        }
        if (registerUserAward == null) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        DynamicPopWindow record = new DynamicPopWindow();
        record.setEventCode(CifPopTipEvent.CIF_BINDING_ROLE.name());
        record.setCreator(SYSTEM);
        record.setUpdater(SYSTEM);
        record.setIsDeleted("0");
        record.setUpdateDate(now);
        record.setCreateDate(now);
        record.setPartyId(partyId);
        record.setContent("恭喜你获得" + registerUserAward + "元优惠券");

        dynamicPopWindowMapper.insert(record);
    }

    /**
     * 检查是否在活动有效时间内
     * 开启活动的sql
     * INSERT INTO xlactdb.act_activity (ID, ACTIVITY_CODE, ACTIVITY_NAME, START_DATE, EXPIRE_DATE, ACTIVITY_MAIN_IMG, ACTIVITY_MAIN_IMG_DEST, IS_DELETED, CREATOR, UPDATER, CREATE_DATE, UPDATE_DATE, COMMENTS)
     * VALUES (NULL, 'EC_REGISTER_AWARD', '电商注册送优惠券', '开始时间', '结束时间',
     * NULL,
     * NULL, '0', 'system', 'system', now(),
     * now(), NULL);
     *   目前只检查活动有效性，准确的做法是检查是否有绑定相关事件的弹窗
     *
     * @return
     */
    private boolean checkIfPopWindowExpire() {

        Activity activity = activityMapper.selectActivity(THIS_ACTIVITY_CODE);
        if (activity == null) {
            logger.info("===========活动尚不存在，活动无效：[[ {} ]]===========", THIS_ACTIVITY_CODE);
            return true;
        }
        Date now = new Date();
        Date startDate = activity.getStartDate();
        Date expireDate = activity.getExpireDate();

        if (now.before(startDate)) {
            return true;
        }

        if (expireDate == null) {
            return false;
        }
        return now.after(expireDate);
    }

    class EcAwardException extends BizException {

        public EcAwardException() {

        }

        public EcAwardException(String message) {

            super(message);
        }

        public EcAwardException(ActPreconditions.ResponseEnum responseEnum) {

            super(responseEnum);
        }

        public EcAwardException(ActPreconditions.ResponseEnum responseEnum, Object result) {

            super(responseEnum, result);
        }
    }
}
