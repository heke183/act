package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.annotation.PopTipMapper;
import com.xianglin.act.common.dal.annotation.PopTipSelector;
import com.xianglin.act.common.dal.model.PopWindow;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@PopTipMapper
public interface PopWindowMapper extends Mapper<PopWindow> {

    @PopTipSelector(returnType = 5)
    List<PopWindow> queryPopWindowList();
}