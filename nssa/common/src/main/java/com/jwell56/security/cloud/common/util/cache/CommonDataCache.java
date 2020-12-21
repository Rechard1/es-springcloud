package com.jwell56.security.cloud.common.util.cache;

import java.time.LocalDateTime;


/**
 * 数据缓存类
 * @author wsg
 * @since 2019/5/10
 */
public class CommonDataCache {
    private String key;

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    private LocalDateTime createTime;

    //TODO 之后要改成redis（redis对LocalDateTime的支持不好，目前仅在无LocalDateTime结构的数据使用redis
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public CommonDataCache(String key, Object data) {
        this.key = key;
        this.data = data;
        this.createTime = LocalDateTime.now();
    }

    boolean paramCheck(String key) {
        if (this.createTime.plusSeconds(CommonCachePool.LIVE_TIME).isBefore(LocalDateTime.now())) {//超时
            return false;
        }
        return this.key.equals(key);
    }
}
