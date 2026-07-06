package com.liuyang.admin.common;

import com.liuyang.admin.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {   // JWT 工具类：专门负责 生成 Token 和 解析/校验 Token。

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtUtil(JwtProperties jwtProperties) {  // 注入 JwtProperties
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // Payload 里存用户 ID（sub 字段）
                .claim("username", username)   // 额外存用户名
                .setIssuedAt(now)                  // 签发时间
                .setExpiration(expireTime)          // 过期时间（现在 + 86400000 毫秒 = 24 小时）
                .signWith(secretKey, SignatureAlgorithm.HS256) // 用密钥签名，防篡改
                .compact();                         // 拼成最终字符串 eyJhbGci...
    }

    public Claims parseToken(String token) {  // 解析并校验 Token
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)   // 用同一个 secret 验签
                .build()
                .parseClaimsJws(token)// 验签名 + 检查是否过期
                .getBody();          // 取出 Payload（Claims 对象）
        //  失败会抛异常： 伪造、改过、过期 → JwtException。
    }

    public Long getUserId(String token) {  //  从 Token 取用户 ID
        return Long.valueOf(parseToken(token).getSubject());
    }
}
