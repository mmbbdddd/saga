package cn.hz.ddbm.pc.status.redis;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.support.StatusManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.io.Serializable;

public class RedisStatusManager implements StatusManager {
    @Autowired
    RedisTemplate<String, Triple<FlowStatus, ?, String>> redisTemplate;
    String keyTemplate = "%s:%s";

    @Override
    public Type code() {
        return Type.redis;
    }

    @Override
    public void setStatus(String flow, Serializable flowId, Triple<FlowStatus, ?, String> triple, Integer timeout, FsmContext  ctx) throws IOException {
        redisTemplate.opsForValue().set(String.format(keyTemplate, flow, flowId), triple, timeout);
    }

    @Override
    public Triple<FlowStatus, ?, String>   getStatus(String flow, Serializable flowId) throws IOException {
        return redisTemplate.opsForValue().get(String.format(keyTemplate, flow, flowId));
    }


}
