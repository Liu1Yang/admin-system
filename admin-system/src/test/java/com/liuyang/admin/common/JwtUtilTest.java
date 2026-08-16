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

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-jwt-secret-key-at-least-32-chars");
        properties.setExpiration(3600000L);
        properties.setRefreshExpiration(86400000L);
        jwtUtil = new JwtUtil(properties);
    }

    @Test
    void generateAccessToken_shouldContainUserIdAndType() {
        String token = jwtUtil.generateAccessToken(1L, "admin");

        assertNotNull(token);
        assertEquals(1L, jwtUtil.getUserId(token));
        assertEquals(JwtUtil.TYPE_ACCESS, jwtUtil.getTokenType(token));
        assertEquals("admin", jwtUtil.parseToken(token).get("username"));
    }

    @Test
    void generateRefreshToken_shouldHaveRefreshType() {
        String token = jwtUtil.generateRefreshToken(2L, "liuyang");

        assertEquals(JwtUtil.TYPE_REFRESH, jwtUtil.getTokenType(token));
    }

    @Test
    void validateTokenType_shouldRejectWrongType() {
        String refresh = jwtUtil.generateRefreshToken(1L, "admin");

        assertThrows(Exception.class, () -> jwtUtil.validateTokenType(refresh, JwtUtil.TYPE_ACCESS));
    }

    @Test
    void getRemainingSeconds_shouldBePositive() {
        String token = jwtUtil.generateAccessToken(2L, "liuyang");

        assertTrue(jwtUtil.getRemainingSeconds(token) > 0);
    }

    @Test
    void parseToken_shouldFailWhenTampered() {
        String token = jwtUtil.generateAccessToken(1L, "admin") + "x";

        assertThrows(Exception.class, () -> jwtUtil.parseToken(token));
    }

    @Test
    void parseToken_shouldFailWhenExpired() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-jwt-secret-key-at-least-32-chars");
        properties.setExpiration(-1000L);
        JwtUtil expiredUtil = new JwtUtil(properties);

        String token = expiredUtil.generateAccessToken(1L, "admin");

        assertThrows(ExpiredJwtException.class, () -> expiredUtil.parseToken(token));
    }
}
