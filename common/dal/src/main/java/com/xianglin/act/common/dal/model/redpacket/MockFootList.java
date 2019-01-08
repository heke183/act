package com.xianglin.act.common.dal.model.redpacket;

import java.util.ArrayList;

/**
 * @author yefei
 * @date 2018-04-10 14:45
 */
public class MockFootList<E> extends ArrayList<E> {

    private MockFootList() {}

    public static MockFootList getInstance() {
        return Singleton.instance;
    }

    private static class Singleton {
        public static MockFootList<MockFoot> instance = new MockFootList<>();
        static {
            instance.add(new MockFoot("https://cdn02.xianglin.cn/5938c2a8-8f71-48df-9145-386ddf02eaef.png", "夕瑶", "13:10", "9.90"));
            instance.add(new MockFoot("https://cdn02.xianglin.cn/9532b135-164b-4246-86e4-da794a026874.png", "小巴", "3:10", "7.80"));
            instance.add(new MockFoot("https://cdn02.xianglin.cn/d632f95f-1bea-4c35-95d6-e3bae6817522.png", "夏天", "8:10", "30.20"));
        }
    }

    public static class MockFoot {

        private String headImage;

        private String name;

        private String date;

        private String amount;

        public MockFoot() {
        }

        public MockFoot(String headImage, String name, String date, String amount) {
            this.headImage = headImage;
            this.name = name;
            this.date = date;
            this.amount = amount;
        }

        public String getHeadImage() {
            return headImage;
        }

        public void setHeadImage(String headImage) {
            this.headImage = headImage;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("MockFoot{");
            sb.append("headImage='").append(headImage).append('\'');
            sb.append(", name='").append(name).append('\'');
            sb.append(", date='").append(date).append('\'');
            sb.append(", amount='").append(amount).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
