package com.xianglin.act.biz.shared;

import com.xianglin.act.common.dal.model.Activity;
import com.xianglin.act.common.dal.model.ActivityConfig;
import com.xianglin.core.model.vo.UserAddressVo;

import java.util.List;
import java.util.Optional;

/**
 * @author yefei
 * @date 2018-04-10 9:59
 */
public interface ActService {

    /**
     *
     * @return
     */
    List<Activity> actList();

    /**
     * 查询活动
     *
     * @param activityCode
     * @return
     */
    Activity selectAct(String activityCode);

    /**查询全部配置信息
     * @param activityCode
     * @return
     */
    List<ActivityConfig> queryActConfigList(String activityCode);

    /**具体配置查询
     * @param activityCode
     * @param key
     * @return
     */
    Optional<String> queryActConfig(String activityCode,String key);

    /**更新配置信息
     * @param activityCode
     * @param key
     * @param value
     * @return
     */
    boolean updateActConfig(String activityCode,String key,String value);

    /**
     * 新增参数配置
     * @param activityConfig
     * @return
     */
    Long insertActivityConfig(ActivityConfig activityConfig);

    /**
     * 用户实名认证查询
     * @param partyId 用户id
     * @return 是否实名认证
     */
    UserAddressVo userCertification(Long partyId);
}
