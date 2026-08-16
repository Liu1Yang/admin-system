package com.liuyang.admin.common;

import com.liuyang.admin.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    public static final String CLAIM_TYPE = "type";
    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId, String username) {
        return buildToken(userId, username, jwtProperties.getExpiration(), TYPE_ACCESS);
    }

    public String generateRefreshToken(Long userId, String username) {
        return buildToken(userId, username, jwtProperties.getRefreshExpiration(), TYPE_REFRESH);
    }

    /** 兼容旧调用 */
    public String generateToken(Long userId, String username) {
        return generateAccessToken(userId, username);
    }

    private String buildToken(Long userId, String username, long expireMs, String type) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + expireMs);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim(CLAIM_TYPE, type)
                .setIssuedAt(now)
                .setExpiration(expireTime)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserId(String token) {
        return Long.valueOf(parseToken(token).getSubject());
    }

    public String getTokenType(String token) {
        Object type = parseToken(token).get(CLAIM_TYPE);
        return type == null ? null : type.toString();
    }

    public void validateTokenType(String token, String expectedType) {
        String type = getTokenType(token);
        if (!expectedType.equals(type)) {
            throw new JwtException("Token 类型无效");
        }
    }

    public long getRemainingSeconds(String token) {
        Date expiration = parseToken(token).getExpiration();
        long seconds = (expiration.getTime() - System.currentTimeMillis()) / 1000;
        return Math.max(seconds, 1L);
    }
}
