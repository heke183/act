package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.AttendanceCoinAward;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AttendanceCoinAwardMapper extends Mapper<AttendanceCoinAward> {

    List<AttendanceCoinAward> getUnmutifyAwards();
}