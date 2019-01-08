package com.xianglin.act.common.dal.support.pop;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.util.StringUtil;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 11:24.
 */

public class SelectorMapperScannerConfigurer extends MapperScannerConfigurer {

    /**
     * 注册完成后，对MapperFactoryBean的类进行特殊处理
     *
     * @param registry
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {

        MapperHelper mapperHelper = getMapperHelper();
        super.postProcessBeanDefinitionRegistry(registry);
        //如果没有注册过接口，就注册默认的Mapper接口
//        mapperHelper.ifEmptyRegisterDefaultInterface();
        String[] names = registry.getBeanDefinitionNames();
        GenericBeanDefinition definition;
        for (String name : names) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(name);
            if (beanDefinition instanceof GenericBeanDefinition) {
                definition = (GenericBeanDefinition) beanDefinition;
                if (StringUtil.isNotEmpty(definition.getBeanClassName())
                        && definition.getBeanClassName().equals("tk.mybatis.spring.mapper.MapperFactoryBean")) {
                    //替换掉FactoryBean
                    definition.setBeanClass(SelectorMapperFactoryBean.class);
                    definition.getPropertyValues().add("mapperHelper", mapperHelper);
                }
            }
        }
    }
}
