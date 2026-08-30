package com.liuyang.admin.mq;

import com.liuyang.admin.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OperLogDlqConsumer {

    private static final Logger log = LoggerFactory.getLogger(OperLogDlqConsumer.class);

    @RabbitListener(queues = RabbitMqConfig.OPER_LOG_DLQ_QUEUE)
    public void onOperLogDeadLetter(OperationLogMessage message) {
        log.error("[MQ 告警] 操作日志消费失败进入死信! user={} module={} action={} uri={} — 请排查 DB/MQ 或人工补录",
                message.getUsername(),
                message.getModule(),
                message.getAction(),
                message.getUri());
    }
}
