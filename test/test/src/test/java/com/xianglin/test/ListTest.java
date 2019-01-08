package com.xianglin.test;

import com.xianglin.act.common.dal.model.redpacket.MockFootList;

/**
 * @author yefei
 * @date 2018-04-10 15:13
 */
public class ListTest {

    public static void main(String[] args) {
        MockFootList instance = MockFootList.getInstance();
        instance.add(0, new MockFootList.MockFoot("https://cdn02.xianglin.cn/d632f95f-1bea-4c35-95d6-e3bae6817522.png", "qiu天", "8:10", "8.2"));
        System.out.println(instance);
    }
}
