package cn.hz.ddbm.pc.session.memory;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.core.support.SessionManager;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;

public class MemorySessionManager implements SessionManager {

    String keyTemplate = "%s:%s;%s";

    Integer               cacheSize;
    Integer               hours;
    Cache<String, Object> cache;

    public MemorySessionManager(Integer cacheSize, Integer hours) {
        Assert.notNull(cacheSize, "cacheSize is null");
        Assert.notNull(hours, "hours is null");
        this.cacheSize = cacheSize;
        this.hours     = hours;
        this.cache     = Caffeine.newBuilder()
                .initialCapacity(cacheSize > 256 ? cacheSize / 8 : cacheSize)
                .maximumSize(cacheSize)
                .expireAfterWrite(Duration.ofHours(hours))
                .build();
    }


    @Override
    public Type code() {
        return Type.memory;
    }

    @Override
    public void set(String flowName, String flowId, String key, Object value) {
        cache.put(String.format(keyTemplate, flowId, flowId, key), value);
    }

    @Override
    public Object get(String flowName, String flowId, String key) {
        return cache.getIfPresent(String.format(keyTemplate, flowId, flowId, key));
    }
}
