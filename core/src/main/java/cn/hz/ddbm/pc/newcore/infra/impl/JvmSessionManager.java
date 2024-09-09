package cn.hz.ddbm.pc.newcore.infra.impl;

import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.SessionException;
import cn.hz.ddbm.pc.newcore.infra.SessionManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JvmSessionManager implements SessionManager {

    String keyTemplate = "%s:%s";

    ConcurrentMap<String, Map<String, Object>> cache;

    public JvmSessionManager() {
        cache = new ConcurrentHashMap<>();
    }


    @Override
    public Coast.SessionType code() {
        return Coast.SessionType.jvm;
    }

    @Override
    public void set(String flowName, Serializable id, Map<String, Object> session) throws SessionException {
        try {
            cache.put(String.format(keyTemplate, flowName, id), session);
        } catch (Exception e) {
            throw new SessionException(e);
        }
    }

    @Override
    public Map<String, Object> get(String flowName, Serializable flowId) throws SessionException {
        try {
            return cache.computeIfAbsent(String.format(keyTemplate, flowName, flowId), s -> new HashMap<>());
        } catch (Exception e) {
            throw new SessionException(e);
        }
    }

}
