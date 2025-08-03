package com.dfc.ind.uitls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * @author zhouzhenhui
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate strRedisTemplate;

    public static RedisTemplate<String, String> stringRedisTemplate;

    public static RedisTemplate<String, Object> objectRedisTemplate;

    @PostConstruct
    public void init() {
        RedisUtil.stringRedisTemplate = strRedisTemplate;
        RedisUtil.objectRedisTemplate = redisTemplate;
    }

    /**
     * redis计数器
     * @param key
     * @param expireTime
     * @param unit
     * @return
     */
    public static RedisAtomicInteger getRedisCounter(String key, long expireTime, TimeUnit unit) {
        RedisAtomicInteger counter = new RedisAtomicInteger(key, stringRedisTemplate.getConnectionFactory());
        if (counter.get() == 0) {
            counter.expire(expireTime, unit);
        }
        return counter;
    }
    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value)
    {
        redisTemplate.opsForValue().set(key, value);
    }
}
