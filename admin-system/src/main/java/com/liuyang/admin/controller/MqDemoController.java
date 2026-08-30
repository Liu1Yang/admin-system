package com.liuyang.admin.controller;

import com.liuyang.admin.common.Result;
import com.liuyang.admin.config.RabbitMqConfig;
import com.liuyang.admin.mq.DemoMessageProducer;
import com.liuyang.admin.mq.OperationLogProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MQ 学习 Demo")
@RestController
@RequestMapping("/api/mq")
public class MqDemoController {

    private final DemoMessageProducer demoMessageProducer;
    private final OperationLogProducer operationLogProducer;

    public MqDemoController(DemoMessageProducer demoMessageProducer,
                              OperationLogProducer operationLogProducer) {
        this.demoMessageProducer = demoMessageProducer;
        this.operationLogProducer = operationLogProducer;
    }

    @Operation(summary = "发送 Demo 消息", description = "Day39：自动 ACK，Consumer 打印日志")
    @PostMapping("/demo")
    public Result<String> sendDemo(
            @RequestParam(defaultValue = "Hello RabbitMQ") String content,
            @RequestParam(defaultValue = "admin-system") String sender) {
        demoMessageProducer.send(content, sender);
        return Result.success("消息已发送到 RabbitMQ，请查看 app 日志中的 [MQ 消费]");
    }

    @Operation(summary = "发送 Retry 消息", description = "Day40：手动 ACK + 重试 + 死信。content 以 fail 开头会失败")
    @PostMapping("/retry")
    public Result<String> sendRetry(
            @RequestParam String content,
            @RequestParam(defaultValue = "admin-system") String sender) {
        demoMessageProducer.sendRetry(content, sender);
        return Result.success(String.format(
                "已发送到 retry 队列。content 以 fail 开头将重试 %d 次后进入死信队列 admin.dlq.queue",
                RabbitMqConfig.RETRY_MAX_ATTEMPTS));
    }

    @Operation(summary = "模拟操作日志消费失败", description = "Day43：重试 3 次后进 admin.operlog.dlq.queue，日志出现 [MQ 告警]")
    @PostMapping("/operlog-fail")
    public Result<String> sendOperLogFail() {
        operationLogProducer.sendFailTest();
        return Result.success(String.format(
                "已发送失败消息，将重试 %d 次后进入 admin.operlog.dlq.queue，请查看 [MQ 告警] 日志",
                RabbitMqConfig.RETRY_MAX_ATTEMPTS));
    }
}
