package com.liuyang.admin.mq;

import com.liuyang.admin.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DemoMessageProducer {

    private final RabbitTemplate rabbitTemplate; //   声明，没有赋值

    public DemoMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;  // final 修饰的变量只能在构造方法里赋值一次，之后不能再改。
    }

    public void send(String content, String sender) {
        DemoMessage message = new DemoMessage();
        message.setContent(content);
        message.setSender(sender);
        message.setSentAt(LocalDateTime.now());

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.DEMO_EXCHANGE,    // 交换机名
                RabbitMqConfig.DEMO_ROUTING_KEY, // 路由键
                message                          // 消息体
        );
    }
}
