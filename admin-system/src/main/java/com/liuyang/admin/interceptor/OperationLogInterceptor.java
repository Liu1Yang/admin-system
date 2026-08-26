package com.liuyang.admin.interceptor;

import com.liuyang.admin.annotation.OperationLog;
import com.liuyang.admin.common.UserContext;
import com.liuyang.admin.entity.User;
import com.liuyang.admin.mapper.UserMapper;
import com.liuyang.admin.mq.OperationLogMessage;
import com.liuyang.admin.mq.OperationLogProducer;
import com.liuyang.admin.service.cache.UserCacheService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Component
public class OperationLogInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTR = "operationLogStartTime";  // 存在 request 里的 key，用于存请求开始时间

    private final OperationLogProducer operationLogProducer;   // 	发送 MQ 消息（异步保存日志）
    private final UserCacheService userCacheService;  //	从缓存取用户信息
    private final UserMapper userMapper; // 缓存没有时从数据库查用户信息

    public OperationLogInterceptor(OperationLogProducer operationLogProducer,
                                   UserCacheService userCacheService,
                                   UserMapper userMapper) {
        this.operationLogProducer = operationLogProducer;
        this.userCacheService = userCacheService;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // ① 不是 Controller 方法 → 直接放行（如静态资源）
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // ② 检查方法上有没有 @OperationLog 注解
        if (handlerMethod.getMethodAnnotation(OperationLog.class) != null) {
            // ③ 有注解 → 记录当前时间（用于后面计算耗时）
            request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        }
        return true; // 继续执行
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, // 请求执行后
                                Object handler, Exception ex) {
        // ① 不是 Controller 方法 → 直接返回
        if (!(handler instanceof HandlerMethod)) {
            return;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // ② 检查方法上有没有 @OperationLog 注解
        OperationLog operationLog = handlerMethod.getMethodAnnotation(OperationLog.class);
        if (operationLog == null) {
            return; // 没有注解 → 不记录日志
        }
        // ③ 收集数据
        Long userId = UserContext.getUserId();
        String username = resolveUsername(userId);
        Integer durationMs = resolveDurationMs(request);

        // ④ 构造消息对象
        OperationLogMessage message = new OperationLogMessage();
        message.setUserId(userId);
        message.setUsername(username);
        message.setModule(operationLog.module()); // 从注解取模块名
        message.setAction(operationLog.action()); // 从注解取操作名
        message.setMethod(request.getMethod());  // HTTP 方法（GET/POST）
        message.setUri(request.getRequestURI());  // 请求路径
        message.setIp(resolveClientIp(request));  // 客户端 IP
        message.setSuccess(ex == null && response.getStatus() < 400); // 成功/失败
        message.setDurationMs(durationMs);       // 耗时
        message.setOccurredAt(LocalDateTime.now()); // 发生时间

        // ⑤ 发送 MQ 消息（异步保存到数据库）
        operationLogProducer.send(message);
    }

    private String resolveUsername(Long userId) { //  获取用户名（带缓存）
        if (userId == null) {
            return "anonymous"; // 未登录用户
        }
        // ① 先从缓存取
        User cached = userCacheService.getById(userId);
        if (cached != null && StringUtils.hasText(cached.getUsername())) {
            return cached.getUsername();
        }
        // ② 缓存没有，从数据库取
        User user = userMapper.selectById(userId);
        return user != null ? user.getUsername() : "user-" + userId;
    }

    private Integer resolveDurationMs(HttpServletRequest request) { // 计算耗时
        Object start = request.getAttribute(START_TIME_ATTR);
        if (start instanceof Long) {
            return (int) (System.currentTimeMillis() - (Long) start);
        }
        return null;
    }

    private String resolveClientIp(HttpServletRequest request) { //获取真实 IP
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
