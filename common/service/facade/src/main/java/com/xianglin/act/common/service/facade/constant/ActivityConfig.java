package com.xianglin.act.common.service.facade.constant;

/**
 * 活动配置
 */
public class ActivityConfig {

    /**
     * 投票活动
     */
    public enum ActivityVote{

        //首页基础设置
        TITLE("活动标题"),
        LEAST_PARTAKE("最少参与人数"),
        EDIT_STYLE_BANNER("活动banner图"),
        BANNER_DEF("活动Banner默认图"),
        COUNT_DOWN_TYPE("倒计时Type"),
        COUNT_DOWN("活动倒计时"),
        ACT_RULER_TYPE("活动规则Type"),
        ACT_RULER("活动规则"),
        BACKGROUND_IMG_TYPE("编辑背景Type"),
        BACKGROUND_IMG("编辑背景"),
        REGISTER_BUTTON_IMG("报名按钮"),
        REGISTER_BUTTON_IMG_DEF("报名按钮默认图片"),
        MAIN_BUTTON_IMG("我的按钮"),
        MAIN_BUTTON_IMG_DEF("我的按钮默认图片"),
        VOTE_BUTTON_IMG("投票按钮"),
        VOTE_BUTTON_IMG_DEF("投票按钮默认图片"),
        ORDER("排序规则"),
        ACTIVITY_CONTENT("活动介绍"),

        //报名投票设置
        PUSH_REGISTER("开启报名"),
        REGISTER_START_TIME("活动报名开始时间"),
        REGISTER_END_TIME("活动报名截止时间"),
        REGISTER_CONTENT("报名须知"),
        LIMIT_PEOPLE_TYPE("是否限制人数"),
        MAX_PEOPLE_NUM("最大报名人数"),
        VOTE_START_TIME("投票开始时间"),
        VOTE_END_TIME("投票结束时间"),
        VOTE_TYPE("投票类型"),
        VOTE_PEOPLE_NUM("每人每日可以为几名选手投票"),
        VOTE_CONTENT("投票须知"),
        VOTE_REWARD_TYPE("投票是否发放金币"),
        VOTE_MIN_GOLD("投票最少获得金币"),
        VOTE_MAX_GOLD("投票最多获得金币"),

        //分享页/分享设置
        LOGO_QR_TYPE("是否显示二维码/LOGO"),
        LOGO_CODE("二维码/LOGO"),
        LOGO_CODE_DEF("二维码/LOGO默认图"),
        WECHAT_SHARED_IMG("微信分享图标"),
        WECHAT_SHARED_IMG_DEF("微信分享图标默认图"),
        WECHAT_TITLE("微信分享标题"),
        WECHAT_CONTENT("微信分享内容"),
        SHARED_TYPE("分享类型(路径)"),
        NEED_USER_REGISTER("非注册用户是否需要注册才可投票"),
        VOTE_IMG("投票按钮图片"),
        VOTE_IMG_DEF("投票按钮默认图"),
        JOIN_IMG("参与按钮图片"),
        JOIN_IMG_DEF("参与按钮默认图"),
        ;
        public String desc;

        ActivityVote(String desc) {
            this.desc = desc;
        }
    }

    public enum ActivityCode{

        HD001("双旦赢大礼"),
        ;

        public String desc;

        ActivityCode(String desc) {
            this.desc = desc;
        }
    }

    public enum ActivityStatus{

        CLEAR("清除活动记录"),
        END("结束活动"),
        PUBLISH("发布活动"),
        ;

        public String desc;

        ActivityStatus(String desc) {
            this.desc = desc;
        }
    }

    public enum ActivityType{

        VOTE("投票"),
        ;

        public String desc;

        ActivityType(String desc) {
            this.desc = desc;
        }
    }
    
    public enum PrizeType{
        SPECIAL_PRIZE("特等奖","iPhoneX"),
        FIRST_PRIZE("一等奖","现金388元"),
        SECOND_PRIZE("二等奖","200元话费券"),
        THIRD_PRIZE("三等奖","购物券88元"),
        FOURTH_PRIZE("四等奖","保温杯"),
        LUCKY_PRIZE("幸运奖","2元话费券"),
        XL_GOLD_COIN("随机金币奖励","随机金币奖励"),
        ;
        public String msg;

        public String tip;
        
        PrizeType(String code, String msg) {

            this.msg = code;
            this.msg = msg;
        }
    }

    public enum VoteType{

        A("默认","一次性投票"),
        B("自定义","每人每日可为几名选手投票"),
        ;
        public String desc;
        public String tip;

        VoteType(String desc, String tip) {
            this.desc = desc;
            this.tip = tip;
        }
    }

    public enum VoteGoldType{

        A("默认","不发放随机金币奖励"),
        B("自定义","发放随机金币奖励"),
        ;
        public String desc;
        public String tip;

        VoteGoldType(String desc, String tip) {
            this.desc = desc;
            this.tip = tip;
        }
    }


    public enum PushRegister{

        on("默认","不发放随机金币奖励"),
        off("自定义","发放随机金币奖励"),
        ;
        public String desc;
        public String tip;

        PushRegister(String desc, String tip) {
            this.desc = desc;
            this.tip = tip;
        }
    }
}
