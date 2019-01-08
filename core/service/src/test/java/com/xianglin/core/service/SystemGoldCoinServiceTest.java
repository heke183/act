package com.xianglin.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/20 17:20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/*.xml")
public class SystemGoldCoinServiceTest {

    @Autowired
    private SystemGoldCoinService systemGoldCoinService;

    @Test
    public void dispathCoin2People() {

        Optional<Boolean> aBoolean = systemGoldCoinService.dispathCoin2People(7000038L, 100);
        Assert.assertEquals(aBoolean.get(), true);
    }

    @Test
    public void chargeCoin2System() {

        Optional<Boolean> aBoolean = systemGoldCoinService.chargeCoin2System(7000038L, 100);
        Assert.assertEquals(aBoolean.get(), true);
    }
}