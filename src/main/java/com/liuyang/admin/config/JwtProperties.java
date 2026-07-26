package com.liuyang.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {  // 作用：把 yml 里的配置「映射」成 Java 对象

    private String secret;

    private Long expiration;
}
