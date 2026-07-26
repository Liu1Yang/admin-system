package com.liuyang.admin.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuyang.admin.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class UserCacheService {

    private static final Logger log = LoggerFactory.getLogger(UserCacheService.class); // 控制台打印
    private static final String KEY_PREFIX = "user:info:";
    private static final long TTL_HOURS = 1;  //  1 小时后 Redis 自动删除这个 Key

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public UserCacheService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper; // 将Java对象转化为JSON字符串
    }

    public User getById(Long id) {
        String key = buildKey(id);
        String json = redisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(json)) {
            log.debug("Redis 缓存未命中: {}", key);
            return null;
        }
        try {
            log.debug("Redis 缓存命中: {}", key);
            return objectMapper.readValue(json, User.class);
        } catch (JsonProcessingException e) {
            log.warn("Redis 缓存反序列化失败，删除脏数据: {}", key);
            redisTemplate.delete(key);
            return null;
        }
    }

    public void set(User user) {
        if (user == null || user.getId() == null) {
            return;
        }
        try {
            String key = buildKey(user.getId());
            String json = objectMapper.writeValueAsString(user); // 它会读取该对象的所有字段（getter 方法），把它转成一个 JSON 字符串。
            redisTemplate.opsForValue().set(key, json, TTL_HOURS, TimeUnit.HOURS);
            log.debug("写入 Redis 缓存: {}", key);
        } catch (JsonProcessingException e) {
            log.warn("Redis 缓存序列化失败, userId={}", user.getId());
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
    }  // key设计：业务:类型:id
    // 示例 Key： user:info:1 → 存在 Redis 里的 value 是 User 的 JSON 字符串。
}
