# Day43 操作日志 Consumer 失败进 DLQ + 告警

> **目标：** 操作日志 Consumer 消费失败时重试 3 次，仍失败进入**专用死信队列**，并打 ERROR 级告警日志。

---

## 一、和 Day40 的区别

| | Day40 `admin.dlq.queue` | Day43 `admin.operlog.dlq.queue` |
|--|---------------------------|----------------------------------|
| 消息体 | `DemoMessage` | `OperationLogMessage` |
| 用途 | 学习重试 Demo | **真实业务**操作日志告警 |
| 为何分开 | 同一 DLQ 无法反序列化两种 JSON | 各业务独立 DLQ |

---

## 二、链路

```text
admin.operlog.queue
  → OperationLogConsumer（手动 ACK，重试 3 次）
  → basicNack(requeue=false)
  → admin.dlx.exchange（routing: admin.operlog.dlq）
  → admin.operlog.dlq.queue
  → OperLogDlqConsumer
  → log.error("[MQ 告警] ...")   ← 告警（生产可接钉钉/邮件）
```

---

## 三、关键配置

**operLogQueue 绑定死信：**

```java
QueueBuilder.durable(OPER_LOG_QUEUE)
    .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
    .withArgument("x-dead-letter-routing-key", OPER_LOG_DLQ_ROUTING_KEY)
    .build();
```

**OperLogDlqConsumer 告警：**

```java
log.error("[MQ 告警] 操作日志消费失败进入死信! user={} module={} ...", ...);
```

---

## 四、验收步骤

**1. 队列参数变更：必须删旧队列（常见启动失败原因）**

Day43 给 `admin.operlog.queue` 增加了 `x-dead-letter-exchange`，若 RabbitMQ 里仍是 Day41 创建的旧队列，启动会报：

```text
PRECONDITION_FAILED - inequivalent arg 'x-dead-letter-exchange' for queue 'admin.operlog.queue'
```

**解决（任选其一）：**

- 管理界面 http://localhost:15672 → Queues → `admin.operlog.queue` → **Delete**
- 或命令行：`docker exec admin-rabbitmq rabbitmqctl delete_queue admin.operlog.queue`

删队列后**重启 Spring Boot**，应用会自动用新参数重建队列。

**2. 若 RabbitMQ 未启动**

```powershell
cd D:\project\study
docker compose up -d rabbitmq
```

**3. 发送模拟失败消息：**

```powershell
Invoke-RestMethod -Method Post "http://localhost:8080/api/mq/operlog-fail"
```

**3. 约 6～10 秒内观察日志：**

```text
[MQ 操作日志 消费] ... module=__FAIL__ attempt=1/3
[MQ 操作日志 重试] 第 1 次失败: ...
...
[MQ 操作日志 NACK] 重试 3 次后 requeue=false → 死信队列
[MQ 告警] 操作日志消费失败进入死信! user=system module=__FAIL__ ...
```

**4. RabbitMQ 管理界面**

Queues → `admin.operlog.dlq.queue` 消费后应为 0。

---

## 五、面试怎么说

> 各业务使用**独立 DLQ**，避免 JSON 类型冲突；Consumer 手动 ACK + 本地重试，耗尽后 `basicNack(requeue=false)` 进 DLX；DLQ Consumer 打 ERROR 日志作为告警，生产环境可对接监控系统。

---

## Day43 验收

- [ ] `POST /api/mq/operlog-fail` 后出现 `[MQ 告警]`
- [ ] `admin.operlog.dlq.queue` 可见消息流转
- [ ] 正常写操作仍走 `[MQ 操作日志 ACK]`

---

## 下一步

[Day44 商品变更异步刷新 Redis 缓存](./mq-day44.md)
