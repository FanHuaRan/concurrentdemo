package com.fhr.concurrentdemo.cache;

import java.util.concurrent.TimeUnit;

/**
 * @author FanHuaran
 * @description 内存缓存接口
 * @create 2018-04-13 17:01
 **/
public interface MemoryCache {
    /**
     * 获取缓存数据
     *
     * @param key
     * @return
     */
    Object get(String key);

    /**
     * 设置缓存数据
     *
     * @param key
     * @param value
     * @param expired
     * @param idle
     * @param unit
     */
    void set(String key, Object value, int expired, int idle, TimeUnit unit);

    /**
     * 设置缓存数据
     *
     * @param key
     * @param value
     * @param expired
     * @param unit
     */
    void set(String key, Object value, int expired, TimeUnit unit);

    /**
     * 设置缓存时间，带空闲失效，但不带过期
     * @param key
     * @param value
     * @param idle
     * @param unit
     */
    void setWithIdle(String key, Object value, int idle, TimeUnit unit);

    /**
     * 设置缓存数据
     * @param key
     * @param value
     */
    void set(String key, Object value);

    /**
     * 若无则添加缓存数据
     *
     * @param key
     * @param value
     * @param expired
     * @param idle
     * @param unit
     * @return
     */
    Object putIfAbsent(String key, Object value, int expired, int idle, TimeUnit unit);

    /**
     * 若无则添加缓存数据
     *
     * @param key
     * @param value
     * @param expired
     * @param unit
     * @return
     */
    Object putIfAbsent(String key, Object value, int expired, TimeUnit unit);

    /**
     * 若无则添加缓存数据,带空闲失效，但不带过期
     * @param key
     * @param value
     * @return
     */
    Object putIfAbsentWithIdle(String key, Object value,int idle,TimeUnit unit);

    /**
     * 若无则添加缓存数据
     * @param key
     * @param value
     * @return
     */
    Object putIfAbsent(String key, Object value);

    /**
     * 判断是否包含缓存数据
     *
     * @param key
     * @return
     */
    boolean contains(String key);

    /**
     * 移除缓存数据
     *
     * @param key
     * @return
     */
    Object remove(String key);
}
