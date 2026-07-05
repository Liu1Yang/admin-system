package com.liuyang.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {  // 文档整体信息，显示在顶部
        return new OpenAPI()
                .info(new Info()
                        .title("admin-system API")
                        .description("LiuYang 后台管理系统接口文档")
                        .version("1.0.0")
                        .contact(new Contact().name("LiuYang")));
    }
}
