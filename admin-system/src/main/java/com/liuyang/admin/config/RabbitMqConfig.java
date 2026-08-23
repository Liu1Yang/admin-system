package com.liuyang.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    public static final String DEMO_QUEUE = "admin.demo.queue";  //  队列名称，存消息的地方
    public static final String DEMO_EXCHANGE = "admin.demo.exchange";  // 交换机名称，消息先到这里
    public static final String DEMO_ROUTING_KEY = "admin.demo";  // 路由键，决定消息从交换机去哪个队列

    @Bean
    public Queue demoQueue() {
        return QueueBuilder.durable(DEMO_QUEUE) // 创建一个持久化队列（RabbitMQ 重启后队列还在创建一个持久化队列（RabbitMQ 重启后队列还在
                .build(); // 真正创建队列对象
    }

    @Bean
    public DirectExchange demoExchange() {

        return new DirectExchange(DEMO_EXCHANGE); //  创建分拣中心（交换机）
    }

    @Bean
    public Binding demoBinding(Queue demoQueue, DirectExchange demoExchange) {
        return BindingBuilder
                .bind(demoQueue).to(demoExchange) // 	把队列绑定到交换机
                .with(DEMO_ROUTING_KEY); // 规定：只有带路由键 admin.demo 的消息才进这个队列
    }

    // 这个方法就是“生产一个转换器 Bean”，没有其他逻辑。
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());//Jackson 知道了 LocalDateTime 应该转成 "2026-08-23T14:18:03" 这样的字符串，不再报错。
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, // 告诉快递员“怎么连接 RabbitMQ 服务器”
                                         Jackson2JsonMessageConverter messageConverter) { // 给快递员配一台打包机
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter); // 它负责把消息打包（转 JSON）、送到交换机。
        return template;
    }
}
