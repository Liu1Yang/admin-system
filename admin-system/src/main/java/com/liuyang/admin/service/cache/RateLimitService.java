package com.liuyang.admin.service.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 固定窗口计数限流（Redis INCR + EXPIRE）。
 */
@Service
public class RateLimitService {

    private static final String KEY_PREFIX = "rate:limit:";

    private final StringRedisTemplate redisTemplate;

    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * @return true 允许通过；false 超过限额
     */
    public boolean tryAcquire(String scope, int maxRequests, int windowSeconds) {
        // ① 如果配置不合理（最大请求数 <=0 或窗口时间 <=0），直接放行
        if (maxRequests <= 0 || windowSeconds <= 0) {
            return true;
        }
        // ② 组装 Redis Key，如 "rate_limit:login:192.168.1.1"
        String key = KEY_PREFIX + scope;
        // ③ Redis 原子自增 +1，返回自增后的值
        Long count = redisTemplate.opsForValue().increment(key);
        // ④ 极端情况：Redis 挂了，降级放行（保证服务可用性）
        if (count == null) {
            return true;
        }
        // ⑤ 如果是第一次请求（自增后为 1），设置过期时间 = 窗口期
        if (count == 1L) {
            redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);
        }
        // ⑥ 判断是否超限
        return count <= maxRequests;
    }
}
