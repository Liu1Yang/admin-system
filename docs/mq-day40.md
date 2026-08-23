# Day40 ACK、重试与死信队列

> **目标：** 理解手动 ACK、消费失败重试、死信队列（DLQ）的完整链路。

---

## 一、Day39 vs Day40

| | Day39 `admin.demo.queue` | Day40 `admin.retry.queue` |
|--|--------------------------|---------------------------|
| ACK | 自动 ACK（默认） | **手动 ACK**（`basicAck`） |
| 失败 | 无演示 | **重试 3 次** |
| 仍失败 | — | 进入 **死信队列** `admin.dlq.queue` |

---

## 二、消息流转（失败场景）

```text
POST /api/mq/retry?content=fail-test
        ↓
admin.retry.exchange → admin.retry.queue
        ↓
RetryMessageConsumer 抛异常（同一条消息重试 3 次，间隔 2s）
        ↓
channel.basicNack(deliveryTag, false, false)   ← requeue=false
        ↓
x-dead-letter-exchange → admin.dlx.exchange
        ↓
admin.dlq.queue → DlqMessageConsumer 打印 [MQ 死信]
```

---

## 三、关键配置

### 1. 队列绑定死信交换机

```java
QueueBuilder.durable(RETRY_QUEUE)
    .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
    .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
    .build();
```

### 2. 手动 ACK + Consumer 内重试

`retryContainerFactory`：`AcknowledgeMode.MANUAL`

`RetryMessageConsumer` 内：

- 成功 → `channel.basicAck(deliveryTag, false)`
- 失败且未耗尽 → `Thread.sleep(2000)` 后同条消息再试
- 第 3 次仍失败 → `channel.basicNack(deliveryTag, false, false)`（**requeue=false** → 死信）

### 3. 模拟失败

`RetryMessageConsumer` 中：`content` 以 **`fail`** 开头 → 抛异常。

> **踩坑：** Spring Retry + `RejectAndDontRequeueRecoverer` 在 **MANUAL ACK** 下，重试耗尽后不会自动 `basicNack`，消息会一直停在 **Unacked**，也进不了 DLQ。Day40 因此在 Consumer 里显式重试 + `basicNack`。

---

## 四、验收步骤

**1. 重启后端**（队列结构有变化，建议重启 RabbitMQ 应用或删旧队列）

若管理界面里已有旧版 `admin.retry.queue`，可先删队列再起应用，或：

```powershell
docker compose restart rabbitmq app
```

**2. 成功路径**

```powershell
Invoke-RestMethod -Method Post "http://localhost:8080/api/mq/retry?content=ok&sender=liuyang"
```

日志：

```text
[MQ Retry 消费] content=ok
[MQ Retry ACK] 消费成功 content=ok
```

**3. 失败 → 重试 → 死信**

```powershell
Invoke-RestMethod -Method Post "http://localhost:8080/api/mq/retry?content=fail-test&sender=liuyang"
```

观察日志（约 6～10 秒内）：

```text
[MQ Retry 消费] content=fail-test     ← 第 1 次
[MQ Retry 消费] content=fail-test     ← 第 2 次（重试）
[MQ Retry 消费] content=fail-test, attempt=3/3
[MQ Retry NACK] 重试 3 次后 requeue=false → 死信队列
[MQ 死信] content=fail-test ...
```

**4. RabbitMQ 管理界面**

http://localhost:15672 → Queues：

| 队列 | 说明 |
|------|------|
| `admin.retry.queue` | 正常消费后应为 0 |
| `admin.dlq.queue` | 失败消息最终在这（消费后也为 0） |

---

## 五、ACK 三种模式

| 模式 | 行为 | 本项目 |
|------|------|--------|
| **AUTO** | 方法正常返回 = ACK，抛异常 = NACK | Day39 Demo |
| **MANUAL** | 代码里显式 `basicAck` / `basicNack` | Day40 Retry |
| **NONE** | 发完就不管 | 很少用 |

**面试点：** 手动 ACK 适合「处理完再确认」；失败可 NACK 并重试或进 DLQ。

---

## 六、死信队列（DLQ）什么时候进

消息进入 DLQ 的常见原因：

1. **消费 reject 且不 requeue**（本项目 Day40）
2. 消息 **TTL 过期**
3. 队列 **长度超限** 被挤出的消息

---

## 七、面试怎么说

> Retry 队列配 **x-dead-letter-exchange**，Consumer 用 **手动 ACK**；失败在代码里重试 3 次，仍失败则 **basicNack(requeue=false)**，消息路由到死信交换机，由 **DLQ Consumer** 记录。

---

## Day40 验收

- [ ] `content=ok` → 日志有 `[MQ Retry ACK]`
- [ ] `content=fail-xxx` → 重试约 3 次后出现 `[MQ 死信]`
- [ ] 管理界面能看到 `admin.dlq.queue`
- [ ] 能解释 AUTO vs MANUAL ACK、DLQ 作用

---

## 下一步 Day41

把 MQ 接到真实业务：**异步操作日志** 或 **商品缓存刷新**。

完成后说「继续 Day41」。
