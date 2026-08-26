# 阶段 C：消息队列（MQ）学习路线

> 在阶段 B（Day38）完成后开始。选型：**RabbitMQ**（入门友好，Spring AMQP 生态成熟）。

## 学习顺序

| Day | 主题 | 文档 | 状态 |
|-----|------|------|------|
| **Day39** | 概念 + Docker + 生产者/消费者 Hello | [mq-day39.md](./mq-day39.md) | ✅ |
| **Day40** | ACK、重试、死信队列 | [mq-day40.md](./mq-day40.md) | ✅ |
| **Day41** | 接入业务：异步操作日志 | [mq-day41.md](./mq-day41.md) | ✅ |

## 和 Redis 的分工

| | Redis | RabbitMQ |
|--|--------|----------|
| 本项目用途 | 缓存、黑名单、限流、Refresh Token | **异步任务、解耦** |
| 消息堆积 | 不适合长期大量堆 | 专为堆积设计 |
| 学习阶段 | Day20 左右 | **Day39 起** |

## 快速启动 RabbitMQ

```powershell
# 只起 MQ（本地 mvn spring-boot:run 时）
cd D:\project\study
docker compose up -d rabbitmq

# 或全套
docker compose up -d --build
```

管理界面：http://localhost:15672  
账号：**admin / 123456**

## 核心概念（先记住）

```text
Producer  →  Exchange  →  (routing key)  →  Queue  →  Consumer
              交换机                         队列        消费者
```

- **Producer**：发消息（`DemoMessageProducer`）
- **Exchange**：路由规则（`DirectExchange`）
- **Queue**：存消息（`admin.demo.queue`）
- **Consumer**：收消息（`@RabbitListener`）
- **Broker**：RabbitMQ 服务器本身

## 项目内 MQ 相关文件

```text
admin-system/
├── config/RabbitMqConfig.java    # 队列、交换机、DLQ、retryContainerFactory
├── mq/DemoMessage.java
├── mq/DemoMessageProducer.java
├── mq/DemoMessageConsumer.java   # Day39 自动 ACK
├── mq/RetryMessageConsumer.java  # Day40 手动 ACK + 重试 + basicNack
├── mq/DlqMessageConsumer.java    # Day40 死信消费
├── mq/OperationLogMessage.java   # Day41 操作日志消息体
├── mq/OperationLogProducer.java  # Day41 发 MQ
├── mq/OperationLogConsumer.java  # Day41 异步落库
├── annotation/OperationLog.java  # Day41 标记写操作
├── interceptor/OperationLogInterceptor.java
├── controller/OperationLogController.java
└── controller/MqDemoController.java
```

## 下一步

完成 [Day41 验收](./mq-day41.md) 后，阶段 C MQ 学习告一段落。
