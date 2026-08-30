package com.liuyang.admin.mq;

import com.liuyang.admin.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductCacheProducer { // 商品缓存消息发送器

    private final RabbitTemplate rabbitTemplate;

    public ProductCacheProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendRefresh(Long productId) {
        ProductCacheMessage message = new ProductCacheMessage();
        message.setProductId(productId);
        message.setAction(ProductCacheMessage.ACTION_REFRESH); // 动作：刷新
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.PRODUCT_CACHE_EXCHANGE,
                RabbitMqConfig.PRODUCT_CACHE_ROUTING_KEY,
                message
        );
    }

    public void sendDelete(Long productId) {
        ProductCacheMessage message = new ProductCacheMessage();
        message.setProductId(productId);
        message.setAction(ProductCacheMessage.ACTION_DELETE);  // 动作：删除
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.PRODUCT_CACHE_EXCHANGE,
                RabbitMqConfig.PRODUCT_CACHE_ROUTING_KEY,
                message
        );
    }
}
