package com.xianglin.act.biz.shared.Impl.pop.filter;

import com.google.common.base.Preconditions;
import com.xianglin.act.biz.shared.annotation.PopTipPreFilter;
import com.xianglin.act.common.dal.model.PopWindow;

import java.util.Date;

import static com.xianglin.act.biz.shared.Impl.PopTipAssembleServiceImpl.RETRUN_TYPE_HOLDER;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 23:48.
 */
@PopTipPreFilter
public class PopWindowPreFilter extends IPopTipPreFilter<PopWindow> {


    @Override
    public boolean test(PopWindow popWindow) {

        Date now = new Date();
        Date showStartTime = popWindow.getShowStartTime();
        Date showExpireTime = popWindow.getShowExpireTime();

        Preconditions.checkArgument(showStartTime != null, "活动弹窗开始时间不能为空");
        Preconditions.checkArgument(showExpireTime != null, "活动弹窗结束时间不能为空");

        return showExpireTime.after(now) && showStartTime.before(now);
    }
}
