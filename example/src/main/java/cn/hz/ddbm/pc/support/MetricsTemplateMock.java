package cn.hz.ddbm.pc.support;

import cn.hz.ddbm.pc.core.support.StatisticsSupport;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsTemplateMock implements StatisticsSupport {
    Map<String, AtomicLong> map = new HashMap<>();

    @Override
    public void increment(String flowName, Serializable flowId, Enum node, String key) {
        String realKey = String.format("%s:%s:%s:%s", flowName, flowId, node.name(), key);
        map.computeIfAbsent(realKey, s -> new AtomicLong(0)).incrementAndGet();
    }

    @Override
    public Long get(String flowName, Serializable flowId, Enum node, String key) {
        String realKey = String.format("%s:%s:%s:%s", flowName, flowId, node.name(), key);
        return map.computeIfAbsent(realKey, s -> new AtomicLong(0)).get();
    }
}
