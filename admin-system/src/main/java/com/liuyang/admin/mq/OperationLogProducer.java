package com.liuyang.admin.mq;

import com.liuyang.admin.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    /** Day43 验收：发送必定消费失败的消息，重试后进 operlog 死信队列 */
    public void sendFailTest() {
        OperationLogMessage message = new OperationLogMessage();
        message.setUserId(0L);
        message.setUsername("system");
        message.setModule(OperationLogConsumer.FAIL_MODULE);
        message.setAction("测试失败");
        message.setMethod("POST");
        message.setUri("/api/mq/operlog-fail");
        message.setIp("127.0.0.1");
        message.setSuccess(false);
        message.setDurationMs(0);
        message.setOccurredAt(LocalDateTime.now());
        send(message);
    }
}
