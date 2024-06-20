package com.xiaomi.bms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.time.Duration;

/**
 * @author zhuzhe1018
 * @date 2024/6/16
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    @Autowired
    private LettuceConnectionFactory lettuceConnectionFactory;
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 使用String序列化键
        template.setKeySerializer(new StringRedisSerializer());
        // 使用GenericJackson2JsonRedisSerializer序列化值
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    //设置缓存过期时间

    @Bean("batteryWarnManager")
    public RedisCacheManager redisCacheManager(){
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMillis(30000))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        return RedisCacheManager.builder(lettuceConnectionFactory).cacheDefaults(configuration)
                .build();
    }

    @Override
    @Bean("batteryWarnGenerate")
    public KeyGenerator keyGenerator(){
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... objects) {
                System.out.println("target:  "+target);
                System.out.println("method:  "+method);
                System.out.println("objects:  "+objects);
                StringBuilder sb = new StringBuilder();
                Cacheable annotation = method.getAnnotation(Cacheable.class);
                String[] cacheNames = annotation.cacheNames();
                for (String elem : cacheNames) {
                    sb.append(elem + ".");
                }
                sb.deleteCharAt(sb.length() - 1); // 删除掉 cacheNames 中的最后一个点(.)

                sb.append("::").append(target.getClass()
                                .getSimpleName()).append("::")
                        .append(method.getName()).append("::")
                        .append(Arrays.toString(objects));

                return sb.toString();
            }
        };

    }

}
