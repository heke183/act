package com.xianglin.act.biz.shared.Impl.pop.converter;

import com.google.common.reflect.TypeToken;
import com.xianglin.act.common.service.facade.model.ActivityDTO;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/13 17:18.
 */
public abstract class IPopTipConverter<T> extends TypeToken<T> {

    public abstract ActivityDTO converter(T input);
}
