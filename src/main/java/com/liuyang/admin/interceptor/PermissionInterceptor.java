package com.liuyang.admin.interceptor;

import com.liuyang.admin.annotation.RequirePermission;
import com.liuyang.admin.common.BusinessException;
import com.liuyang.admin.common.UserContext;
import com.liuyang.admin.service.PermissionService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    private final PermissionService permissionService;

    public PermissionInterceptor(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (requirePermission == null) {
            requirePermission = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
        }
        if (requirePermission == null) {
            return true;
        }

        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录，请先登录");
        }

        String permissionCode = requirePermission.value();
        if (!permissionService.hasPermission(userId, permissionCode)) {
            throw new BusinessException(403, "无权限访问");
        }
        return true;
    }
}
