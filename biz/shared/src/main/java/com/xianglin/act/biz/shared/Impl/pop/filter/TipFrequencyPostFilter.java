package com.xianglin.act.biz.shared.Impl.pop.filter;

import com.xianglin.act.biz.shared.annotation.PopTipPostFilter;
import com.xianglin.act.common.dal.mappers.PopWindowHistoryMapper;
import com.xianglin.act.common.dal.model.PopWindowHistory;
import com.xianglin.act.common.service.facade.constant.PopTipFrequencyEnum;
import com.xianglin.act.common.service.facade.model.ActivityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDate;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 16:55.
 */
@PopTipPostFilter
public class TipFrequencyPostFilter implements IPopTipPostFilter {

    @Autowired
    private PopWindowHistoryMapper popWindowHistoryMapper;

    @Override
    public boolean test(ActivityDTO activityDTO) {

        if (activityDTO == null) {
            return false;
        }
        Integer frequency = activityDTO.getFrequency();
        if (frequency == null || activityDTO.getId() == null) {
            return true;
        }
        PopTipFrequencyEnum popTipFrequencyEnum = PopTipFrequencyEnum.vauleOfCode(frequency);
        if (popTipFrequencyEnum == null) {
            throw new IllegalStateException("频率后过滤器：弹框频率错误，只能是每天或者一次");
        }
        Long id = activityDTO.getId();
        Long partyId = activityDTO.getPartyId();

        checkArgument(id != null, "活动弹框id不能为空");
        checkArgument(partyId != null, "partyId不能为空");

        Object from = null;
        Object to = null;
        switch (popTipFrequencyEnum) {
            case ONECE:
                from = LocalDate.now();
                to = ((LocalDate) from).plusDays(1);
                break;
            case EVERY_DAY:
                from = activityDTO.getShowStartTime();
                to = activityDTO.getShowExpireTime();
                break;
        }
        Example example = new Example(PopWindowHistory.class);
        example.and()
                .andEqualTo("partyId", partyId)
                .andEqualTo("popWindowId", id)
                .andEqualTo("isDeleted", "0")
                .andGreaterThanOrEqualTo("popDate", from)
                .andLessThan("popDate", to);
        List<PopWindowHistory> historyList = popWindowHistoryMapper.selectByExample(example);
        return historyList.isEmpty();
    }
}
