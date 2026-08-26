package com.liuyang.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    // Day39 Demo
    public static final String DEMO_QUEUE = "admin.demo.queue";
    public static final String DEMO_EXCHANGE = "admin.demo.exchange";
    public static final String DEMO_ROUTING_KEY = "admin.demo";

    // Day40 Retry + DLQ
    public static final String RETRY_QUEUE = "admin.retry.queue";
    public static final String RETRY_EXCHANGE = "admin.retry.exchange";
    public static final String RETRY_ROUTING_KEY = "admin.retry";

    public static final String DLX_EXCHANGE = "admin.dlx.exchange";  // 死信交换机
    public static final String DLQ_QUEUE = "admin.dlq.queue";       // 死信队列
    public static final String DLQ_ROUTING_KEY = "admin.dlq";       // 死信路由键

    public static final int RETRY_MAX_ATTEMPTS = 3;

    // Day41 操作日志（业务队列，自动 ACK）
    public static final String OPER_LOG_QUEUE = "admin.operlog.queue";
    public static final String OPER_LOG_EXCHANGE = "admin.operlog.exchange";
    public static final String OPER_LOG_ROUTING_KEY = "admin.operlog";

    @Bean
    public Queue demoQueue() {
        return QueueBuilder.durable(DEMO_QUEUE).build();
    }

    @Bean
    public DirectExchange demoExchange() {
        return new DirectExchange(DEMO_EXCHANGE);
    }

    @Bean
    public Binding demoBinding(Queue demoQueue, DirectExchange demoExchange) {
        return BindingBuilder.bind(demoQueue).to(demoExchange).with(DEMO_ROUTING_KEY);
    }

    /** 业务队列：消费失败且不再 requeue 时，消息进入死信交换机 */
    @Bean
    public Queue retryQueue() {
        return QueueBuilder.durable(RETRY_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public DirectExchange retryExchange() {
        return new DirectExchange(RETRY_EXCHANGE);
    }

    @Bean
    public Binding retryBinding(Queue retryQueue, DirectExchange retryExchange) {
        return BindingBuilder.bind(retryQueue).to(retryExchange).with(RETRY_ROUTING_KEY);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(DLQ_ROUTING_KEY);
    }

    @Bean
    public Queue operLogQueue() {
        return QueueBuilder.durable(OPER_LOG_QUEUE).build();
    }

    @Bean
    public DirectExchange operLogExchange() {
        return new DirectExchange(OPER_LOG_EXCHANGE);
    }

    @Bean
    public Binding operLogBinding(Queue operLogQueue, DirectExchange operLogExchange) {
        return BindingBuilder.bind(operLogQueue).to(operLogExchange).with(OPER_LOG_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    /**
     * Day40：手动 ACK；失败时在 Consumer 内重试，耗尽后 basicNack(requeue=false) 进死信队列。
     */
    @Bean
    public SimpleRabbitListenerContainerFactory retryContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
