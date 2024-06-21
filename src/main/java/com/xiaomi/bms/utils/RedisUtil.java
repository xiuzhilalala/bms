package com.xiaomi.bms.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取指定 key 的字符串值。
     * @param key Redis key。
     * @return String Redis key 对应的字符串值。
     */
    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 设置指定 key 的字符串值。
     * @param key Redis key。
     * @param value Redis key 对应的值。
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置带过期时间的 key-value 对。
     * @param key Redis key。
     * @param value Redis key 对应的值。
     * @param timeout 过期时间。
     * @param unit 过期时间单位。
     */
    public void setWithExpiration(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 删除指定 key 。
     * @param key Redis key。
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 自增指定 key 的值。
     * @param key Redis key。
     * @param delta 自增的步长。
     * @return Long 自增后的值。
     */
    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 向指定 key 的哈希表中存储字段和值。
     * @param key Redis key。
     * @param hashKey 哈希表中的字段。
     * @param value 哈希表中的值。
     */
    public void hashPut(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 删除指定 key 的哈希表中的字段。
     * @param key Redis key。
     * @param hashKey 哈希表中的字段。
     */
    public void hashDelete(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 获取指定 key 的哈希表中的字段值。
     * @param key Redis key。
     * @param hashKey 哈希表中的字段。
     * @return Object 哈希表中的字段值。
     */
    public Object hashGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 检查指定 key 是否存在。
     * @param key Redis key。
     * @return boolean 如果存在返回 true，否则返回 false。
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
