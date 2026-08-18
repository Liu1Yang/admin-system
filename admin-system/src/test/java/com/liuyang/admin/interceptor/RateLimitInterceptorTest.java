package com.liuyang.admin.interceptor;

import com.liuyang.admin.common.BusinessException;
import com.liuyang.admin.config.RateLimitProperties;
import com.liuyang.admin.service.cache.RateLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitInterceptorTest {

    @Mock
    private RateLimitService rateLimitService;

    private RateLimitProperties properties;
    private RateLimitInterceptor interceptor;

    @BeforeEach
    void setUp() {
        properties = new RateLimitProperties();
        properties.setEnabled(true);
        properties.setLoginMaxRequests(10);
        properties.setLoginWindowSeconds(60);
        properties.setApiMaxRequests(100);
        properties.setApiWindowSeconds(60);
        interceptor = new RateLimitInterceptor(properties, rateLimitService);
    }

    @Test
    void preHandle_shouldPassWhenWithinLimit() {
        when(rateLimitService.tryAcquire(anyString(), anyInt(), anyInt())).thenReturn(true);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.setRemoteAddr("127.0.0.1");

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void preHandle_shouldRejectLoginWhenExceedLimit() {
        when(rateLimitService.tryAcquire(eq("login:127.0.0.1"), anyInt(), anyInt())).thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.setRemoteAddr("127.0.0.1");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
        assertEquals(429, ex.getCode());
    }

    @Test
    void preHandle_shouldSkipWhenDisabled() {
        properties.setEnabled(false);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.setRemoteAddr("127.0.0.1");

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }
}
