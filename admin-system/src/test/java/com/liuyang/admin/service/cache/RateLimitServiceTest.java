package com.liuyang.admin.service.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        rateLimitService = new RateLimitService(redisTemplate);
    }

    @Test
    void tryAcquire_shouldAllowWithinLimit() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L, 2L, 3L);

        assertTrue(rateLimitService.tryAcquire("login:127.0.0.1", 3, 60));
        assertTrue(rateLimitService.tryAcquire("login:127.0.0.1", 3, 60));
        assertTrue(rateLimitService.tryAcquire("login:127.0.0.1", 3, 60));
    }

    @Test
    void tryAcquire_shouldRejectWhenExceedLimit() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(11L);

        assertFalse(rateLimitService.tryAcquire("api:127.0.0.1", 10, 60));
    }

    @Test
    void tryAcquire_shouldSetExpireOnFirstRequest() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        rateLimitService.tryAcquire("api:127.0.0.1", 10, 60);

        verify(redisTemplate).expire(anyString(), eq(60L), eq(TimeUnit.SECONDS));
    }

    @Test
    void tryAcquire_shouldSkipWhenLimitDisabled() {
        assertTrue(rateLimitService.tryAcquire("api:127.0.0.1", 0, 60));
        verify(redisTemplate, never()).opsForValue();
    }
}
