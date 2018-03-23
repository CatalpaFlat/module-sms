package com.github.catalpaflat.sms.cache.memory;


import com.github.catalpaflat.sms.cache.SmsCache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author CatalpaFlat
 */
public class SmsCacheMemory implements SmsCache {

    private static volatile SmsCacheMemory instance = null;
    /**
     * 数据容器
     */
    private Map<String, Object> container;

    private SmsCacheMemory() {
        container = new LinkedHashMap<String, Object>();
    }

    public static SmsCacheMemory instance() {
        if (instance == null) {
            synchronized (SmsCacheMemory.class) {
                if (instance == null) {
                    instance = new SmsCacheMemory();
                }
            }
        }
        return instance;
    }

    /**
     * 获取容器
     * 如果组存在就返回，不存在就创建，保证不为null
     *
     * @param key      key
     * @param capacity 容量
     * @return 组
     */
    public Group group(String key, int capacity) {

        Group group;
        Object entry = container.get(key);
        if (entry != null) {
            group = (Group) entry;
        } else {
            group = new Group(capacity);
            container.put(key, group);
        }

        return group;
    }

    /**
     * 如果组存在就返回，不存在就创建，默认容量1000
     *
     * @param key key
     * @return 组
     */
    public Group group(String key) {
        return this.group(key, 1000);
    }

    public void set(String key, Object value, int time) {
        Group group = group(key, time);
        group.push(key, value, time);
    }

    public Object get(String key) {
        Group group = group(key);
        return group.getValue(key);
    }

    public void del(String key) {
        Group group = group(key);
        group.delete(key);
    }
}
