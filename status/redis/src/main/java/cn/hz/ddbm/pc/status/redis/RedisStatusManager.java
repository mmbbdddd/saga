package cn.hz.ddbm.pc.status.redis;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;
import cn.hz.ddbm.pc.newcore.infra.StatusManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.io.Serializable;

public class RedisStatusManager implements StatusManager {
    @Autowired
    RedisTemplate<String, Pair<FlowStatus, ?>> redisTemplate;
    String keyTemplate = "%s:%s";

    @Override
    public Coast.StatusType code() {
        return null;
    }

    @Override
    public void setStatus(String s, Serializable serializable, Pair<FlowStatus, ?> pair, Integer integer) throws StatusException {

    }

    @Override
    public Pair<FlowStatus, ?> getStatus(String s, Serializable serializable) throws StatusException {
        return null;
    }

    @Override
    public void idempotent(String s) throws IdempotentException {

    }

    @Override
    public void unidempotent(String s) throws IdempotentException {

    }

//    @Override
//    public Type code() {
//        return Type.redis;
//    }
//
//    @Override
//    public void setStatus(String flow, Serializable flowId, Triple<FlowStatus, ?, String> triple, Integer timeout, FsmContext  ctx) throws IOException {
//        redisTemplate.opsForValue().set(String.format(keyTemplate, flow, flowId), triple, timeout);
//    }
//
//    @Override
//    public Triple<FlowStatus, ?, String>   getStatus(String flow, Serializable flowId) throws IOException {
//        return redisTemplate.opsForValue().get(String.format(keyTemplate, flow, flowId));
//    }


}
