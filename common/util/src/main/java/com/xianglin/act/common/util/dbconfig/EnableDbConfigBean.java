package com.xianglin.act.common.util.dbconfig;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/19 16:29.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Import(ImportCofigBeanDefinitionRegistrar.class)
public @interface EnableDbConfigBean {

    String scanPackage();
}
