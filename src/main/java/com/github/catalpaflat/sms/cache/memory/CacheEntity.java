package com.github.catalpaflat.sms.cache.memory;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author CatalpaFlat
 */
public class CacheEntity implements Serializable {

    private static final long serialVersionUID = 2082223810638865724L;
    /**
     * key
     */
    @Getter
    @Setter
    private String key;
    /**
     * 值
     */
    @Getter
    @Setter
    private Object value;
    /**
     * 缓存的时候存的时间戳，用来计算该元素是否过期
     */
    @Getter
    @Setter
    private Long timestamp;
    /**
     * 默认长期有效
     */
    @Getter
    @Setter
    private int expire;
    /**
     * 容器
     */
    @Getter
    @Setter
    private Group group;

    public CacheEntity(String key, Object value, Long timestamp, int expire, Group group) {
        super();
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
        this.expire = expire;
        this.group = group;
    }

    /**
     * 获取剩余时间
     *
     * @return 剩余时间
     */
    public int ttl() {
        if (this.expire == 0) {
            return this.expire;
        }
        return this.expire - getTime();
    }

    /**
     * 获取当前时间和元素的相差时间
     *
     * @return 当前时间和元素的相差时间
     */
    private int getTime() {

        if (this.expire == 0) {
            return this.expire;
        }
        Long current = System.currentTimeMillis();
        Long value = current - this.timestamp;
        return (int) (value / 1000) + 1;
    }

    /**
     * 是否到期
     *
     * @return 是否到期
     */
    public boolean isExpire() {

        if (this.expire == 0) {
            return true;
        }
        if (getTime() > this.expire) {
            // 失效了就移除
            group.delete(key);
            return false;
        }
        return true;
    }
}
