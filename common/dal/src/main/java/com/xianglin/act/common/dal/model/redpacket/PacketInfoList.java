package com.xianglin.act.common.dal.model.redpacket;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author yefei
 * @date 2018-04-04 13:48
 */
public class PacketInfoList<E> extends ArrayList<E> {

    private PacketInfoList(){}

    public static PacketInfoList getInstance() {
        return Singleton.instance;
    }

    public String get() {
        int index = (int) (ThreadLocalRandom.current().nextDouble() * size());
        return (String) super.get(index);
    }

    private static class Singleton {
        public static PacketInfoList<String> instance = new PacketInfoList<>();
        static {
            instance.add("狂撒一个亿，加入我们，人满打款。&点开有惊喜，注册立得3-99元");
            instance.add("就差你了，快帮我点开，一起领福利！&注册立得3-99元，点我领取-->>");
            instance.add("亲，快帮我点一下，马上就有钱拿了&注册立得3-99元，点我领取-->>");
            instance.add("恭喜发财，大吉大利&注册立得3-99元，点我领取-->>");
            instance.add("还差2个人，领完能挣99&我在乡邻APP，每天都在提现，秒到账，速来-->>");
        }
    }
}
