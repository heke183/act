package com.xianglin.test;

import org.junit.Test;

import java.util.UUID;

/**
 * @author yefei
 * @date 2018-03-29 9:43
 */
public class UUIDTest {

    @Test
    public void uuid() {
        String string = UUID.randomUUID().toString();
        System.out.println(string.replace("-","").toString());
    }
}
