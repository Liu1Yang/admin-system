package com.liuyang.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    /** 是否启用限流 */
    private boolean enabled = true;

    /** 通用 API：窗口内最大请求数 */
    private int apiMaxRequests = 100;

    /** 通用 API：窗口时长（秒） */
    private int apiWindowSeconds = 60;

    /** 登录接口：窗口内最大请求数（防暴力破解） */
    private int loginMaxRequests = 10;

    /** 登录接口：窗口时长（秒） */
    private int loginWindowSeconds = 60;
}
