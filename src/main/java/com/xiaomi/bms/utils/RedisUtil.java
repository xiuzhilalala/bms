package com.xiaomi.bms.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * @author PC
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }





    // Set String value
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void setWithExpiration(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }


    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }


    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }
}
