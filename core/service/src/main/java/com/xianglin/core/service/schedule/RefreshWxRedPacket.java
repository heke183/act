package com.xianglin.core.service.schedule;

import com.xianglin.act.common.dal.mappers.CustomerPrizeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 * @date 2018-04-12 9:22
 */
public class RefreshWxRedPacket {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(RefreshWxRedPacket.class);

    private final static int delay = 24 * 60 * 60 * 1000;

    private ScheduledExecutorService scheduledExecutorService;

    @Resource
    private CustomerPrizeMapper customerPrizeMapper;


    public RefreshWxRedPacket() {

        scheduledExecutorService = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(Boolean.TRUE);
                thread.setName("RefreshWxRedPacket");
                return thread;
            }
        });
        init();
    }

    public void init() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        // 每天 00:00 更新红包个数
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                logger.info("-------> 刷新微信红包个数。");
                customerPrizeMapper.updateWxRedPacket();
            }
        }, todayEnd.getTimeInMillis() - System.currentTimeMillis(), delay, TimeUnit.MILLISECONDS);
    }
}
