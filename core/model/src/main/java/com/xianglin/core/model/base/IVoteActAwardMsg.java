package com.xianglin.core.model.base;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/4 18:05.
 */
public interface IVoteActAwardMsg {

    /**
     * 手机短信模板
     *
     * @return
     */
    String getSmsMessageTemp();

    /**
     * app通知消息
     *
     * @return
     */
    String getAppMessageTemp();
}
