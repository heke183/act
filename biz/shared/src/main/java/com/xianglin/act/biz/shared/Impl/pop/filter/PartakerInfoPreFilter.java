package com.xianglin.act.biz.shared.Impl.pop.filter;

import com.xianglin.act.biz.shared.annotation.PopTipPreFilter;
import com.xianglin.act.common.dal.model.redpacket.PartakerInfo;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 23:48.
 */
@PopTipPreFilter
public class PartakerInfoPreFilter extends IPopTipPreFilter<PartakerInfo> {

    @Override
    public boolean test(PartakerInfo partakerInfo) {

        return !partakerInfo.isRemind();
    }
}
