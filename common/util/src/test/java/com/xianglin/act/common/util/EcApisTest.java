package com.xianglin.act.common.util;

import org.junit.Test;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/17 17:00.
 */
public class EcApisTest {

    @Test
    public void testEcAward() {

        EcApis ecApis = new EcApis();
        ecApis.setEcHostUrl("https://mai-dev2.xianglin.cn");
        ecApis.getRegisterUserAward("12432423");

    }

    @Test
    public void testProps() {

        String hello = ConfigPropertyUtils.get("h5.server.url");
        System.out.println(hello);

    }
}