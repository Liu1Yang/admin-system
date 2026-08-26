package com.liuyang.admin.mq;

import com.liuyang.admin.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class OperationLogProducer {

    private final RabbitTemplate rabbitTemplate;

    public OperationLogProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(OperationLogMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.OPER_LOG_EXCHANGE,
                RabbitMqConfig.OPER_LOG_ROUTING_KEY,
                message
        );
    }
}
