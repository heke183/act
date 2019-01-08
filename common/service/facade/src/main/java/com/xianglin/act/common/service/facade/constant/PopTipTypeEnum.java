package com.xianglin.act.common.service.facade.constant;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/10 11:05.
 */
public enum PopTipTypeEnum implements EnumReadable {
    /**
     * 活动导流提示弹窗
     */
    POP_TIP_OF_NO_BUTTON("活动弹框", 0),
    /**
     * 活动结果提示弹窗——一个按钮
     */
    POP_TIP_OF_ONE_BUTTON("2连图", 1),

    /**
     * 活动结果提示弹窗——两个按钮
     */
    POP_TIP_OF_TWO_BUTTON("2按钮", 2),

    /**
     * 活动导流提示弹窗——tab
     */
    POP_TIP_OF_TAB("打卡", 3) {
        public boolean shouldLog() {
            //不自动记录日志，回调接口后记录日志
            return false;
        }
    },

    /**
     * 活动结果提示弹窗——两秒跳转
     */
    POP_TIP_OF_UNFINISH_ACTIVITY("两秒跳转", 4),

   /**
     * 右悬浮框
     */
    FLOAT_WINDOW_OF_RIGHT("右悬浮框", 5){
       public boolean shouldLog() {
           //不自动记录日志，回调接口后记录日志
           return false;
       } 
   };

    private String desc;

    private int code;

    PopTipTypeEnum() {

    }

    PopTipTypeEnum(String desc, int code) {

        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {

        return desc;
    }

    public void setDesc(String desc) {

        this.desc = desc;
    }

    @Override
    public String getName() {

        return desc;
    }

    public int getCode() {

        return code;
    }


    public void setCode(int code) {

        this.code = code;
    }

    public static PopTipTypeEnum vauleOfCode(int code) {

        for (PopTipTypeEnum popTipTypeEnum : PopTipTypeEnum.values()) {
            if (popTipTypeEnum.getCode() == code) {
                return popTipTypeEnum;
            }
        }
        return null;
    }

    /**
     * 返回弹窗结果时，是否主动记录弹窗日志
     * 如果返回为false，则需要客户端在用户关闭弹窗时回调关闭接口记录日志
     *
     * @return
     * @see com.xianglin.act.biz.service.implement.ActServiceImpl#open(java.lang.Integer, java.lang.Long)
     */
    public boolean shouldLog() {

        return true;
    }
}
