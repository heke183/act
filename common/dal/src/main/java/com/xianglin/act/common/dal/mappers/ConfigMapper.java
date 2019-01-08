package com.xianglin.act.common.dal.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @author yefei
 * @date 2018-04-02 17:08
 */
public interface ConfigMapper {

    /**
     * 查询配置项
     *
     * @param configCode
     * @return
     */
    String selectConfig(String configCode);

    /** 更新配置项
     * @param configCode
     * @param configValue
     * @return
     */
    int updateConfig(@Param("configCode")String configCode,@Param("configValue")String configValue);

}
