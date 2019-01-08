package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.DynamicPopWindow;
import com.xianglin.act.common.dal.model.PopWindow;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DynamicPopWindowMapper extends Mapper<DynamicPopWindow> {

    List<PopWindow> queryDynamicPopWindow(Long partyId);

}