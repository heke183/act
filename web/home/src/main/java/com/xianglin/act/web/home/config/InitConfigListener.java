package com.xianglin.act.web.home.config;

import com.xianglin.act.common.util.ConfigPropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitConfigListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(InitConfigListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String env = ConfigPropertyUtils.getCurrentEnv();
        System.setProperty("spring.profiles.active", env);
        logger.info("-----> spring.profiles.active: {}", env);
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}