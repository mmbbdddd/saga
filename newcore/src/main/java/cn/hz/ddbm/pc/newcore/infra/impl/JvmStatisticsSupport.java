package cn.hz.ddbm.pc.newcore.infra.impl;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.infra.StatisticsSupport;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.io.Serializable;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public class JvmStatisticsSupport implements StatisticsSupport {


    Cache<String, AtomicLong> cache;

    public JvmStatisticsSupport() {
        this.cache = Caffeine.newBuilder()
                .initialCapacity(1000)
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofHours(2))
                .build();
    }


    @Override
    public Coast.StatisticsType code() {
        return Coast.StatisticsType.jvm;
    }

    @Override
    public void increment(String flowName, Serializable flowId, State node, String variable) {
        String realKey = String.format("%s:%s:%s:%s", flowName, flowId, node.code(), variable);
        cache.get(realKey, s -> new AtomicLong(0)).incrementAndGet();
    }

    @Override
    public Long get(String flowName, Serializable flowId, State node, String variable) {
        String realKey = String.format("%s:%s:%s:%s", flowName, flowId, node.code(), variable);
        return cache.get(realKey, s -> new AtomicLong(0)).get();
    }
}
