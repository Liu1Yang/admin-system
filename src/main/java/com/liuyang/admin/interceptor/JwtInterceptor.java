package com.liuyang.admin.interceptor;

import com.liuyang.admin.common.BusinessException;
import com.liuyang.admin.common.JwtUtil;
import com.liuyang.admin.common.UserContext;
import com.liuyang.admin.service.cache.TokenBlacklistService;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtInterceptor(JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = resolveToken(request);   // ① 从 Header 取 Token
        if (!StringUtils.hasText(token)) {    // ② 没 Token → 401
            throw new BusinessException(401, "未登录，请先登录");
        }

        if (tokenBlacklistService.isBlacklisted(token)) { // 是否在黑名单
            throw new BusinessException(401, "Token 已失效，请重新登录");
        }

        try {
            Long userId = jwtUtil.getUserId(token);   // ③ 解析 Token 拿 userId
            UserContext.setUserId(userId);    // ④ 存到当前线程
            return true;                          // ⑤ 放行，继续进 Controller
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(401, "Token 无效或已过期");    // 伪造/过期 → 401
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
