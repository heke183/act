package com.xianglin.act.biz.shared.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.xianglin.act.biz.shared.Impl.pop.converter.IPopTipConverter;
import com.xianglin.act.biz.shared.PopTipAssembleService;
import com.xianglin.act.biz.shared.annotation.PopTipPostFilter;
import com.xianglin.act.biz.shared.annotation.PopTipPreFilter;
import com.xianglin.act.common.dal.support.pop.PopTipDO;
import com.xianglin.act.common.dal.support.pop.PopTipRequest;
import com.xianglin.act.common.dal.support.pop.SelectorRegesiter;
import com.xianglin.act.common.service.facade.constant.PopTipTypeEnum;
import com.xianglin.act.common.service.facade.model.ActivityDTO;
import com.xianglin.act.common.util.SessionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 9:48.
 */
@Component
public class PopTipAssembleServiceImpl implements InitializingBean, com.xianglin.act.biz.shared.PopTipAssembleService {

    private static final Logger logger = LoggerFactory.getLogger(PopTipAssembleService.class);

    public static final ThreadLocal<Integer> RETRUN_TYPE_HOLDER = new ThreadLocal<>();

    @Autowired
    private SelectorRegesiter selectorRegesiter;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SessionHelper sessionHelper;

    private static volatile boolean EXPORT_FLAG = false;

    private static final Map<Class<?>, Predicate<Object>> PRE_FILTER_MAP = Maps.newHashMap();

    private static final Map<Class<?>, IPopTipConverter<Object>> CONVERTER_MAP = Maps.newHashMap();

    private Predicate<ActivityDTO> allFilter = activityDTO -> true;

    /**
     * 获取弹框列表
     *
     * @param request
     * @return
     */
    @Override
    public Optional<List<ActivityDTO>> assemblePopTips(PopTipRequest request) {

        if (!EXPORT_FLAG) {
            throw new IllegalStateException("系统正在启动初始化，请稍后重试");
        }
        //所有等待弹窗的集合
        List<ActivityDTO> popWindows = Lists.newArrayList();
        //收集所有弹窗
        selectorRegesiter
                .getPipTipSelectors()
                .forEach(
                        input -> {
                            PopTipDO dbValues = input.invoke(request);
                            List<ActivityDTO> retValue = resolveDbValues(dbValues);
                            popWindows.addAll(retValue);
                        }
                );

        List<ActivityDTO> tempCollect = popWindows.stream().filter(allFilter).collect(Collectors.toList());
        return sortFilteredCollect(tempCollect);
    }

    private Optional<List<ActivityDTO>> sortFilteredCollect(List<ActivityDTO> tempCollect) {

        if (tempCollect == null) {
            return Optional.empty();
        }

        tempCollect.sort((o1, o2) -> {

            if (o1.getId() == null && o2.getId() == null) {
                return 0;
            }
            if (o1.getId() == null) {
                return -1;
            }
            if (o2.getId() == null) {
                return 1;
            }
            return -Integer.compare(o1.getOrderNum(), o2.getOrderNum());
        });
        return Optional.ofNullable(tempCollect);
    }

    //进行类型转换，所有DO类型转换成DTO类型，进行统一处理
    private List<ActivityDTO> resolveDbValues(PopTipDO dbValues) {

        Object dbRecords = dbValues.getDbRecords();
        Integer popTipType = dbValues.getPopTipType();
        Integer returnType = dbValues.getReturnType();

        List<ActivityDTO> retList = Lists.newArrayList();
        if (dbRecords == null) {
            return retList;
        }
        if (dbRecords instanceof List) {
            for (Object input : ((List) dbRecords)) {
                if (input == null) {
                    continue;
                }
                mappingDTO(popTipType, returnType, retList, input);
            }
        } else {
            mappingDTO(popTipType, returnType, retList, dbRecords);
        }
        return retList;
    }

    private void mappingDTO(Integer popTipType, Integer returnType, List<ActivityDTO> retList, Object input) {

        RETRUN_TYPE_HOLDER.set(returnType);
        if (preFilter(input)) {
            Long partyId = sessionHelper.getCurrentPartyId();
            ActivityDTO tempDTO = CONVERTER_MAP.get(input.getClass()).converter(input);
            if (tempDTO.getShowType() == null) {
                tempDTO.setPopTipType(PopTipTypeEnum.vauleOfCode(popTipType));
                tempDTO.setShowType(popTipType);
            }
            tempDTO.setPartyId(partyId);
            retList.add(tempDTO);
        }
        RETRUN_TYPE_HOLDER.remove();
    }

    private boolean preFilter(Object input) {

        Class<?> type = input.getClass();
        Predicate<Object> objectPredicate = PRE_FILTER_MAP.get(type);
        if (objectPredicate == null) {
            return true;
        }
        return objectPredicate.test(input);
    }


    /**
     * 初始化过滤器链
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        logger.info("=========== [[ { 初始化过滤器 } ]]===========");
        //初始化前过滤器
        Map<String, Object> preFilters = applicationContext.getBeansWithAnnotation(PopTipPreFilter.class);
        for (Object filter : preFilters.values()) {
            Class rawType = ((TypeToken) filter).getRawType();
            String filterClassName = filter.getClass().getName();
            if (filter instanceof Predicate) {
                PRE_FILTER_MAP.putIfAbsent(rawType, ((Predicate<Object>) filter));
                logger.info("===========过滤类型：[[ {} ]]，前置过滤器类名：[[ {} ]]===========", rawType.getName(), filterClassName);
            } else {
                logger.info("=========== 前置过滤器类名：[[ {} ]] 不是Predict的子类,初始化忽略===========", filterClassName);
            }
        }
        logger.info("=========== [[ { 前置过滤器初始化完成 } ]]===========");

        //初始化后过滤器
        Map<String, Object> postFilters = applicationContext.getBeansWithAnnotation(PopTipPostFilter.class);
        if (postFilters == null) {
            postFilters = Maps.newHashMap();
        }
        for (Object filter : postFilters.values()) {
            String filterClassName = filter.getClass().getName();
            if (filter instanceof Predicate) {
                allFilter = allFilter.and((Predicate<? super ActivityDTO>) filter);
                logger.info("===========添加后置过滤器，类名：[[ {} ]]===========", filterClassName);
            } else {
                logger.info("=========== 后置过滤器，类名：[[ {} ]] 不是Predict的子类===========", filterClassName);
            }
        }
        logger.info("=========== [[ { 后置过滤器初始化完成 } ]]===========");

        //初始化转换器
        Map<String, IPopTipConverter> converters = applicationContext.getBeansOfType(IPopTipConverter.class);
        for (IPopTipConverter converter : converters.values()) {
            String converterClassName = converter.getClass().getName();
            Class rawType = converter.getRawType();
            CONVERTER_MAP.putIfAbsent(rawType, ((IPopTipConverter<Object>) converter));
            logger.info("===========添加转换器器，类名：[[ {} ]]===========", converterClassName);
        }
        EXPORT_FLAG = true;
    }
}
