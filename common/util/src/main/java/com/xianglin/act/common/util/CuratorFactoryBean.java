package com.xianglin.act.common.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/23 15:03.
 */

public class CuratorFactoryBean implements FactoryBean<CuratorFramework> {

    public static final String NAMESPACE = "act";

    private String zookeeperServer;

    @Override
    public CuratorFramework getObject() throws Exception {

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(zookeeperServer)
                .connectionTimeoutMs(5000)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace(NAMESPACE)
                .build();
        client.start();
        return client;
    }

    @Override
    public Class<?> getObjectType() {

        return CuratorFramework.class;
    }

    @Override
    public boolean isSingleton() {

        return true;
    }

    public void setZookeeperServer(String zookeeperServer) {

        this.zookeeperServer = zookeeperServer;
    }
}
