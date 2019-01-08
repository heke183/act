package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author jiang yong tao
 * @date 2018/8/3  9:45
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/*.xml")
public class ConfigMapperTest {

    @Autowired
    private ConfigMapper configMapper;

    @Test
    public void selectConfig() {
        String str = "MID_AUTUMN_ACTIVITY_START_TIME";
        String stop = configMapper.selectConfig(str);
        System.out.println("stopDate>>>>>>>=" + stop);
    }
}