package com.liuyang.admin.mq;

import com.liuyang.admin.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DemoMessageConsumer {  // 消费者  收消息并打印日志

    private static final Logger log = LoggerFactory.getLogger(DemoMessageConsumer.class);

    @RabbitListener(queues = RabbitMqConfig.DEMO_QUEUE)
    public void onMessage(DemoMessage message) {
        log.info("[MQ 消费] sender={}, content={}, sentAt={}",
                message.getSender(), message.getContent(), message.getSentAt());
    }
}
