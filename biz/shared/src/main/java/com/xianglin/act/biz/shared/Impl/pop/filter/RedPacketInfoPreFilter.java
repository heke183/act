package com.xianglin.act.biz.shared.Impl.pop.filter;

import com.xianglin.act.common.dal.model.redpacket.RedPacketInfo;

import static com.xianglin.act.biz.shared.Impl.PopTipAssembleServiceImpl.RETRUN_TYPE_HOLDER;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 23:48.
 */
//@PopTipPreFilter  无需过滤
public class RedPacketInfoPreFilter extends IPopTipPreFilter<RedPacketInfo> {


    @Override
    public boolean test(RedPacketInfo redPacketInfo) {

        Integer returnType = RETRUN_TYPE_HOLDER.get();
        String memcCode = redPacketInfo.getMemcCode();
        String isComplete = redPacketInfo.getIsComplete();
        if (returnType == 2) {
            return ("N".equals(isComplete));
        } else if (returnType == 3) {
            return (null == memcCode && "Y".equals(isComplete));
        } else {
            return false;
        }
    }
}
