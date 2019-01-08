package com.xianglin.act.common.util.config.db;

import com.xianglin.act.common.util.dbconfig.DbConfigBean;
import lombok.Data;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/6 11:18.
 */
@Data
@DbConfigBean
public class BaseConfiguration {

    private String shouldSendSms;
}
