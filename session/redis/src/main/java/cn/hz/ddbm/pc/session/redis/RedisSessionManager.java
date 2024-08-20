package cn.hz.ddbm.pc.session.redis;

import cn.hz.ddbm.pc.core.support.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisSessionManager implements SessionManager {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    String keyTemplate = "%s:%s;%s";

    @Override
    public SessionManager.Type code() {
        return Type.memory;
    }

    @Override
    public void set(String flowName, String flowId, String key, Object value) {
        redisTemplate.opsForValue().set(String.format(keyTemplate, flowId, flowId, key), value);
    }

    @Override
    public Object get(String flowName, String flowId, String key) {
        return redisTemplate.opsForValue().get(String.format(keyTemplate, flowId, flowId, key));
    }
}
