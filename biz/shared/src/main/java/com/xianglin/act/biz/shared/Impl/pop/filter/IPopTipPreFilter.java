package com.xianglin.act.biz.shared.Impl.pop.filter;

import com.google.common.reflect.TypeToken;

import java.util.function.Predicate;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 16:57.
 */
public abstract class IPopTipPreFilter<T> extends TypeToken<T> implements Predicate<T> {

}
