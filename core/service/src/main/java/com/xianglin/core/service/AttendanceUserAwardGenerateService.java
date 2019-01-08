package com.xianglin.core.service;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/23 15:29.
 */

/**
 * 计算用户奖励服务接口
 */
public interface AttendanceUserAwardGenerateService {

    /**
     * 计算用户奖励
     */
    void calculateUserAward();

    /**
     * 获取活动进行天数
     * @return
     */
    long getActivityDays();
}
