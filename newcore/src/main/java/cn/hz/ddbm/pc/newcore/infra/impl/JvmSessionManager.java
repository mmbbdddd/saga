package cn.hz.ddbm.pc.newcore.infra.impl;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.SessionException;
import cn.hz.ddbm.pc.newcore.infra.SessionManager;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.io.Serializable;
import java.time.Duration;
import java.util.Map;

public class JvmSessionManager implements SessionManager {

    String keyTemplate = "%s:%s";

    Cache<String, Map<String, Object>> cache;

    public JvmSessionManager() {
        this.cache     = Caffeine.newBuilder()
                .initialCapacity(1000)
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofHours(2))
                .build();
    }


    @Override
    public Coast.SessionType code() {
        return Coast.SessionType.jvm;
    }

    @Override
    public void set(String flowName, Serializable id, Map<String, Object> session) throws SessionException {
        try {
            cache.put(String.format(keyTemplate, flowName, id), session);
        }catch (Exception e){
            throw new SessionException(e);
        }
    }

    @Override
    public Map<String, Object> get(String flowName, Serializable flowId) throws SessionException {
        try {
            return cache.getIfPresent(String.format(keyTemplate, flowName, flowId));
        }catch (Exception e){
            throw new SessionException(e);
        }
    }

}
