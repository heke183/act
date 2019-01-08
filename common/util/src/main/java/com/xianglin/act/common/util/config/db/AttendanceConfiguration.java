package com.xianglin.act.common.util.config.db;

import com.xianglin.act.common.util.dbconfig.DbConfigBean;
import lombok.Data;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/19 17:08.
 */
@Data
@DbConfigBean
public class AttendanceConfiguration {

    private String initSignInPeopleNum;

    private String signInStartTime;

    private String signInEndTime;

    private String awardResultTime;

    private String defaultUserHeadimg;
}
