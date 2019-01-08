package com.xianglin.core.service.schedule;

import com.xianglin.act.common.dal.mappers.PrizeProbabilityConfigMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 对于特等奖/一等奖/二等奖 提升概率
 * 抽中奖后 回归原始概率
 *
 * @author yefei
 * @date 2018-01-23 11:19
 */
public class ProbabilityAddSchedule {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(ProbabilityAddSchedule.class);

    private final static int delay = 60 * 5;

    private ScheduledExecutorService scheduledExecutorService;

    @Resource
    private PrizeProbabilityConfigMapper prizeProbabilityConfigMapper;


    public ProbabilityAddSchedule() {

        scheduledExecutorService = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(Boolean.TRUE);
                thread.setName("ProbabilityAddSchedule");
                return thread;
            }
        });
        //init();
    }

    public void init() {
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                logger.debug("-------> 增加概率。");
                prizeProbabilityConfigMapper.addProbability();
            }
        }, 10, delay, TimeUnit.SECONDS);
    }
}
