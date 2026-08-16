package com.liuyang.admin.service.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(redisTemplate);
    }

    @Test
    void store_shouldWriteRedisWithTtl() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        refreshTokenService.store("refresh-token", 1L, 3600L);

        verify(valueOperations).set(anyString(), eq("1"), eq(3600L), eq(TimeUnit.SECONDS));
    }

    @Test
    void getUserIdIfValid_shouldReturnUserId() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn("42");

        assertEquals(42L, refreshTokenService.getUserIdIfValid("refresh-token"));
    }

    @Test
    void getUserIdIfValid_shouldReturnNullWhenMissing() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);

        assertNull(refreshTokenService.getUserIdIfValid("refresh-token"));
    }

    @Test
    void revoke_shouldDeleteKey() {
        refreshTokenService.revoke("refresh-token");

        verify(redisTemplate).delete(anyString());
    }
}
