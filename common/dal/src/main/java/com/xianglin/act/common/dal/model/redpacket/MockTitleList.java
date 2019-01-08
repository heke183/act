package com.xianglin.act.common.dal.model.redpacket;

import java.util.ArrayList;

/**
 * @author yefei
 * @date 2018-04-10 14:41
 */
public class MockTitleList<E> extends ArrayList<E> {

    private MockTitleList() {}

    public static MockTitleList getInstance() {
        return Singleton.instance;
    }

    private static class Singleton {
        public static MockTitleList<MockTitle> instance = new MockTitleList<>();

        static {
            instance.add(new MockTitle("https://cdn02.xianglin.cn/ed5bafec-a9cd-4908-b3c8-377805b2afc6.png","没了对象省了流量","提取了10元", "1天前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/9ba3464b-efcb-48a4-bb25-7d584d13bae8.png","small","提取了20元", "6小时前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/5c378610-271a-48df-8aa4-6bda34f24ab1.png","秋天","提取了4.50元", "8小时前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/0cf3b5ed-c49c-40a1-8808-f479450954e8.png","三小白","提取了3.20元", "1分钟前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/5938c2a8-8f71-48df-9145-386ddf02eaef.png","下滕","提取了1.10元", "7小时前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/df8e2a62-b94b-4aab-8f34-b5b4b2fa4a55.png","牛顿","提取了5.60元", "3小时前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/0f0b95b8-8de8-4cfc-8a5a-95976fc108c6.png","明知不合衬i","提取了9.12元", "2小时前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/9532b135-164b-4246-86e4-da794a026874.png","彼岸花城这是命?","提取了11元", "2天前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/d37f1129-6bd2-41d7-8fc2-8d87b2a60295.png","择城宿栖","提取了1.26元", "2分钟前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/79690b23-8bff-408d-bd05-111dfe3716af.png","旧人旧梦旧时光 ≈","提取了1.23元", "10分钟前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/ad527561-5feb-440d-ba51-78920388f646.png","愿得一人心°","提取了10.11元", "20分钟前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/bc21f168-640e-44be-9d65-7359a17475f6.png","巴西","提取了10.21元", "1小时前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/e41e275c-ba90-4f51-99d8-52ae2b4283e5.png","‖洛可可‖","提取了10.32元", "10小时前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/d632f95f-1bea-4c35-95d6-e3bae6817522.png","欢古","提取了10.21元", "3小时前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/a73c820c-9d09-4188-b5ff-0a0cf003ec1f.png","温存少年","提取了6.41元", "7小时前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/6735c903-c53d-446f-942e-8a62baa4e7f7.png","空城","提取了3.31元", "8小时前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/85ce8b83-39fd-4eec-910e-871578b5b90b.png","盐巴头","提取了7.10元", "4小时前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/f4636de5-a6e1-4189-bab1-4d060aa91d7f.png","狂想曲。","提取了2.11元", "7小时前"));
            instance.add(new MockTitle("https://cdn02.xianglin.cn/df786450-98c7-40ef-b961-edf0f4581bc4.png","少时不狂何时狂","提取了7.11元", "9小时前"));
        }
    }

    static class MockTitle {

        private String headImage;

        private String name;

        private String info;

        private String date;

        public MockTitle() {
        }

        public MockTitle(String headImage, String name, String info, String date) {
            this.headImage = headImage;
            this.name = name;
            this.info = info;
            this.date = date;
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

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
