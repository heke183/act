package com.xianglin.act.common.util.dbconfig;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static org.apache.commons.lang3.time.DateFormatUtils.ISO_DATETIME_FORMAT;
import static org.apache.commons.lang3.time.DateFormatUtils.ISO_DATE_FORMAT;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/19 16:14.
 */
public class DbConfigBeanInvokeHandler implements MethodInterceptor, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DbConfigBeanInvokeHandler.class);

    private static final Map<Class<?>, Function<String, Object>> CAST_MAP = Maps.newConcurrentMap();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Object target;

    public DbConfigBeanInvokeHandler(Object target) {

        this.target = target;
    }

    public DbConfigBeanInvokeHandler() {

    }

    /**
     * 查询sql
     */
    private static final String SQL = " SELECT CONFIG_VALUE " +

            " FROM act_system_config " +

            " WHERE IS_DELETED = '0' " +

            " AND CONFIG_CODE = ? ";

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        String methodName = method.getName();
        if (!methodName.startsWith("get")) {
            return null;
        }
        if (method.getDeclaringClass() == Object.class) {
            //getClass方法返回
            return Object.class;
        }

        String upperUnderScoreKeyName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, methodName.substring(3));
        String value = jdbcTemplate.queryForObject(SQL, new String[]{upperUnderScoreKeyName}, String.class);
        logger.debug("===========查询配置值成功  key：[[ {} ]]  value：[[ {} ]]===========", upperUnderScoreKeyName, value);
        return castValue(value, method);
    }

    /**
     * 粗糙的类型转换
     *
     * @param value
     * @param method
     * @return
     */
    private Object castValue(String value, Method method) {

        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        value = value.trim();
        Class<?> returnType = method.getReturnType();
        if (returnType == String.class) {
            return value;
        }
        return CAST_MAP.get(returnType).apply(value);
    }

    /**
     * 初始化cast映射
     * 函数式编程就是好用
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        CAST_MAP.put(Integer.class, Integer::valueOf);
        CAST_MAP.put(int.class, Integer::valueOf);
        CAST_MAP.put(Long.class, Long::valueOf);
        CAST_MAP.put(long.class, Long::valueOf);
        CAST_MAP.put(Double.class, Double::valueOf);
        CAST_MAP.put(double.class, Double::valueOf);
        CAST_MAP.put(Float.class, Float::valueOf);
        CAST_MAP.put(float.class, Float::valueOf);
        CAST_MAP.put(BigDecimal.class, BigDecimal::new);
        CAST_MAP.put(Void.class, input -> null);
        CAST_MAP.put(Date.class, input -> {
            try {
                return DateUtils.parseDate(input, ISO_DATETIME_FORMAT.getPattern(), ISO_DATE_FORMAT.getPattern());
            } catch (ParseException e) {
                throw new RuntimeException(e.getCause());
            }
        });
        CAST_MAP.put(LocalDateTime.class, input -> {
            // 可以参考mybatis的结果转换，但是先不管了，后面再看吧
            throw new UnsupportedOperationException("暂不支持LocalDateTime类型的转换");
        });
    }
}
