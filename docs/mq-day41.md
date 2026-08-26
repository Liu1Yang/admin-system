# Day41 MQ 接入业务：异步操作日志

> **目标：** 把 RabbitMQ 接到真实业务——写操作异步落库，接口不被 DB 写入拖慢。

---

## 一、为什么用 MQ 记操作日志？

| 同步写库 | MQ 异步 |
|---------|---------|
| 接口多一次 INSERT，RT 变长 | 接口只发消息，毫秒级返回 |
| DB 抖动直接影响用户 | 消费端可独立扩容、重试 |
| 和业务代码耦合 | 拦截器 + Consumer 解耦 |

**面试点：** 操作日志、审计、通知、缓存刷新等「可最终一致」的场景，都适合 MQ 异步。

---

## 二、整体链路

```text
Controller（带 @OperationLog）
        ↓ 业务执行完成
OperationLogInterceptor.afterCompletion
        ↓ 组装 OperationLogMessage
OperationLogProducer → admin.operlog.exchange
        ↓
admin.operlog.queue
        ↓
OperationLogConsumer → INSERT operation_log
        ↓
GET /api/operation-logs 分页查询
```

---

## 三、关键代码

### 1. 注解标记写操作

```java
@OperationLog(module = "商品", action = "新增")
@RequirePermission("product:write")
@PostMapping
public Result<ProductVO> create(...) { ... }
```

已标注：**商品 / 分类 / 用户** 的增删改等写接口。

### 2. 拦截器发 MQ（不阻塞响应）

`OperationLogInterceptor` 在 `afterCompletion` 里：

- 读取 `@OperationLog`、当前用户、URI、耗时
- 调用 `OperationLogProducer.send()` — **不直接写库**

> `order=3`，`afterCompletion` **逆序**执行，先于 `JwtInterceptor` 清理 `UserContext`，仍能取到 `userId`。

### 3. Consumer 落库

```java
@RabbitListener(queues = RabbitMqConfig.OPER_LOG_QUEUE)
public void onOperationLog(OperationLogMessage message) {
    operationLogService.saveFromMessage(message);
}
```

业务队列用 **自动 ACK**（Day39 默认），简单可靠。

---

## 四、数据库

新环境 Docker 会自动执行 `operation-log.sql`。

**已有库**手动执行：

```powershell
docker cp D:\project\study\admin-system\sql\operation-log.sql admin-mysql:/tmp/operation-log.sql
docker exec -i admin-mysql mysql -u root -p123456 -e "source /tmp/operation-log.sql"
```

（容器名以 `docker ps` 为准。）

---

## 五、验收步骤

**1. 确保 RabbitMQ 运行**

```powershell
cd D:\project\study
docker compose up -d rabbitmq
```

**2. 重启后端**

**3. admin 登录后，做一次写操作**（如新增分类 / 修改商品）

**4. 看 app 日志**

```text
[MQ 操作日志] user=admin module=分类 action=新增 uri=/api/categories success=true 45ms
```

**5. 查操作日志 API**

```powershell
# 先登录拿 Token，再：
Invoke-RestMethod -Headers @{ Authorization = "Bearer <token>" } `
  "http://localhost:8080/api/operation-logs?page=1&size=10"
```

admin 有 `log:read` 权限；liuyang（USER 角色）应返回 403。

**6. RabbitMQ 管理界面**

Queues → `admin.operlog.queue`：消费后 Ready / Unacked 应为 0。

---

## 六、和 Day39 / Day40 对比

| 队列 | 用途 | ACK |
|------|------|-----|
| `admin.demo.queue` | 学习 Demo | 自动 |
| `admin.retry.queue` | 重试 + DLQ 学习 | 手动 |
| `admin.operlog.queue` | **真实业务** 操作日志 | 自动 |

---

## 七、面试怎么说

> 写操作通过 `@OperationLog` + 拦截器收集上下文，**Producer 发 MQ** 后立即返回；**Consumer 异步 INSERT** 到 `operation_log`，查询走独立分页接口。这样主链路 RT 不受日志库影响，后续还可以把 Consumer 拆成独立服务水平扩展。

---

## Day41 验收

- [ ] 写操作后日志有 `[MQ 操作日志]`
- [ ] `GET /api/operation-logs` 能查到记录
- [ ] liuyang 无 `log:read` 时 403
- [ ] 能画清楚「拦截器 → MQ → Consumer → DB」链路

---

## 阶段 C 小结

Day39～41 完成 RabbitMQ 从 Hello World 到业务落地。后续可扩展：

- 操作日志前端页面
- Consumer 失败进 DLQ + 告警
- 商品变更异步刷新 Redis 缓存
