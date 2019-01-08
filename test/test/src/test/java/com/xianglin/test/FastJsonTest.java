package com.xianglin.test;

import com.alibaba.fastjson.JSON;
import com.xianglin.act.common.dal.model.redpacket.Sharer;
import com.xianglin.act.common.dal.model.redpacket.SharerInfo;

/**
 * @author yefei
 * @date 2018-04-09 17:11
 */
public class FastJsonTest {

    public static void main(String[] args) {
        Sharer sharer = new SharerInfo();
        System.out.println(sharer.isSharer());
        System.out.println(JSON.toJSONString(sharer));
    }

}
