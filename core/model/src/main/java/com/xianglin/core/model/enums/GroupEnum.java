package com.xianglin.core.model.enums;

import org.apache.commons.collections.ListUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * @author jiang yong tao
 * @date 2018/12/19  14:44
 */
public class GroupEnum {

    public enum GroupTranType {
        RAFFLE("抽奖",10000L),
        WITHDRAW("提现",10000L),
        EXCHANGE("兑换",20000L),
        DISMANTLE("拆红包花费",30000),
        DISMANTLE_IN("拆红包收入",30000),
        ;
        String desc;

        /**
         * 对应的系统用户
         */
        Long sysPartyId;

        public String getDesc() {
            return desc;
        }

        public Long getSysPartyId() {
            return sysPartyId;
        }

        GroupTranType(String desc, long sysPartyId) {
            this.desc = desc;
            this.sysPartyId = sysPartyId;
        }
    }

    public enum GroupInfoStyle {
        GXFC("恭喜发财"),
        GNYS("狗年运势"),
        GHZF("恭贺祝福"),
        GX("感谢"),
        YNZH("有你真好"),;
        String desc;

        GroupInfoStyle(String desc) {
            this.desc = desc;
        }
    }

    public enum GroupInfoStatus {
        I("进行中"),
        S("已成团"),
        F("失效"),;
        public  String desc;

        GroupInfoStatus(String desc) {
            this.desc = desc;
        }
    }

    public enum UserJoinGroupStatus {
        S("组团成功"),
        F("人未满，组团失败"),
        END("你今天红包已领完！不可助力好友拆红包哟！"),
        ONE("好友仅可互相助力一次，去开团发红包吧！"),
        JOIN("你已参过好友团,仅可助力好友一次，去开团抢红包吧!"),
        NEW("当前团限新用户可领，去开团抢红包吧!"),
        FULL("此团已满，去开团抢现金红包吧！"),;
        public  String desc;

        UserJoinGroupStatus(String desc) {
            this.desc = desc;
        }
    }
    
    

    public enum GroupInfoDetailStatus {
        I("进行中"),
        S("发放成功"),
        F("失效"),;
        String desc;

        GroupInfoDetailStatus(String desc) {
            this.desc = desc;
        }
    }

    public enum GroupTipsType {
        RAFFLE("抽奖"),
        RAFFLESUCCEFUL("抽奖成功"),
        EXCHANGE("兑换成功"),
        WITHDRAW("提现成功"),
        SENDREDPACKET("已成团给用户发红包提示"),
        GET("用户领取的团长的红包"),
        SUCCESS("组团成功提示"),
        FAIL("组团失败提示"),;
        public String desc;

        public String getDesc() {
            return desc;
        }

        GroupTipsType(String desc) {
            this.desc = desc;
        }
    }


    public enum GroupExchangeType {
        O("订单"),
        E("优惠券"),
        R("红包明细"),
        ;
        String desc;

        GroupExchangeType(String desc) {
            this.desc = desc;
        }
    }

    /**
     * 拆红包的红包类型
     */
    public enum DismantlePacketType {
        TOW("2元", 2,1) {
            @Override
            public List<BigDecimal> randomPacket() {
                return super.randomPacket(4, 10, 1, 4);
            }
        },
        FIVE("5元", 5,2) {
            @Override
            public List<BigDecimal> randomPacket() {
                return super.randomPacket(20, 50, 1, 20);
            }
        },
        TEN("10元", 10,5) {
            @Override
            public List<BigDecimal> randomPacket() {
                return super.randomPacket(40, 100, 5, 40);
            }
        },
        TWENTY("20元", 20,10) {
            @Override
            public List<BigDecimal> randomPacket() {
                return super.randomPacket(80, 200, 10, 80);
            }
        },;
        private String desc;

        private Integer value;

        private Integer basicValue;

        public String getDesc() {
            return desc;
        }

        public Integer getValue() {
            return value;
        }

        public Integer getBasicValue() {
            return basicValue;
        }

        public abstract List<BigDecimal> randomPacket();

        private List<BigDecimal> randomPacket(int highMin, int highMax, int lowMin, int lowMax) {
            return CompletableFuture.supplyAsync(() -> {
                return new ArrayList<BigDecimal>(4) {{
                    IntStream.range(1, 5).forEach(v -> {
                        add(new BigDecimal(ThreadLocalRandom.current().nextInt(highMin, highMax)+""));

                    });
                }};
            }).thenCombine(CompletableFuture.supplyAsync(() -> {
                return new ArrayList<BigDecimal>(3) {{
                    IntStream.range(1, 4).forEach(v -> {
                        add(new BigDecimal(ThreadLocalRandom.current().nextInt(lowMin, lowMax)+""));
                    });
                }};
            }), (v1, v2) -> {
                v1.addAll(v2);
                Collections.shuffle(v1);
                return v1;
            }).join();
        }

        DismantlePacketType(String desc, Integer value,Integer basicValue) {
            this.desc = desc;
            this.value = value;
            this.basicValue = basicValue;
        }
    }

}
