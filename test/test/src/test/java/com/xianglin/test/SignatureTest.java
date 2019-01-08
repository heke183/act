package com.xianglin.test;

import com.xianglin.act.common.util.MD5;

/**
 * @author yefei
 * @date 2018-01-27 13:51
 */
public class SignatureTest {

    public static void main(String[] args) throws Exception {
        String s = "5199881" + "20180127";


        System.out.println( MD5.encode(s));
    }
}
