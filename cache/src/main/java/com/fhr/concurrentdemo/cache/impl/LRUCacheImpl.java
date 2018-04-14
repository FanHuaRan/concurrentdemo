package com.fhr.concurrentdemo.cache.impl;

import com.fhr.concurrentdemo.cache.MemoryCache;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author FanHuaran
 * @description 依靠LinkedHashMap+读写锁实现LRU缓存
 * @create 2018-04-13 17:08
 **/
public class LRUCacheImpl implements MemoryCache {
    public static final int DEFAULT_CAPACITY = 10000;
    // 默认过期时间,负数也代表不过期
    public static final int DEFAULT_EXPIRED = -1;

    // 默认时间单位
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.MILLISECONDS;

    // 默认空闲失效时间，负数代表不失效， 空闲失效时间<DEFAULT_EXPIRED才有意义
    public static final int DEFAULT_IDLE = -1;

    protected final ReentrantLock lock = new ReentrantLock();

    protected final int capacity;

    protected final Map<String, CacheData> dataMap;

    public LRUCacheImpl(int capacity) {
        this.capacity = capacity;
        this.dataMap = new LRUCacheMap(capacity);
    }

    /**
     * 数据缓存map
     */
    protected static class LRUCacheMap extends LinkedHashMap<String, CacheData> {
        /**
         * 0.5的装载因子效果更好
         */
        protected static final float LOAD_FACTOR = 0.5F;

        protected final int cacheCapacity;

        /**
         * 判断是否移除最老的元素，最先put
         *
         * @param eldest
         * @return
         */
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, CacheData> eldest) {
            return size() > cacheCapacity;
        }

