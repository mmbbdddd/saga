package cn.hz.ddbm.pc.statistics;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.infra.StatisticsSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;

public class RedisStatistics implements StatisticsSupport {

    Integer cacheSize;
    Integer hours;
    @Autowired
    RedisTemplate<String, Long> redisTemplate;

    public RedisStatistics(Integer cacheSize, Integer hours) {
        Assert.notNull(cacheSize, "cacheSize is null");
        Assert.notNull(hours, "hours is null");
        this.cacheSize = cacheSize;
        this.hours     = hours;
    }

    @Override
    public Coast.StatisticsType code() {
        return null;
    }

    @Override
    public void increment(String flowName, Serializable flowId, State state, String variableName) {

    }

    @Override
    public Long get(String flowName, Serializable flowId, State state, String variableName) {
        return null;
    }

//    @Override
//    public void increment(String flowName, Serializable flowId, State node, String key) {
//        String realKey = String.format("%s:%s:%s:%s", flowName, flowId, node, key);
//        redisTemplate.opsForValue().increment(realKey);
//    }
//
//    @Override
//    public Long get(String flowName, Serializable flowId, State node, String key) {
//        String realKey = String.format("%s:%s:%s:%s", flowName, flowId, node, key);
//        return redisTemplate.opsForValue().get(realKey);
//    }
}
