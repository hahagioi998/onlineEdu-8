package com.online.edu.service.base.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    //前台网站API
    @Bean
    public Docket webApiConfig(){
        //开启Swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                //设置分组  前台接口分组
                .groupName("webApi")
                //调用下面方法定义文档
                .apiInfo(webApiInfo())
                //对分组进行配置选择
                .select()
                //设置 路径   包含的路径用and
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }

    //后台管理API
    @Bean
    public Docket adminApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("adminApi")
                .apiInfo(adminApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();
    }

    //优化Swagger2首页文档的标题和介绍。进行了分组后，两者不能一样  辅助的类都用私有  辅助类，只需被本类调用的方法不用添加到容器中
    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                //定义文档标题
                .title("网站的api文档")
                //定义文档描述
                .description("本文档描述了在线教育学院网站的api接口定义")
                //文档的版本
                .version("1.0")
                //文档的联系人
                .contact(new Contact("Answer","https://showanswer.github.io/","1310151064@qq.com"))
                .build();
    }

    //优化Swagger2后台文档的标题和介绍。进行了分组后，两者不能一样  辅助的类都用私有  辅助类，只需被本类调用的方法不用添加到容器中
    private ApiInfo adminApiInfo(){
        return new ApiInfoBuilder()
                //定义文档标题
                .title("后台管理系统的api文档")
                //定义文档描述
                .description("本文档描述了在线教育学院后台管理系统的api接口定义")
                //文档的版本
                .version("1.0")
                //文档的联系人
                .contact(new Contact("Answer","https://showanswer.github.io/","1310151064@qq.com"))
                .build();
    }

}
