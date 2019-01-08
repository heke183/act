package com.xianglin.core.service.impl;

import com.xianglin.act.common.dal.mappers.SequenceMapper;
import com.xianglin.core.service.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/18 16:00.
 */
@Service("dbIdGenerator")
public class LongIdGeneratorImpl implements IdGenerator<Long> {

    @Autowired
    private SequenceMapper sequenceMapper;

    @Override
    public Long generateId() {

        return sequenceMapper.getSequence();
    }
}
