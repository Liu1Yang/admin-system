package com.liuyang.admin.controller;

import com.liuyang.admin.common.Result;
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

    @Operation(summary = "发送 Demo 消息", description = "Day39 学习用：消息发到 RabbitMQ，Consumer 异步打印日志")
    @PostMapping("/demo")
    public Result<String> sendDemo(
            @RequestParam(defaultValue = "Hello RabbitMQ") String content,
            @RequestParam(defaultValue = "admin-system") String sender) {
        demoMessageProducer.send(content, sender);
        return Result.success("消息已发送到 RabbitMQ，请查看 app 日志中的 [MQ 消费]");
    }
}
