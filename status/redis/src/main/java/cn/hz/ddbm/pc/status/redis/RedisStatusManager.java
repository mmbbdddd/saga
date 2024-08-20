package cn.hz.ddbm.pc.status.redis;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.State;
import cn.hz.ddbm.pc.core.support.StatusManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.io.Serializable;

public class RedisStatusManager implements StatusManager {
    @Autowired
    RedisTemplate<String, State> redisTemplate;
    String keyTemplate = "%s:%s";

    @Override
    public Type code() {
        return Type.redis;
    }

    @Override
    public void setStatus(String flow, Serializable flowId, State<?> flowStatus, Integer timeout, FsmContext<?, ?> ctx) throws IOException {
        redisTemplate.opsForValue().set(String.format(keyTemplate, flow, flowId), flowStatus, timeout);
    }


    @Override
    public State<?> getStatus(String flow, Serializable flowId) throws IOException {
        return redisTemplate.opsForValue().get(String.format(keyTemplate, flow, flowId));
    }

}
