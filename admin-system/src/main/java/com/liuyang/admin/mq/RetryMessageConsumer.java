package com.liuyang.admin.mq;

import com.liuyang.admin.config.RabbitMqConfig;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RetryMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(RetryMessageConsumer.class);

    @RabbitListener(queues = RabbitMqConfig.RETRY_QUEUE, containerFactory = "retryContainerFactory")
    public void onRetryMessage(DemoMessage message, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException, InterruptedException {
        int maxAttempts = RabbitMqConfig.RETRY_MAX_ATTEMPTS;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            log.info("[MQ Retry 消费] content={}, attempt={}/{}", message.getContent(), attempt, maxAttempts);
            try {
                if (message.getContent() != null && message.getContent().startsWith("fail")) {
                    throw new IllegalStateException("模拟消费失败: " + message.getContent());
                }
                channel.basicAck(deliveryTag, false);// 告诉 RabbitMQ：“这条消息我处理好了，你可以删掉了
                log.info("[MQ Retry ACK] 消费成功 content={}", message.getContent());
                return;
            } catch (Exception e) {
                if (attempt < maxAttempts) {
                    log.warn("[MQ Retry 重试] 第 {} 次失败: {}", attempt, e.getMessage());
                    Thread.sleep(2000L);
                } else {
                    // requeue=false → 触发 x-dead-letter-exchange，进入 admin.dlq.queue
                    channel.basicNack(deliveryTag, false, false); // 第三个参数 false = requeue=false（不重新入队） → 进入死信队列。
                    log.warn("[MQ Retry NACK] 重试 {} 次后 requeue=false → 死信队列, content={}",
                            maxAttempts, message.getContent());
                }
            }
        }
    }
}
