package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.ActInvite;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Describe :
 * Created by xingyali on 2018/8/23 14:17.
 * Update reason :
 */
public interface ActInviteMapper extends Mapper<ActInvite> {

    /**
     * 查询排行榜
     * @return
     */
    List<ActInvite> selectActIviteList();

}
