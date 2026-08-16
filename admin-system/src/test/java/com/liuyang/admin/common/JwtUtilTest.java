package com.liuyang.admin.common;

import com.liuyang.admin.config.JwtProperties;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach  // 每个测试方法执行前都会跑一遍，用于初始化测试环境。
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-jwt-secret-key-at-least-32-chars");
        properties.setExpiration(3600000L);
        jwtUtil = new JwtUtil(properties);
    }

    @Test
    void generateToken_shouldContainUserIdAndUsername() { // 验证生成的 Token 里能正确取出 userId 和 username。
        String token = jwtUtil.generateToken(1L, "admin");

        assertNotNull(token);
        assertEquals(1L, jwtUtil.getUserId(token));
        assertEquals("admin", jwtUtil.parseToken(token).get("username"));
    }

    @Test
    void getRemainingSeconds_shouldBePositive() {
        String token = jwtUtil.generateToken(2L, "liuyang");

        assertTrue(jwtUtil.getRemainingSeconds(token) > 0);
    }

    @Test
    void parseToken_shouldFailWhenTampered() {
        String token = jwtUtil.generateToken(1L, "admin") + "x";

        assertThrows(Exception.class, () -> jwtUtil.parseToken(token));
    }

    @Test
    void parseToken_shouldFailWhenExpired() { // 创造一个已过期的 Token，验证解析时是否抛 ExpiredJwtException。
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-jwt-secret-key-at-least-32-chars");
        properties.setExpiration(-1000L);
        JwtUtil expiredUtil = new JwtUtil(properties);

        String token = expiredUtil.generateToken(1L, "admin");

        assertThrows(ExpiredJwtException.class, () -> expiredUtil.parseToken(token));
    }
}
