package com.xianglin.act.common.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象COPY
 *
 * @class com.xianglin.xlnodecore.common.util.DTOUtils
 * @date 2015年9月17日 下午2:18:46
 */
public class DTOUtils {

    public static final Logger log = LoggerFactory.getLogger(DTOUtils.class);

    public static <S, T> T map(S source, T dest) throws Exception {
        if (source == null) {
            return null;
        }
        if (source instanceof Map) {
            BeanUtils.copyProperties(dest, source);
        } else {
            org.springframework.beans.BeanUtils.copyProperties(source, dest);
        }
        return dest;
    }

    public static <S, T> T map(S source, Class<T> targetClass) throws Exception {
        if (source == null) {
            return null;
        }
        T cl = targetClass.newInstance();
        if (source instanceof Map) {
            BeanUtils.copyProperties(cl, source);
        } else {
            org.springframework.beans.BeanUtils.copyProperties(source, cl);
        }
        return cl;
    }

    public static <S, T> List<T> map(List<S> source, Class<T> targetClass) throws Exception {
        List<T> list = null;
        if (CollectionUtils.isNotEmpty(source)) {
            list = new ArrayList<>(source.size());
            for (Object obj : source) {
                T target = map(obj, targetClass);
                list.add(target);
            }
        } else {
            list = new ArrayList<>(0);
        }
        return list;
    }

    public static Map<String, Object> beanToMap(Object source) throws Exception {
        return PropertyUtils.describe(source);
    }

    public static Map<String, Object> queryMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("startPage", 1);
        map.put("pageSize", 10);
        return map;
    }
}
