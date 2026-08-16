package com.liuyang.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;

    /** Access Token 有效期（毫秒），默认 2 小时 */
    private Long expiration = 7200000L;

    /** Refresh Token 有效期（毫秒），默认 7 天 */
    private Long refreshExpiration = 604800000L;
}
