package com.xianglin.act.biz.shared.Impl.pop.filter;

import com.xianglin.act.biz.shared.annotation.PopTipPostFilter;
import com.xianglin.act.common.service.facade.model.ActivityDTO;

import java.util.function.Predicate;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 16:57.
 */
public interface IPopTipPostFilter extends Predicate<ActivityDTO> {

}
