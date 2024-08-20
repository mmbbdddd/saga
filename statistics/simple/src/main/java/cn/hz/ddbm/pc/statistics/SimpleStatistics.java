package cn.hz.ddbm.pc.statistics;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.core.support.StatisticsSupport;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.io.Serializable;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleStatistics implements StatisticsSupport {

    Integer cacheSize;
    Integer hours;

    Cache<String, AtomicLong> cache;

    public SimpleStatistics(Integer cacheSize, Integer hours) {
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
    public void increment(String flowName, Serializable flowId, Enum node, String key) {
        String realKey = String.format("%s:%s:%s:%s", flowName, flowId, node.name(), key);
        cache.get(realKey, s -> new AtomicLong(0)).incrementAndGet();
    }

    @Override
    public Long get(String flowName, Serializable flowId, Enum node, String key) {
        String realKey = String.format("%s:%s:%s:%s", flowName, flowId, node.name(), key);
        return cache.get(realKey, s -> new AtomicLong(0)).get();
    }
}
