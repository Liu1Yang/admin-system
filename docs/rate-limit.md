# Day37 接口限流

> 基于 **Redis ZSET + Lua 滑动窗口**，按 **客户端 IP** 限制请求频率。  
> Lua 把「删过期记录 → 计数 → 写入」放进一次 `EVAL`，避免并发下计数不准。

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
RateLimitInterceptor (order=0)
    ↓
RateLimitService.tryAcquire(scope, max, window)
    ↓
EVAL lua/sliding_window_rate_limit.lua
    Redis ZSET  key: rate:limit:{scope}
    score = 请求时间戳(ms)
    member = 时间戳-UUID
```

Lua 步骤：

1. `ZREMRANGEBYSCORE` 删掉窗口外的记录  
2. `ZCARD` 统计窗口内次数  
3. 未超限则 `ZADD` 本次请求，并 `PEXPIRE` 窗口时长  

| 类 | 作用 |
|----|------|
| `RateLimitProperties` | 读取 yml 配置 |
| `RateLimitService` | 执行 Lua 脚本 |
| `RateLimitInterceptor` | 按 IP + 路径判断，抛 429 |

拦截器顺序：

```
0 RateLimit  →  1 JWT  →  2 Permission
```

Redis 异常时 **降级放行**（宁可放过，也不把业务打挂）。

## 三种算法对比（面试）

| 算法 | 原理 | 优点 | 缺点 |
|------|------|------|------|
| 固定窗口 INCR+EXPIRE | 每个整点窗口计数 | 简单、省内存 | 窗口交界可能突发 2 倍流量 |
| 滑动窗口 ZSET | 记录每次请求时间，统计最近 N 秒 | 精确、无边界尖峰 | 每次请求存一条，内存随 QPS 涨 |
| **滑动窗口 Lua+ZSET（当前）** | 同上，但 Lua 保证原子性 | 精确 + 并发安全 | 实现稍复杂 |

固定窗口问题示例：限额 10 次/分钟，59 秒打 10 次、下一分钟 0 秒再打 10 次 → 实际 1 秒内 20 次。滑动窗口按「过去 60 秒」计数，不会出现这种尖峰。

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

## 面试怎么说

- **怎么做限流？** 登录按 IP 10 次/分钟，业务 API 100 次/分钟；用 Redis **滑动窗口（ZSET + Lua）**，避免固定窗口边界突发。
- **为什么要 Lua？** `ZREMRANGE`、`ZCARD`、`ZADD` 若分多次调用，并发下会超卖；Lua 在 Redis 单线程里一次跑完。
- **为什么不用固定窗口？** 学习项目早期可以用 INCR；对登录防爆破够用，但对精确 QPS 控制，滑动窗口更稳。
- **ZSET 内存怎么办？** 窗口结束后 `PEXPIRE` 清 key；高 QPS 可再考虑令牌桶。
- **429 vs 403：** 429 太多请求；403 无权限。

## Day37 验收

- [ ] 登录连续失败超过 10 次/分钟 → 429
- [ ] 正常业务请求不受影响
- [ ] `rate-limit.enabled=false` 可关闭限流
- [ ] `mvn test` 通过（含 RateLimitServiceTest）
- [ ] 能讲清固定窗口边界问题，以及 Lua 保证原子性的原因

## 下一步

- **Day38**：阶段 B 收尾 → [review.md](./review.md)
