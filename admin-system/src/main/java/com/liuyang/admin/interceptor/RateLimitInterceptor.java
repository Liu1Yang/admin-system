package com.liuyang.admin.interceptor;

import com.liuyang.admin.common.BusinessException;
import com.liuyang.admin.config.RateLimitProperties;
import com.liuyang.admin.service.cache.RateLimitService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {  // 限流拦截器

    private static final String LOGIN_PATH = "/api/auth/login";

    private final RateLimitProperties rateLimitProperties;
    private final RateLimitService rateLimitService;

    public RateLimitInterceptor(RateLimitProperties rateLimitProperties,
                                RateLimitService rateLimitService) {
        this.rateLimitProperties = rateLimitProperties;
        this.rateLimitService = rateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // ① 限流开关关闭 → 直接放行
        if (!rateLimitProperties.isEnabled()) {
            return true;
        }
        // ② 浏览器预检请求 → 直接放行（不带 Token，也不计费）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // ③ 获取客户端真实 IP
        String clientIp = resolveClientIp(request);
        String uri = request.getRequestURI();

        // ④ 登录接口单独限流（更严格）
        if (LOGIN_PATH.equals(uri)) {
            String scope = "login:" + clientIp;
            if (!rateLimitService.tryAcquire(scope,
                    rateLimitProperties.getLoginMaxRequests(),
                    rateLimitProperties.getLoginWindowSeconds())) {
                throw new BusinessException(429, "登录尝试过于频繁，请稍后再试");
            }
        }

        // ⑤ 所有 API 接口限流（包括登录接口也会走到这里）
        String apiScope = "api:" + clientIp;
        if (!rateLimitService.tryAcquire(apiScope,
                rateLimitProperties.getApiMaxRequests(),
                rateLimitProperties.getApiWindowSeconds())) {
            throw new BusinessException(429, "请求过于频繁，请稍后再试");
        }

        return true;
    }

    private String resolveClientIp(HttpServletRequest request) {
        // 如果经过 Nginx 等反向代理，取 X-Forwarded-For
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            return xff.split(",")[0].trim();
        }
        // 取 X-Real-IP
        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return realIp.trim();
        }
        // 直连时直接取远程地址
        return request.getRemoteAddr();
    }
}
