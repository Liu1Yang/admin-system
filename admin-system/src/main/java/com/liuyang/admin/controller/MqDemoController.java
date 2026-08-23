package com.liuyang.admin.controller;

import com.liuyang.admin.common.Result;
import com.liuyang.admin.config.RabbitMqConfig;
import com.liuyang.admin.mq.DemoMessageProducer;
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

    public MqDemoController(DemoMessageProducer demoMessageProducer) {
        this.demoMessageProducer = demoMessageProducer;
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
}
