package com.xianglin.core.service;

import com.xianglin.act.common.dal.model.PopWindow;

import java.util.List;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/20 10:33.
 */

public interface GenericPopWindowGenerator {

    List<PopWindow> generateDynamicPopWindow(Long partyId);
}
