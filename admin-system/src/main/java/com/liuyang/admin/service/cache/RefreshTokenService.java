package com.liuyang.admin.service.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Refresh Token 存 Redis，支持登出吊销与刷新校验。
 */
@Service
public class RefreshTokenService {

    private static final String KEY_PREFIX = "refresh:token:";

    private final StringRedisTemplate redisTemplate;

    public RefreshTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void store(String refreshToken, Long userId, long ttlSeconds) {
        if (!StringUtils.hasText(refreshToken) || userId == null || ttlSeconds <= 0) {
            return;
        }
        redisTemplate.opsForValue().set(
                buildKey(refreshToken),  // 给 Redis 的 Key 加上统一前缀，方便管理和隔离。 如  "refresh:" + refreshToken;
                String.valueOf(userId),
                ttlSeconds,
                TimeUnit.SECONDS
        );
    }

    public Long getUserIdIfValid(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return null;
        }
        String userId = redisTemplate.opsForValue().get(buildKey(refreshToken));
        if (!StringUtils.hasText(userId)) {
            return null;
        }
        return Long.valueOf(userId);
    }

    public void revoke(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return;
        }
        redisTemplate.delete(buildKey(refreshToken));
    }

    private String buildKey(String refreshToken) {
        String hash = DigestUtils.md5DigestAsHex(refreshToken.getBytes(StandardCharsets.UTF_8));
        return KEY_PREFIX + hash;
    }
}
