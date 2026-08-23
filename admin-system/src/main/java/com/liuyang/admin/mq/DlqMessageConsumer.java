package com.liuyang.admin.mq;

import com.liuyang.admin.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DlqMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(DlqMessageConsumer.class);

    @RabbitListener(queues = RabbitMqConfig.DLQ_QUEUE)
    public void onDeadLetter(DemoMessage message) {
        log.warn("[MQ 死信] sender={}, content={}, sentAt={} — 重试 {} 次后进入死信队列",
                message.getSender(),
                message.getContent(),
                message.getSentAt(),
                RabbitMqConfig.RETRY_MAX_ATTEMPTS);
    }
}
