package com.jwell56.security.cloud.common.cache;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 数据缓存池
 *
 * @author wsg
 * @since 2019/5/10
 */
public class CommonCachePool {
    static final long LIVE_TIME = 60;//缓存存活时间

    private static ConcurrentHashMap<String, CommonDataCache> map;

    public CommonCachePool() {
        if (map == null) {
            map = new ConcurrentHashMap<>();
        }
    }

    /**
     * 添加缓存数据
     */
    public void add(CommonDataCache commonDataCache) {
        if (map == null) {
            map = new ConcurrentHashMap<>();
        }
        map.put(commonDataCache.getKey(), commonDataCache);
    }

    /**
     * 根据key获取缓存数据，无缓存数据时返回null
     */
    public CommonDataCache get(String key) {
        if (map != null && !map.isEmpty()) {
            return map.get(key);
        } else {
            return null;
        }
    }

    /**
     * 根据key获取缓存数据，无缓存数据时返回null
     */
    public static Object getData(String key) {
        Object object = null;
        try {
            if (map != null && !map.isEmpty()) {
                CommonDataCache commonDataCache = map.get(key);
                if (commonDataCache != null && commonDataCache.paramCheck(key)) {
                    object = commonDataCache.getData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 添加缓存数据
     */
    public static void setData(String key, Object data) {
        if (map == null) {
            map = new ConcurrentHashMap<>();
        }
        map.put(key, new CommonDataCache(key, data));
    }

    /**
     * 添加缓存数据
     */
    public static void setData(String key, Object data, LocalDateTime outTime) {
        if (map == null) {
            map = new ConcurrentHashMap<>();
        }
        map.put(key, new CommonDataCache(key, data, outTime));
    }

    public void deleteData(String key) {
        map.remove(key);
    }

    public static void deleteAll() {
        map = new ConcurrentHashMap<>();
    }
}
