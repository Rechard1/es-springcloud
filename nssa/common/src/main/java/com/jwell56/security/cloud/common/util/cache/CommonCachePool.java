package com.jwell56.security.cloud.common.util.cache;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据缓存池
 *
 * @author wsg
 * @since 2019/5/10
 */
public class CommonCachePool {
    static final long LIVE_TIME = 7200;//缓存存活时间

    private static List<CommonDataCache> commonDataCacheList;

    public CommonCachePool() {
        if (commonDataCacheList == null) {
            commonDataCacheList = new ArrayList<>();
        }
    }

    /**
     * 添加缓存数据
     */
    public void add(CommonDataCache commonDataCache) {
        if (commonDataCacheList == null) {
            commonDataCacheList = new ArrayList<>();
        }
        commonDataCacheList.add(commonDataCache);
    }

    /**
     * 根据key获取缓存数据，无缓存数据时返回null
     */
    public CommonDataCache get(String key) {
        if (commonDataCacheList != null && !commonDataCacheList.isEmpty()) {
            for (CommonDataCache commonDataCache : commonDataCacheList) {
                if (commonDataCache != null && commonDataCache.paramCheck(key)) {
                    return commonDataCache;
                }
            }
        }
        return null;
    }

    /**
     * 删除缓存数据，缓存数据默认保存周期为1小时
     */
    public void deleteOldData() {
        List<CommonDataCache> newList = new ArrayList<>();
        if (commonDataCacheList != null && !commonDataCacheList.isEmpty()) {
            for (CommonDataCache commonDataCache : commonDataCacheList) {
                if (commonDataCache.getCreateTime().plusSeconds(LIVE_TIME).isAfter(LocalDateTime.now())) {
                    newList.add(commonDataCache);
                }
            }
        }
        commonDataCacheList = newList;
    }

    public void deleteData(String key) {
        List<CommonDataCache> newList = new ArrayList<>();
        if (commonDataCacheList != null && !commonDataCacheList.isEmpty()) {
            for (CommonDataCache commonDataCache : commonDataCacheList) {
                if (!commonDataCache.paramCheck(key)) {
                    newList.add(commonDataCache);
                }
            }
        }
        commonDataCacheList = newList;
    }

    public static void deleteAll() {
        commonDataCacheList = new ArrayList<>();
    }

    /**
     * 根据key获取缓存数据，无缓存数据时返回null
     */
    public static Object getData(String key) {
        if (commonDataCacheList != null && !commonDataCacheList.isEmpty()) {
            for (CommonDataCache commonDataCache : commonDataCacheList) {
                if (commonDataCache.paramCheck(key)) {
                    return commonDataCache.getData();
                }
            }
        }
        return null;
    }

    /**
     * 添加缓存数据
     */
    public static void setData(String key, Object data) {
        if (commonDataCacheList == null) {
            commonDataCacheList = new ArrayList<>();
        }
        commonDataCacheList.add(new CommonDataCache(key, data));
    }
}
