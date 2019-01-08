package com.xianglin.test;

/**
 * @author yefei
 * @date 2018-02-02 14:38
 */
public class StringReplaceTest {

    private final static String NODE_MESSAGE = "亲爱的朋友，您在乡邻中了$，赶紧去我的-优惠券中查看吧，实物会后期主动联系您给予发放";

    public static void main(String[] args) {
        System.out.println(NODE_MESSAGE.replaceFirst("[$]", "二等奖"));
    }
}


