package com.xianglin.core.model.enums;


import lombok.experimental.var;
import org.omg.CORBA.PRIVATE_MEMBER;

/**
 * 种树活动相关美剧
 */
public class ActPlantEnum {

    /**
     * 爱心交易类型
     */
    public enum TranType{
        COLLECT("1001","收取"),
        EXANGE("1002","兑换"),
        LUCKDRAW("1003","大转盘抽奖"),
        PRIZE("1004","大转盘中奖"),
        STEP("1005","步步生金兑换"),
        ;

        public String code;
        public String desc;

        TranType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /**
     * tip消息类型
     */
    public enum TipType{
        TIP("提示消息"),
        LEVEL("小树又长大了"),
        INVITE("邀请"),
        RANDOMPRIZE("随机奖励")
        ;

        public String desc;

        private TipType(String desc) {

            this.desc = desc;
        }
    }

    /**
     *用户任务类型 
     */
    public enum ActPlantTaskTypeEnum {
        USERTASK("用户任务"),
        REGISTER("注册"),
        ;

        public String desc;

        private ActPlantTaskTypeEnum(String desc) {

            this.desc = desc;
        }
    }

    /**
     *用户任务code类型 
     */
    public enum ActPlantTaskCodeEnum {
        SHARE_RECEIVE_LOVE("001", "分享领取爱心"),
        STEP("008", "步步生金"),
        INVITE("003", "邀请好友来种树"),
        DEPOSIT("004", "存款"),
        PHONE("005", "手机充值"),
        SHOPPING("006", "购物"),
        REGISTER("007", "新用户注册"),
        PUNCH_IN("002","去打卡"),
        LUCKY_BAG("009","福袋"),
        ;
        public String desc;
        
        public String value;

        ActPlantTaskCodeEnum(String desc, String value) {

            this.desc = desc;
            this.value = value;
        }
    }

    /**
     *用户任务code类型 
     */
    public enum ActPlantPrizeCodeEnum {
        PAPER("1001", "卷纸"),
        T_SHIRT("1002", "短袖"),
        CUP("1003", "保温杯"),
        MONEY("1004", "50元现金"),
        ;
        

        public String desc;

        public String value;

        ActPlantPrizeCodeEnum(String desc, String value) {

            this.desc = desc;
            this.value = value;
        }
    }

    /**
     *用户任务code类型 
     */
    public enum DeleteTypeEnum {
        Y("已删除"),
        N("未删除"),
        ;
        public String desc;

        private DeleteTypeEnum(String desc) {

            this.desc = desc;
        }
    }

    public enum ActPrizeType {
        GOLD("GOLD","金币"),
        GOODS("GOODS","物品(实物)"),
        VOUCHERS("VOUCHERS","话费券"),
        ;
        private String code;
        private String desc;

        public String getCode() {
            return code;
        }

        ActPrizeType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    public enum StatusType{
        I("初始值"),
        A("审核中"),
        F("失败"),
        S("成功"),
        ;
        public String desc;

        private StatusType(String desc) {

            this.desc = desc;
        }
    }

    /**
     * 种树活动奖励
     */
    public enum ActPlantGoldType{

        ACT_PLANT_GOLD_TYPE("福利树活动随机奖励"),
        ACT_PLANT_GOLD_TURNPLATE("福利树活动大转盘奖励"),
        ACT_PLANT_GOLD_PRIZE("福利树活动礼品"),

        ACT_PLANT_TREE_FIRST("种树活动第一名"),
        ACT_PLANT_TREE_SECOND("种树活动第二名"),
        ACT_PLANT_TREE_THRID("种树活动第三名"),
        ;

        public String desc;

        ActPlantGoldType(String desc) {
            this.desc = desc;
        }
    }


    /**
     * 分享时随机奖励
     */
    public enum ActPlantRandom{

        ACT_PLANT_100_GOLD_RANDOM("101","恭喜你，随机获得100金币!"),
        ACT_PLANT_2_TICKET("102","恭喜你，随机获得2元话费券!"),
        ACT_PLANT_5_TICKET("103","恭喜你，随机获得5元优惠券!")
        ;
        private String code;
        private String desc;

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        ActPlantRandom(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /**
     * 用户的角色类型
     */
    public enum UserType{

        APP_USER("普通用户"),
        EMPLOYEE("员工"),
        NODE_MANAGER("站长")
        ;
    private String desc;

        UserType(String desc) {
            this.desc = desc;
        }
    }


    public enum InviteStatus{

        APPLY_NOSTART("1001","报名未开始"),
        NOT_APPLY("1002","未报名"),
        APPLYED("1003","已报名"),
        APPLY_END("1004","报名结束")
        ;

        private String code;
        private String desc;

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        InviteStatus(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    public enum InviteLimit{

        INVITE_LIMIT(100,"好友争霸报名上限为100人!");

        private Integer num;
        private String desc;

        public Integer getNum() {
            return num;
        }

        public String getDesc() {
            return desc;
        }

        InviteLimit(Integer num, String desc) {
            this.num = num;
            this.desc = desc;
        }
    }

    public enum PlantFestivalCode {
        NEWYEAR("元旦"),
        SPRING("春节"),
        QINGMING("清明"),
        LABOUR("劳动"),
        DRAGONBOAT("端午"),
        MID("中秋"),
        NATIONAL("国庆"),
        ;
        public String desc;

        PlantFestivalCode(String desc) {
            this.desc = desc;
        }

        public static String getActCode(String str) {
            String actCode;
            switch (str) {
                case "NEWYEAR":
                    actCode = NEWYEAR.name();
                    break;
                case "SPRING":
                    actCode = SPRING.name();
                    break;
                case "QINGMING" :
                    actCode = QINGMING.name();
                    break;
                case "LABOUR" :
                    actCode = LABOUR.name();
                    break;
                case "DRAGONBOAT" :
                    actCode = DRAGONBOAT.name();
                    break;
                case "MID" :
                    actCode = MID.name();
                    break;
                case "NATIONAL" :
                    actCode = NATIONAL.name();
                    break;
                default: actCode = "NORMAL";
            }
            return actCode;
        }
    }
}
