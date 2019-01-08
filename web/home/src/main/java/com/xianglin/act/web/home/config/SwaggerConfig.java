package com.xianglin.act.web.home.config;

import org.springframework.context.annotation.*;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The type Swagger config.
 *
 * @author yefei
 * @date 2017 -9-28 20:39:10
 */
@Configuration
@EnableSwagger2
@Profile({"dev", "local"})
public class SwaggerConfig {

    /**
     * Api docket.
     *
     * @return the docket
     */
    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title("乡邻运营活动Api接口")
                .description("乡邻运营活动Api接口")
                .version("1.0.0")
                .build();
    }

}