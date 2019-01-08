package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.ActivityConfig;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;


/**
 * @author wanglei
 */
public interface ActivityConfigMapper extends Mapper<ActivityConfig> {

    /**根据活动code和key更新值
     * @param activityCode
     * @param key
     * @param value
     * @return
     */
    @Update("update act_activity_config set CONFIG_VALUE = #{value},update_time = NOW() where ACTIVITY_CODE = #{activityCode} and CONFIG_KEY = #{key}")
    int updateByCodeAndKey(@Param("activityCode") String activityCode, @Param("key") String key, @Param("value") String value);
}