        public LRUCacheMap(int cacheCapacity) {
            // map的容量应该要为缓存容量/装载因子，防止扩容
            // LinkedHashMap默认是维护链表节点的插入顺序，每插入一个元素就在链表头进行插入
            // 为了维护LRU，当使用（读取）时需要更新节点的顺序，所以设置accessorder为true
            // 从而访问节点时也会把节点插入头部
            super((int) (cacheCapacity / LOAD_FACTOR) + 1, LOAD_FACTOR, true);
            this.cacheCapacity = cacheCapacity;
        }
    }

    /**
     * 缓存对象，带数据和相关时间信息
     */
    protected static class CacheData implements Serializable {
        private static final long serialVersionUID = 4984609820856644770L;

        private final long insertTime;

        private final long expired;

        private final long idle;

        private final Object value;

        private final boolean willExpired;

        private final boolean willIdle;

        private long accessTime;

        public CacheData(long insertTime, long expired, long idle, Object value, boolean willExpired, boolean willIdle) {
            this.insertTime = insertTime;
            this.expired = expired;
            this.idle = idle;
            this.value = value;
            this.willExpired = willExpired;
            this.willIdle = willIdle;
            // accessTime初始化为插入时间
            this.accessTime = insertTime;
        }

        @Override
        public String toString() {
            return "CacheData{" +
                    "insertTime=" + insertTime +
                    ", expired=" + expired +
                    ", idle=" + idle +
                    ", value=" + value +
                    ", accessTime=" + accessTime +
                    '}';
        }
    }


    /* ---------------- Public operations -------------- */

    @Override
    public Object get(String key) {
        lock.lock();
        try {
            CacheData data = getData(key);
            return data == null ? null : data.value;
        } finally {
            lock.lock();
        }
    }

    @Override
    public void set(String key, Object value, int expired, int idle, TimeUnit unit) {
        checkExpired(expired);
        checkIdle(idle);
        checkUnit(unit);
        lock.lock();
        try {
            setData(key, value, expired, idle, unit);
        } finally {
            lock.lock();
        }
    }

    @Override
    public void set(String key, Object value, int expired, TimeUnit unit) {
        checkExpired(expired);
        checkUnit(unit);
        lock.lock();
        try {
            setData(key, value, expired, DEFAULT_IDLE, unit);
        } finally {
            lock.lock();
        }
    }

    @Override
    public void setWithIdle(String key, Object value, int idle, TimeUnit unit) {
        checkExpired(idle);
        checkUnit(unit);
        lock.lock();
        try {
            setData(key, value, DEFAULT_EXPIRED, DEFAULT_IDLE, unit);
        } finally {
            lock.lock();
        }
    }

    @Override
    public void set(String key, Object value) {
        lock.lock();
        try {
            setData(key, value, DEFAULT_EXPIRED, DEFAULT_IDLE, DEFAULT_UNIT);
        } finally {
            lock.lock();
        }
    }

    @Override
    public Object putIfAbsent(String key, Object value, int expired, int idle, TimeUnit unit) {
        checkExpired(expired);
        checkIdle(idle);
        checkUnit(unit);
        lock.lock();
        try {
            CacheData data = putIfAbsentData(key, value, expired, idle, unit);
            return data == null ? null : data.value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object putIfAbsent(String key, Object value, int expired, TimeUnit unit) {
        checkExpired(expired);
        checkUnit(unit);
        lock.lock();
        try {
            CacheData data = putIfAbsentData(key, value, expired, DEFAULT_IDLE, unit);
            return data == null ? null : data.value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object putIfAbsentWithIdle(String key, Object value, int idle, TimeUnit unit) {
        checkExpired(idle);
        checkUnit(unit);
        lock.lock();
        try {
            CacheData data = putIfAbsentData(key, value, DEFAULT_EXPIRED, idle, unit);
            return data == null ? null : data.value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        lock.lock();
        try {
            CacheData data = putIfAbsentData(key, value, DEFAULT_EXPIRED, DEFAULT_IDLE, DEFAULT_UNIT);
            return data == null ? null : data.value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(String key) {
        lock.lock();
        try {
            return dataMap.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object remove(String key) {
        lock.lock();
        try {
            CacheData data = dataMap.remove(key);
            return data == null ? null : data.value;
        } finally {
            lock.unlock();
        }
    }


    /* ---------------- private operations -------------- */
    private CacheData getData(String key) {
        CacheData cacheData = dataMap.get(key);
        if (cacheData == null) {
            return null;
        }
        if (isIdle(cacheData) || isExpired(cacheData)) {
            remove(key);
            return null;
        }
        cacheData.accessTime = System.currentTimeMillis();
        return cacheData;
    }

    private void setData(String key, Object value, int expired, int idle, TimeUnit unit) {
        dataMap.put(key, createCacheDataByNow(value, expired, idle, unit));
    }

    private CacheData putIfAbsentData(String key, Object value, int expired, int idle, TimeUnit unit) {
        CacheData cacheData = dataMap.get(key);
        if (cacheData != null) {
            setData(key, value, expired, idle, unit);
        }
        return cacheData;
    }

    private boolean isIdle(CacheData data) {
        return data.willIdle && data.idle >= (System.currentTimeMillis() - data.accessTime);
    }

    private boolean isExpired(CacheData data) {
        return data.willExpired && data.expired >= (System.currentTimeMillis() - data.insertTime);
    }

    /* ---------------- Static utilities -------------- */

    private static void checkExpired(int expired) {
        if (expired < 0) {
            throw new IllegalArgumentException("expired must more than 0");
        }
    }

    private static void checkIdle(int idle) {
        if (idle < 0) {
            throw new IllegalArgumentException("idle must more than 0");
        }
    }

    private static void checkUnit(TimeUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("unit not be null");
        }
    }

    /**
     * 依靠现在的时间参数创建缓存数据对象
     *
     * @param value
     * @param expired
     * @param idle
     * @param unit
     * @return
     */
    private static CacheData createCacheDataByNow(Object value, int expired, int idle, TimeUnit unit) {
        if (expired < 0) {
            throw new IllegalArgumentException("expired must more than 0");
        }
        if (idle < 0) {
            throw new IllegalArgumentException("idle must more than 0");
        }
        if (unit == null) {
            throw new IllegalArgumentException("unit not be null");
        }
        long insertTime = System.currentTimeMillis();
        long expiredMillis = unit.toMillis(expired);
        long idleMillis = unit.toMillis(idle);
        boolean willExpired = expiredMillis > 0;
        boolean willIdle = idleMillis > 0;
        return new CacheData(insertTime, expiredMillis, idleMillis, value, willExpired, willIdle);
    }


    /* ---------------- override common-method -------------- */
    @Override
    public String toString() {
        return "LRUCacheImpl{" +
                "capacity=" + capacity +
                ", dataMap=" + dataMap +
                '}';
    }
}
