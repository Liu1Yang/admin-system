package com.liuyang.admin.mq;

import com.liuyang.admin.config.RabbitMqConfig;
import com.liuyang.admin.entity.Product;
import com.liuyang.admin.mapper.ProductMapper;
import com.liuyang.admin.service.cache.ProductCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProductCacheConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductCacheConsumer.class);

    private final ProductMapper productMapper;
    private final ProductCacheService productCacheService;

    public ProductCacheConsumer(ProductMapper productMapper, ProductCacheService productCacheService) {
        this.productMapper = productMapper;
        this.productCacheService = productCacheService;
    }

    @RabbitListener(queues = RabbitMqConfig.PRODUCT_CACHE_QUEUE)
    public void onProductCache(ProductCacheMessage message) {
        if (message.getProductId() == null) {
            log.warn("[MQ 商品缓存] 忽略无效消息: productId 为空");
            return;
        }

        if (ProductCacheMessage.ACTION_DELETE.equals(message.getAction())) {
            productCacheService.delete(message.getProductId());
            log.info("[MQ 商品缓存] DELETE productId={}", message.getProductId());
            return;
        }

        Product product = productMapper.selectById(message.getProductId());
        if (product != null) {
            productCacheService.set(product);  // 消费者异步更新 Redis
            log.info("[MQ 商品缓存] REFRESH productId={} name={}", product.getId(), product.getName());
        } else {
            productCacheService.delete(message.getProductId());
            log.info("[MQ 商品缓存] REFRESH 商品已不存在，删除缓存 productId={}", message.getProductId());
        }
    }
}
