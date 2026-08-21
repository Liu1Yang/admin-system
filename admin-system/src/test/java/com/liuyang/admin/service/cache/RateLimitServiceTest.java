package com.liuyang.admin.service.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        rateLimitService = new RateLimitService(redisTemplate);
    }

    @Test
    void tryAcquire_shouldAllowWhenLuaReturnsOne() {
        when(redisTemplate.execute(any(RedisScript.class), anyList(), any(), any(), any(), any()))
                .thenReturn(1L);

        assertTrue(rateLimitService.tryAcquire("login:127.0.0.1", 10, 60));
    }

    @Test
    void tryAcquire_shouldRejectWhenLuaReturnsZero() {
        when(redisTemplate.execute(any(RedisScript.class), anyList(), any(), any(), any(), any()))
                .thenReturn(0L);

        assertFalse(rateLimitService.tryAcquire("api:127.0.0.1", 100, 60));
    }

    @Test
    void tryAcquire_shouldDegradeAllowWhenRedisFails() {
        when(redisTemplate.execute(any(RedisScript.class), anyList(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("redis down"));

        assertTrue(rateLimitService.tryAcquire("api:127.0.0.1", 100, 60));
    }

    @Test
    void tryAcquire_shouldSkipWhenLimitDisabled() {
        assertTrue(rateLimitService.tryAcquire("api:127.0.0.1", 0, 60));
        verify(redisTemplate, never()).execute(any(RedisScript.class), anyList(), anyString());
    }
}
