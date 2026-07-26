package com.liuyang.admin.service.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * JWT 黑名单：登出后将 Token 写入 Redis，TTL = Token 剩余有效期。
 */
@Service
public class TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);
    private static final String KEY_PREFIX = "token:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public TokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void add(String token, long ttlSeconds) {
        if (!StringUtils.hasText(token) || ttlSeconds <= 0) {
            return;
        }
        String key = buildKey(token);
        redisTemplate.opsForValue().set(key, "1", ttlSeconds, TimeUnit.SECONDS);
        log.debug("Token 加入黑名单: {}, TTL={}s", key, ttlSeconds);
    }

    public boolean isBlacklisted(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(buildKey(token)));
    }

    private String buildKey(String token) {
        String hash = DigestUtils.md5DigestAsHex(token.getBytes(StandardCharsets.UTF_8));
        return KEY_PREFIX + hash;
    }
}
