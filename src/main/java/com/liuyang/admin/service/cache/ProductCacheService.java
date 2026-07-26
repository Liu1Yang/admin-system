package com.liuyang.admin.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuyang.admin.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class ProductCacheService {

    private static final Logger log = LoggerFactory.getLogger(ProductCacheService.class);
    private static final String KEY_PREFIX = "product:info:";
    private static final long TTL_HOURS = 1;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ProductCacheService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public Product getById(Long id) {
        String key = buildKey(id);
        String json = redisTemplate.opsForValue().get(key); // 从redis中取出序列化好的JSON字符串
        if (!StringUtils.hasText(json)) {
            log.debug("Redis 缓存未命中: {}", key);
            return null;
        }
        try {
            log.debug("Redis 缓存命中: {}", key);
            return objectMapper.readValue(json, Product.class); // 把JSON字符串反序列化为java对象，返回给上层
        } catch (JsonProcessingException e) {
            log.warn("Redis 缓存反序列化失败，删除脏数据: {}", key);
            redisTemplate.delete(key);
            return null;
        }
    }

    public void set(Product product) {
        if (product == null || product.getId() == null) {
            return;
        }
        try {
            String key = buildKey(product.getId());
            String json = objectMapper.writeValueAsString(product); // Java对象转换为JSON字符串
            redisTemplate.opsForValue().set(key, json, TTL_HOURS, TimeUnit.HOURS);
            log.debug("写入 Redis 缓存: {}", key);
        } catch (JsonProcessingException e) {
            log.warn("Redis 缓存序列化失败, productId={}", product.getId());
        }
    }

    public void delete(Long id) {
        if (id == null) {
            return;
        }
        String key = buildKey(id);
        redisTemplate.delete(key);
        log.debug("删除 Redis 缓存: {}", key);
    }

    private String buildKey(Long id) {
        return KEY_PREFIX + id;
    }
}
