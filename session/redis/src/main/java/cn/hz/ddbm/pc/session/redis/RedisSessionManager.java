package cn.hz.ddbm.pc.session.redis;

import cn.hz.ddbm.pc.core.support.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Map;

public class RedisSessionManager implements SessionManager {

    @Autowired
    RedisTemplate<String, Map<String, Object>> redisTemplate;
    String keyTemplate = "%s:%s";

    @Override
    public SessionManager.Type code() {
        return Type.memory;
    }

    @Override
    public void set(String flowName, Serializable id, Map<String, Object> session) {
        redisTemplate.opsForValue().set(String.format(keyTemplate, id), session);
    }

    @Override
    public Map<String, Object> get(String flowName, Serializable flowId) {
        return redisTemplate.opsForValue().get(String.format(keyTemplate, flowId));
    }
}
