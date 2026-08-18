# Day37 接口限流

> 基于 **Redis 固定窗口计数**，按 **客户端 IP** 限制请求频率，防止暴力破解与接口滥用。

## 限流规则

| 范围 | 默认限额 | 窗口 | 说明 |
|------|----------|------|------|
| `POST /api/auth/login` | 10 次 | 60 秒 | 防登录暴力破解 |
| 其他 `/api/**` | 100 次 | 60 秒 | 通用 API 保护 |
| `/api/health` | 不限 | — | 健康检查排除 |

超限返回：

```json
{ "code": 429, "message": "请求过于频繁，请稍后再试" }
```

登录超限消息：`登录尝试过于频繁，请稍后再试`

## 配置（application-dev.yml）

```yaml
rate-limit:
  enabled: true
  api-max-requests: 100
  api-window-seconds: 60
  login-max-requests: 10
  login-window-seconds: 60
```

生产环境可通过环境变量覆盖（见 `application-prod.yml`）：

- `RATE_LIMIT_ENABLED`
- `RATE_LIMIT_API_MAX` / `RATE_LIMIT_API_WINDOW`
- `RATE_LIMIT_LOGIN_MAX` / `RATE_LIMIT_LOGIN_WINDOW`

本地调试可临时关闭：

```yaml
rate-limit:
  enabled: false
```

## 实现结构

```text
RateLimitInterceptor (order=0，最先执行)
    ↓
RateLimitService.tryAcquire(scope, max, window)
    ↓
Redis  key: rate:limit:{scope}
       INCR + EXPIRE（固定窗口）
```

| 类 | 作用 |
|----|------|
| `RateLimitProperties` | 读取 yml 配置 |
| `RateLimitService` | Redis 计数 |
| `RateLimitInterceptor` | 按 IP + 路径判断，抛 429 |

拦截器顺序：

```
0 RateLimit  →  1 JWT  →  2 Permission
```

## 客户端 IP 识别

优先级：`X-Forwarded-For` → `X-Real-IP` → `request.getRemoteAddr()`

前置 Nginx 时需正确传递这些 Header。

## 手动验证

连续快速登录 11 次（错误密码即可）：

```powershell
1..11 | ForEach-Object {
  Invoke-RestMethod -Method Post http://localhost:8080/api/auth/login `
    -ContentType "application/json" `
    -Body '{"username":"admin","password":"wrong"}' `
    -ErrorAction SilentlyContinue
}
```

第 11 次应返回 `code: 429`。

## 面试常问

- **固定窗口 vs 滑动窗口：** 固定窗口实现简单（INCR+EXPIRE）；滑动更平滑但实现复杂  
- **为什么用 Redis：** 多实例部署时限流计数需共享  
- **429 vs 403：** 429 表示「太多请求」；403 表示「无权限」  
- **限流粒度：** 本项目按 IP；生产还可按 userId、接口、租户等  

## Day37 验收

- [ ] 登录连续失败超过 10 次/分钟 → 429
- [ ] 正常业务请求不受影响
- [ ] `rate-limit.enabled=false` 可关闭限流
- [ ] `mvn test` 通过（含 RateLimitServiceTest）

## 下一步

- **Day38**：阶段 B 收尾与复习
