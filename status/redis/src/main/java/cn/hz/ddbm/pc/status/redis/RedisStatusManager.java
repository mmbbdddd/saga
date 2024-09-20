//package cn.hz.ddbm.pc.status.redis;
//
//import cn.hutool.core.lang.Pair;
//import cn.hz.ddbm.pc.newcore.FlowStatus;
//import cn.hz.ddbm.pc.newcore.State;
//import cn.hz.ddbm.pc.newcore.config.Coast;
//import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
//import cn.hz.ddbm.pc.newcore.exception.StatusException;
//import cn.hz.ddbm.pc.newcore.infra.StatusManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.io.Serializable;
//
//public class RedisStatusManager implements StatusManager {
//    @Autowired
//    RedisTemplate<String, Object> redisTemplate;
//    String keyTemplate = "%s:%s";
//
//    @Override
//    public Coast.StatusType code() {
//        return null;
//    }
//
//    @Override
//    public void setStatus(String flow, Serializable flowId, State pair, Integer timeout) throws StatusException {
//        redisTemplate.opsForValue().set(String.format(keyTemplate, flow, flowId), pair, timeout);
//    }
//
//    @Override
//    public State getStatus(String flow, Serializable flowId) throws StatusException {
//        return   (State) redisTemplate.opsForValue().get(String.format(keyTemplate, flow, flowId));
//    }
//
//    @Override
//    public void idempotent(String key) throws IdempotentException {
//        if(redisTemplate.hasKey(key)){
//            throw new IdempotentException(key);
//        }else{
//            redisTemplate.opsForValue().set(key,Boolean.TRUE);
//        }
//    }
//
//    @Override
//    public void unidempotent(String key)   {
//        redisTemplate.delete(key);
//    }
//
//
//}
