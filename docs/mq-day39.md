# Day39 RabbitMQ 入门：Hello MQ

> **目标：** 理解 MQ 基本概念，Docker 跑 RabbitMQ，Spring AMQP 实现一条消息的发送与消费。

---

## 一、为什么要 MQ（1 分钟）

同步调用：

```text
用户登录 → 写库 → 发邮件 → 写日志 → 返回   （邮件慢则整体慢）
```

异步 + MQ：

```text
用户登录 → 写库 → 发 MQ → 立刻返回
                    ↓
              邮件服务消费 / 日志服务消费
```

**解耦、异步、削峰** —— 详见 [mq.md](./mq.md)。

---

## 二、启动 RabbitMQ

### 方式 A：Docker Compose（推荐）

```powershell
cd D:\project\study
docker compose up -d rabbitmq
```

### 方式 B：随全套一起起

```powershell
docker compose up -d --build
```

### 管理界面

| 项 | 值 |
|----|-----|
| URL | http://localhost:15672 |
| 用户 | admin |
| 密码 | 123456 |

登录后 **Queues** 里应看到 `admin.demo.queue`（应用启动后自动声明）。

---

## 三、代码结构

```text
MqDemoController          POST /api/mq/demo  （发消息）
       ↓
DemoMessageProducer       rabbitTemplate.convertAndSend(...)
       ↓
Exchange: admin.demo.exchange  +  routing key: admin.demo
       ↓
Queue: admin.demo.queue
       ↓
DemoMessageConsumer       @RabbitListener 打印日志
```

配置在 `RabbitMqConfig.java`：

- `Queue` — 持久化队列
- `DirectExchange` — 直连交换机（routing key 精确匹配）
- `Binding` — 绑定关系

---

## 四、本地跑后端

**先确保 RabbitMQ 已启动**，再：

```powershell
cd admin-system
mvn spring-boot:run
```

---

## 五、发送测试消息

### PowerShell

```powershell
Invoke-RestMethod -Method Post "http://localhost:8080/api/mq/demo?content=hello&sender=liuyang"
```

### curl

```bash
curl -X POST "http://localhost:8080/api/mq/demo?content=hello&sender=liuyang"
```

期望响应：

```json
{ "code": 200, "message": "success", "data": "消息已发送到 RabbitMQ，请查看 app 日志中的 [MQ 消费]" }
```

### 看消费日志

IDEA 控制台或：

```powershell
docker compose logs -f app
```

应出现：

```text
[MQ 消费] sender=liuyang, content=hello, sentAt=...
```

### 在 RabbitMQ 管理界面验证

1. 打开 Queues → `admin.demo.queue`
2. 发消息后 **Ready** 应为 0（已被 Consumer 消费）
3. **Message rates** 有 publish / deliver 曲线

---

## 六、和 HTTP 调用的区别

| | HTTP 同步 | MQ 异步 |
|--|-----------|---------|
| 调用方 | 等对方处理完 | 发完即走 |
| 耦合 | 要知道对方地址 | 只认队列/交换机 |
| 失败 | 立刻知道 | 需 ACK / 重试机制（Day40） |
| 适用 | 查询、强一致 | 通知、日志、削峰 |

---

## 七、面试怎么说（Day39 水平）

> 用 **RabbitMQ + Spring AMQP** 做过 Demo：Producer 通过 **DirectExchange** 按 routing key 把消息路由到队列，Consumer 用 **@RabbitListener** 订阅。理解 **Exchange / Queue / Binding** 模型，以及 MQ 用于 **异步解耦**，和 Redis 缓存/限流场景不同。

---

## Day39 验收

- [ ] `docker compose up -d rabbitmq` 成功，15672 能登录
- [ ] 后端启动无报错
- [ ] `POST /api/mq/demo` 返回 200
- [ ] 日志出现 `[MQ 消费]`
- [ ] 管理界面能看到 `admin.demo.queue`
- [ ] 能口头解释 Producer → Exchange → Queue → Consumer

---

## 常见问题

| 现象 | 原因 | 处理 |
|------|------|------|
| `Failed to convert Message content` | `Jackson2JsonMessageConverter` 默认不支持 `LocalDateTime` | 已在 `RabbitMqConfig` 注册 `JavaTimeModule` |
| 后端启动连不上 RabbitMQ | MQ 未启动 | `docker compose up -d rabbitmq` |

- 手动 ACK / 自动 ACK
- 消费失败重试
- 死信队列（DLQ）

完成后说「继续 Day40」。
