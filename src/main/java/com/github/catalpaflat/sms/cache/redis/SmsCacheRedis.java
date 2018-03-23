package com.github.catalpaflat.sms.cache.redis;

import com.github.catalpaflat.sms.cache.SmsCache;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author CatalpaFlat
 */
public class SmsCacheRedis implements SmsCache {

    protected RedisTemplate<String, Object> redisTemplate;

    public SmsCacheRedis(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void set(String key, Object value, int time) {
        redisTemplate.opsForValue().set(key, value, time);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void del(String key) {
        redisTemplate.delete(key);
    }
}
