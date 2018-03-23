package com.github.catalpaflat.sms.cache.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author CatalpaFlat
 */
public class Group {
    /**
     * 缓存队列
     */
    private final ArrayBlockingQueue<CacheEntity> queue;

    private Integer capacity;

    public Group(int capacity) {
        queue = new ArrayBlockingQueue<CacheEntity>(capacity);
        this.capacity = capacity;
    }

    /**
     * 压入队列
     *
     * @param key    key
     * @param object 值
     * @param second 有效期
     */
    public void push(String key, Object object, int second) {

        // 放入队列，
        queue.offer(new CacheEntity(key, object, System.currentTimeMillis(), second, this));
    }

    /**
     * 压入队列
     *
     * @param key    key
     * @param object 值
     */
    public void push(String key, Object object) {

        push(key, object, 0);
    }

    /**
     * 返回并移除头部出
     *
     * @return 缓存对象
     */
    public Object poll() {

        CacheEntity entity = queue.poll();
        // 如果有效期超过，返回null
        if (!entity.isExpire()) {
            return null;
        }
        return entity.getValue();
    }

    /**
     * 返回头部元素并放到末尾
     *
     * @return 缓存对象
     */
    public Object rPoll() {

        CacheEntity entity = queue.poll();
        // 如果有效期超过，返回null
        if (!entity.isExpire()) {
            return null;
        }
        Object object = entity.getValue();
        queue.offer(entity);
        return object;
    }

    /**
     * 通过key寻找有效的缓存实体
     *
     * @param key key
     * @return 缓存对象
     */
    private CacheEntity find(String key) {

        synchronized (queue) {
            for (CacheEntity entity : queue) {
                if (key.equals(entity.getKey())) {
                    return entity;
                }
            }
            return null;
        }
    }

    /**
     * 删除key
     *
     * @param key key
     */
    public void delete(String key) {

        synchronized (queue) {
            CacheEntity entity = find(key);
            if (entity != null) {
                queue.remove(entity);
            }
        }
    }

    /**
     * 根据key获取
     *
     * @param key key
     * @return 缓存对象
     */
    public Object getValue(String key) {

        CacheEntity entity = find(key);
        if (entity != null && entity.isExpire()) {
            return entity.getValue();
        }

        return null;
    }

    /**
     * 获取有效的缓存实体
     *
     * @return 缓存对象列表
     */
    private List<CacheEntity> getCacheEntitys() {

        List<CacheEntity> keys = new ArrayList<CacheEntity>();
        for (CacheEntity cacheEntity : queue) {
            if (cacheEntity.isExpire()) {
                keys.add(cacheEntity);
            }
        }
        return keys;
    }

    /**
     * 获取key列表
     *
     * @return key列表
     */
    public List<String> getKeys() {

        List<String> keys = new ArrayList<String>();
        List<CacheEntity> caches = getCacheEntitys();
        for (CacheEntity cacheEntity : caches) {
            keys.add(cacheEntity.getKey());
        }
        return keys;
    }

    /**
     * 获取值列表
     *
     * @return value列表
     */
    public List<Object> getValues() {

        List<Object> values = new ArrayList<Object>();
        List<CacheEntity> caches = getCacheEntitys();
        for (CacheEntity cacheEntity : caches) {
            values.add(cacheEntity.getValue());
        }
        return values;
    }

    /**
     * 查看元素存活时间，-1 失效，0 长期有效
     *
     * @param key key
     * @return 存活时间
     */
    public int ttl(String key) {

        CacheEntity entity = find(key);
        if (entity != null) {
            return entity.ttl();
        }
        return -1;
    }

    /**
     * 返回头部的元素
     *
     * @return 缓存对象
     */
    public Object peek() {

        CacheEntity entity = queue.peek();
        if (entity != null) {
            return entity.getValue();
        }
        return null;
    }

    /**
     * 设置元素存活时间
     *
     * @param key    key
     * @param second 存活时间（秒）
     */
    public void expire(String key, int second) {

        CacheEntity entity = find(key);
        if (entity != null) {
            entity.setTimestamp(System.currentTimeMillis());
            entity.setExpire(second);
        }
    }

    /**
     * 查看key是否存在
     *
     * @param key key
     * @return 结果
     */
    public boolean exist(String key) {

        return find(key) != null;
    }

    /**
     * 查看组是否为空
     *
     * @return 结果
     */
    public boolean isEmpty() {

        return queue.isEmpty();
    }

    /**
     * 获取存活元素的大小
     *
     * @return 大小
     */
    public int size() {

        return getCacheEntitys().size();
    }

    /**
     * 获取容量
     *
     * @return 容量
     */
    public Integer getCapacity() {

        return capacity;
    }
}
