package com.xianglin.act.biz.shared.Impl;

import com.xianglin.act.biz.shared.PopTipLogService;
import com.xianglin.act.common.dal.mappers.PopWindowHistoryMapper;
import com.xianglin.act.common.dal.model.PopWindowHistory;
import com.xianglin.act.common.service.facade.model.ActivityDTO;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/13 2:54.
 */
@Service
public class PopTipLogServiceImpl implements PopTipLogService {

    private static final Logger logger = LoggerFactory.getLogger(PopTipLogServiceImpl.class);

    private static final String UPDATER = "system";

    @Autowired
    private PopWindowHistoryMapper popWindowHistoryMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;


    @Override
    public void batchLog(List<ActivityDTO> tipList) {

        if (tipList == null) {
            return;
        }
        Date now = new Date();
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
            PopWindowHistoryMapper logMapper = sqlSession.getMapper(PopWindowHistoryMapper.class);
            tipList.stream()
                    .filter(input -> input.getPopTipType().shouldLog())  //打卡类型不自动记录日志
                    .map(input -> getPopWindowHistory(input, now))
                    .filter(input -> input.getPopWindowId() != null)
                    .forEach(logMapper::insert);
            sqlSession.commit();
        }
        logger.info("===========弹框日志记录日志完成===========");
    }

    @Override
    public void log(ActivityDTO activityDTO) {

        Date now = new Date();
        PopWindowHistory popWindowHistory = getPopWindowHistory(activityDTO, now);
        popWindowHistoryMapper.insert(popWindowHistory);
    }

    private PopWindowHistory getPopWindowHistory(ActivityDTO input, Date now) {

        PopWindowHistory popWindowHistory = new PopWindowHistory();
        popWindowHistory.setPopWindowId(input.getId());
        popWindowHistory.setPartyId(input.getPartyId());
        popWindowHistory.setTemplateCode(input.getShowType() + "");
        popWindowHistory.setCreateDate(now);
        popWindowHistory.setUpdateDate(now);
        popWindowHistory.setIsDeleted("0");
        popWindowHistory.setPopDate(now);
        popWindowHistory.setCreator(UPDATER);
        popWindowHistory.setUpdater(UPDATER);
        return popWindowHistory;
    }
}
