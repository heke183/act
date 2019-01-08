package com.xianglin.core.service.impl;

import com.xianglin.core.service.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/18 16:01.
 */
@Service("uuidGenerator")
public class StringIdGeneratorImpl implements IdGenerator<String> {

    @Override
    public String generateId() {

        return UUID.randomUUID().toString();
    }
}
