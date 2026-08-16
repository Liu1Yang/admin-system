package com.liuyang.admin.service.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // 告诉 JUnit 5 启用 Mockito 扩展，让 @Mock 注解生效。
class TokenBlacklistServiceTest {   // 测试“退出登录”功能

    @Mock
    private StringRedisTemplate redisTemplate; // StringRedisTemplate 是 Spring 操作 Redis 的工具类

    @Mock
    private ValueOperations<String, String> valueOperations; // Redis 的字符串操作（set、get 等）

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService(redisTemplate);
    }

    @Test
    void add_shouldWriteRedisWithTtl() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        tokenBlacklistService.add("sample-token", 3600L);

        verify(valueOperations).set(anyString(), eq("1"), eq(3600L), eq(TimeUnit.SECONDS));
    }

    @Test
    void isBlacklisted_shouldReturnTrueWhenKeyExists() {
        when(redisTemplate.hasKey(anyString())).thenReturn(true);

        assertTrue(tokenBlacklistService.isBlacklisted("sample-token"));
    }

    @Test
    void isBlacklisted_shouldReturnFalseWhenKeyMissing() {
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        assertFalse(tokenBlacklistService.isBlacklisted("sample-token"));
    }

    @Test
    void add_shouldUseMd5HashAsKeySuffix() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);

        tokenBlacklistService.add("abc", 60L);

        verify(valueOperations).set(keyCaptor.capture(), eq("1"), eq(60L), eq(TimeUnit.SECONDS));
        assertTrue(keyCaptor.getValue().startsWith("token:blacklist:"));
    }
}
