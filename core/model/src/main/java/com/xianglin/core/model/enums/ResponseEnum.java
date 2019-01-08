package com.xianglin.core.model.enums;

/**
 * @author yefei
 * @date 2018-01-27 11:18
 */
public enum ResponseEnum {
    SUCCESS("2000", "成功", "调用成功"),
    FAIL("3000", "业务异常", "调用不合法！"),
    ERROR("4000", "未知错误，系统异常", "抱歉无法操作，请稍后再试！"),
    REPEAT("5000", "请勿重复提交", "请勿重复提交"),
    NO_LOGIN("6000", "未登录", "未登录"),

    // --------大转盘活动-----------
    GOLD_NOT_ENOUGH("3001", "金币不足", "您当前金币不足"),
    ACTIVITY_END("3002", "活动已结束,去APP查看更多活动！", "活动已结束,去APP查看更多活动！"),
    MOBILE_PHONE_IS_NULL("3003", "请输入手机号验证码后抽奖", "请输入手机号验证码后抽奖"),
    PARTY_IS_NULL("3004", "PARTY_IS_NULL", "抱歉无法操作，请稍后再试！"),
    CUSTOMER_INFO_MISS("3005", "用户信息缺失", "用户信息缺失"),
    ALREADY_REGISTER("3006", "用户已注册", "您已注册乡邻，赶紧去APP抽彩电吧！"),
    CHECK_MESSAGE_FAIL("3007", "验证码错误", "验证码错误"),
    ALREADY_PLAY("3008", "您已经抽过一次奖", "您已经抽过一次奖"),
    PLAY_COUNT_LIMIT("3009", "您的抽奖次数已经使用完", "您的抽奖次数已经使用完，明天再来"),
    // --------大转盘活动-----------


    // --------现金红包活动-----------
    RP_OLD_USER("3101", "您是乡邻APP用户，直接去APP发红包吧！", "您是乡邻APP用户，直接去APP发红包吧！"),
    RP_ALREADY_OPEN("3102", "已领好友3次现金红包，达到上限，去APP试试发红包得微信现金", "已领好友3次现金红包，达到上限，去APP试试发红包得微信现金"),
    RP_ALREADY_OPEN_COUNT("3103", "今天已经领过好友红包了，去APP试试", "今天已经领过好友红包了，去APP试试"),

    RP_UNFOLLOW("3104", "你好友未超过10人，去关注好友再来", "你好友未超过10人，去关注好友再来"),
    RP_NO_PUBLISH_ARTICLE("3105", "你从未发过微博，去发布微博再来", "你从未发过微博，去发布微博再来"),
    RP_NO_SIGN("3106", "你今天还没签到晒收入", "你今天还没签到晒收入"),

    RP_CREATE_CHECKED0("3107", "好友未超过10人", "好友未超过10人/从未发过微博"),
    RP_CREATE_CHECKED1("3108", "从未发过微博", "从未发过微博/今天还没签到晒收入"),
    RP_CREATE_CHECKED2("3108", "从未发过微博", "好友未超过10人/今天还没签到晒收入"),
    RP_CREATE_CHECKED3("3110", "今天还没签到晒收入", "好友未超过10人/从未发过微博/今天还没签到晒收入"),

    RP_EXPIRE_24("3111", "超过24小时", "签到，晒收入同时可获得金币"),

    RP_DEVICE_CONDITION("3112", "一个设备一天只能领取一次微信红包或金币", "一个设备一天只能领取一次微信红包或金币"),

    RP_WX_REDIRECT("3113", "未拿到openId,重新跳转", "未拿到openId,重新跳转"),
    // --------现金红包活动-----------


    // --------打卡赢金币活动-----------
    UNEXCEPT_STATUS("3113", "用户活动参与状态异常", "用户活动参与状态异常"),
    // --------打卡赢金币活动-----------

    // --------投票活动-----------
    ACTIVITY_CODE_NOT_EXIST("4001", "活动code参数不存在", "活动code参数不存在"),
    ACT_NOT_EXIST("4002", "活动不存在", "活动不存在"),
    ACT_PARAM_ERROR("4003", "参数错误", "参数错误"),
    ACT_ID_TOO_BIG("4004", "id值超过999", "id值超过999"),
    ACT_USER_INFO_ERROR("4005", "cif获取用户信息异常", "cif获取用户信息异常"),
    ACT_CONVERT_ERROR("4006", "无法识别互动的投票类型", "无法识别互动的投票类型"),
    ACT_EXPIRE("4007", "活动已结束，去APP查看更多活动！", "活动已结束，去APP查看更多活动！"),
    ACT_HAS_TAKE_PART_IN("4008", "已参与，请勿重复参与", "已参与，请勿重复参与"),

    VOTE_USER_REGISTERED("4009", "", "你已是APP用户，去APP中可每天投1票！"),

    VOTE_NEW_USER_VOTED("4010", "", "你已投过票，去乡邻APP,你可以每天投1票哟！"),

    VOTE_LIMIT_OF_DAY("4011", "", "每日只能为同一名选手投一票"),

    VOTE_LIMIT_SELF("4012", "", "只能给自己投一次"),

    VOTE_LIMIT("4013", "", "你今天的投票已达上限，请明天再来"),
    VOTE_IMG_NE("4014", "", "图片不存在"),
    VOTE_NO_PUBLISH("4015", "", "未发布"),
    VOTE_GOLD_NOT_ENOUGH("4016", "你输入金币大于你当前金币值，请重新输入！", "你输入金币大于你当前金币值，请重新输入！"),

    VOTE_KNOCK_OUT("4017","你投的国家已出局，请重投！","你投的国家已出局，请重投！"),
    // --------投票活动-----------

    // --------步步生金活动-----------
    STEP_NOT_EXCHANGETIME("5001", "时间有误，无法兑换金币", "时间有误，无法兑换金币"),
    STEP_NOT_STEPNUMBER("5003", "没有到兑换的步数", "没有到兑换的步数"),
    STEP_REWARD_STEPNUMBER("5004", "该时间段今天已经兑换了，不能重复兑换", "该时间段今天已经兑换了，不能重复兑换"),
    ;

    public String code;

    public String message;

    public String tips;

    ResponseEnum(String code, String message, String tips) {

        this.code = code;
        this.message = message;
        this.tips = tips;
    }

}
