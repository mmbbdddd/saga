package cn.hz.ddbm.pc.newcore.infra.impl;

import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.infra.StatisticsSupport;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class JvmStatisticsSupport implements StatisticsSupport {


    ConcurrentMap<String, AtomicLong> cache;

    public JvmStatisticsSupport() {
        cache = new ConcurrentHashMap<>();
    }


    @Override
    public Coast.StatisticsType code() {
        return Coast.StatisticsType.jvm;
    }

    @Override
    public void increment(String flowName, Serializable flowId, State node, String variable) {
        String realKey = String.format("%s:%s:%s:%s", flowName, flowId, node.code(), variable);
        cache.computeIfAbsent(realKey, s -> new AtomicLong(0)).incrementAndGet();
    }

    @Override
    public Long get(String flowName, Serializable flowId, State node, String variable) {
        String realKey = String.format("%s:%s:%s:%s", flowName, flowId, node.code(), variable);
        return cache.computeIfAbsent(realKey, s -> new AtomicLong(0)).get();
    }
}
