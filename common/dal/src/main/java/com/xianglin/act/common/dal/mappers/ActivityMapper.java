package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.Activity;
import com.xianglin.act.common.dal.model.VoteActivity;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * The interface Activity mapper.
 *
 * @author yefei
 * @date 2018 -01-22 9:24
 */
public interface ActivityMapper extends Mapper<Activity> {

    /**
     * Select activity activity.
     *
     * @param activityCode the activity code
     * @return the activity
     */
    Activity selectActivity(String activityCode);

    /**
     * 精彩活动列表
     *
     * @return
     */
    List<Activity> selectActList();

    /**
     * 按活动code查询投票活动
     *
     * @param activityCode
     * @return
     */
    VoteActivity selectVoteActByActCode(String activityCode);

    /**
     * 更新活动规则
     *
     * @param activityCode
     * @param desc
     * @return
     */
    int updateActDesc(@Param("activityCode") String activityCode, @Param("desc") String desc);
}
