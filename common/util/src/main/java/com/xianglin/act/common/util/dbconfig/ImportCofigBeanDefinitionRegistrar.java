package com.xianglin.act.common.util.dbconfig;

import com.google.common.base.Strings;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.springframework.beans.factory.support.AbstractBeanDefinition.AUTOWIRE_BY_TYPE;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/19 16:32.
 */
public class ImportCofigBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    private static final Logger logger = LoggerFactory.getLogger(ImportCofigBeanDefinitionRegistrar.class);

    private static final String DELIMITERS = ",|;|\t";

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        Map<String, Object> enableAttributesMap = importingClassMetadata.getAnnotationAttributes(EnableDbConfigBean.class.getName());
        AnnotationAttributes enableAttributes = AnnotationAttributes.fromMap(enableAttributesMap);
        String scanPackage = enableAttributes.getString("scanPackage");
        if (Strings.isNullOrEmpty(scanPackage)) {
            throw new IllegalArgumentException("config bean 包扫描路径不能为空");
        }
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.setResourceLoader(resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(DbConfigBean.class));
        String[] packages = StringUtils.tokenizeToStringArray(scanPackage, DELIMITERS);
        Arrays.stream(packages)
                .map(scanner::findCandidateComponents)
                .flatMap(Set::stream)
                .forEach(input -> {
                    GenericBeanDefinition df = (GenericBeanDefinition) input;
                    String beanClassName = df.getBeanClassName();

                    BeanDefinitionBuilder factoryBean = BeanDefinitionBuilder.genericBeanDefinition(DbConfigFactoryBean.class);
                    factoryBean.setAutowireMode(AUTOWIRE_BY_TYPE);
                    factoryBean.addPropertyValue("beanClassName", beanClassName);
                    String randomNumeric = RandomStringUtils.randomNumeric(5);
                    registry.registerBeanDefinition(beanClassName.substring(beanClassName.lastIndexOf(".") + 1) + randomNumeric, factoryBean.getBeanDefinition());
                    logger.info("===========注册config bean的代理成功 className:[[ {} ]]===========", beanClassName);
                });
        logger.info("===========扫描config bean 成功  sacnPackage:[[ {} ]]===========", scanPackage);
    }
}
