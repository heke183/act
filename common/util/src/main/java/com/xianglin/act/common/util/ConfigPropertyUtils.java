package com.xianglin.act.common.util;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * 工具类
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/18 12:59.
 * @see common\\util\\src\\main\\resources\\config.properties
 */
public class ConfigPropertyUtils {

    private static final Properties CONFIG_PROPERTIES;

    public static final String H5_SERVER_URL = "h5.server.url";

    public static final String EC_SERVER_URL = "ec.server.url";

    public static final String CURRENT_ENV = "CURRENT_ENV";

    static {
        try {
            CONFIG_PROPERTIES = PropertiesLoaderUtils.loadAllProperties("config.properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getValue(String key) {

        return CONFIG_PROPERTIES.getProperty(key);
    }

    public static String get(String key) {

        return CONFIG_PROPERTIES.getProperty(key);
    }

    public static String getValueWithDefault(String key, String defaultVal) {

        return CONFIG_PROPERTIES.getProperty(key, defaultVal);
    }

    /**
     * 获取h5服务器地址
     *
     * @return
     */
    public static String getH5ServerHost() {

        return ConfigPropertyUtils.get(H5_SERVER_URL);
    }

    /**
     * 获取h5服务器地址（添加分隔符 /）
     *
     * @return
     */
    public static String getH5ServerHostWithDelimiter() {

        return ConfigPropertyUtils.get(H5_SERVER_URL) + "/";
    }

    /**
     * 获取电商服务器地址
     *
     * @return
     */
    public static String getEcServerHost() {

        return ConfigPropertyUtils.get(EC_SERVER_URL);
    }

    /**
     * 获取电商服务器地址（添加分隔符 /）
     *
     * @return
     */
    public static String getEcServerHostWithDelimiter() {

        return ConfigPropertyUtils.get(EC_SERVER_URL) + "/";
    }

    /**
     * 获取当前环境
     *
     * @return
     */
    public static String getCurrentEnv() {

        return ConfigPropertyUtils.get(CURRENT_ENV);
    }
}
