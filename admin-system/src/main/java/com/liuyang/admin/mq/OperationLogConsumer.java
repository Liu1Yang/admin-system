package com.liuyang.admin.mq;

import com.liuyang.admin.config.RabbitMqConfig;
import com.liuyang.admin.service.OperationLogService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OperationLogConsumer {

    /** Day43 验收：module 为此值时模拟消费失败，重试后进 operlog 专用 DLQ */
    public static final String FAIL_MODULE = "__FAIL__";

    private static final Logger log = LoggerFactory.getLogger(OperationLogConsumer.class);

    private final OperationLogService operationLogService;

    public OperationLogConsumer(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @RabbitListener(queues = RabbitMqConfig.OPER_LOG_QUEUE, containerFactory = "retryContainerFactory")
    public void onOperationLog(OperationLogMessage message, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException, InterruptedException {
        int maxAttempts = RabbitMqConfig.RETRY_MAX_ATTEMPTS;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            log.info("[MQ 操作日志 消费] user={} module={} action={} attempt={}/{}",
                    message.getUsername(), message.getModule(), message.getAction(), attempt, maxAttempts);
            try {
                if (FAIL_MODULE.equals(message.getModule())) {
                    throw new IllegalStateException("模拟操作日志消费失败（Day43 验收）");
                }
                operationLogService.saveFromMessage(message);
                channel.basicAck(deliveryTag, false);
                log.info("[MQ 操作日志 ACK] user={} module={} action={} uri={} success={} {}ms",
                        message.getUsername(),
                        message.getModule(),
                        message.getAction(),
                        message.getUri(),
                        message.getSuccess(),
                        message.getDurationMs());
                return;
            } catch (Exception e) {
                if (attempt < maxAttempts) {
                    log.warn("[MQ 操作日志 重试] 第 {} 次失败: {}", attempt, e.getMessage());
                    Thread.sleep(2000L);
                } else {
                    channel.basicNack(deliveryTag, false, false);
                    log.warn("[MQ 操作日志 NACK] 重试 {} 次后 requeue=false → 死信队列", maxAttempts);
                }
            }
        }
    }
}
