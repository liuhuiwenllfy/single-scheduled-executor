package com.llfy.cesea.core.swagger;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


/**
 * swagger配置文件
 * <a href="http://localhost:8888/swagger-ui/index.html">swagger地址</a>
 * <a href="http://localhost:8888/doc.html">knife4j地址</a>
 *
 * @author LLFY
 */
@EnableOpenApi
@EnableKnife4j
@Configuration
public class SwaggerConfig {

    @Value("${apiInfo.title}")
    private String title;

    @Value("${apiInfo.description}")
    private String description;

    @Value("${apiInfo.basePackage}")
    private String basePackage;

    @Value("${apiInfo.contact.name}")
    private String contactName;

    @Value("${apiInfo.contact.url}")
    private String contactUrl;

    @Value("${apiInfo.contact.email}")
    private String contactEmail;

    @Value("${apiInfo.version}")
    private String version;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("所有模块")
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .contact(new Contact(contactName, contactUrl, contactEmail))
                .version(version)
                .build();
    }
}
