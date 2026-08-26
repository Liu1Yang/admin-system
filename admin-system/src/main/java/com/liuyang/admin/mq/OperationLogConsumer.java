package com.liuyang.admin.mq;

import com.liuyang.admin.config.RabbitMqConfig;
import com.liuyang.admin.service.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OperationLogConsumer {

    private static final Logger log = LoggerFactory.getLogger(OperationLogConsumer.class);

    private final OperationLogService operationLogService;

    public OperationLogConsumer(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @RabbitListener(queues = RabbitMqConfig.OPER_LOG_QUEUE)
    public void onOperationLog(OperationLogMessage message) {
        operationLogService.saveFromMessage(message);
        log.info("[MQ 操作日志] user={} module={} action={} uri={} success={} {}ms",
                message.getUsername(),
                message.getModule(),
                message.getAction(),
                message.getUri(),
                message.getSuccess(),
                message.getDurationMs());
    }
}
