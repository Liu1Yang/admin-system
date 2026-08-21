package com.liuyang.admin.service.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

/**
 * 滑动窗口限流：Redis ZSET 记录每次请求时间戳，Lua 脚本保证删旧值 / 计数 / 写入原子执行。
 */
@Service
public class RateLimitService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);
    private static final String KEY_PREFIX = "rate:limit:";

    private final StringRedisTemplate redisTemplate; // Spring 提供的 Redis 操作工具，用来连接 Redis、执行命令。
    private final DefaultRedisScript<Long> slidingWindowScript; //  Lua 脚本的“容器”，用来加载和存放你的限流脚本。

    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.slidingWindowScript = new DefaultRedisScript<>();  // 创建一个空的脚本容器
        this.slidingWindowScript.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("lua/sliding_window_rate_limit.lua"))); // 说明lua脚本的地址
        this.slidingWindowScript.setResultType(Long.class); // 告诉它这个脚本返回的是 Long 类型（1L 或 0L）
    }

    /**
     * @return true 允许通过；false 超过限额
     */
    public boolean tryAcquire(String scope, int maxRequests, int windowSeconds) {
        if (maxRequests <= 0 || windowSeconds <= 0) {
            return true;
        }

        String key = KEY_PREFIX + scope;
        long windowMs = windowSeconds * 1000L;
        long now = System.currentTimeMillis();
        String member = now + "-" + UUID.randomUUID();

        try {
            Long allowed = redisTemplate.execute(
                    slidingWindowScript,
                    Collections.singletonList(key),
                    String.valueOf(windowMs),
                    String.valueOf(maxRequests),
                    String.valueOf(now),
                    member
            );
            if (allowed == null) {
                log.warn("限流脚本返回空，降级放行: {}", key);
                return true;
            }
            return allowed == 1L;
        } catch (Exception e) {
            log.warn("限流 Redis 异常，降级放行: {}", e.getMessage());
            return true;
        }
    }
}
