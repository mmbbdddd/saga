package cn.hz.ddbm.pc.newcore.infra.impl;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;
import cn.hz.ddbm.pc.newcore.infra.StatusManager;
import cn.hz.ddbm.pc.newcore.log.Logs;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.io.Serializable;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JvmStatusManager implements StatusManager {
    ConcurrentMap<String, Pair<FlowStatus, ?>> statusMap;
    ConcurrentMap<String, Boolean>             actionTables;
    String                                     keyTemplate = "%s:%s";

    public JvmStatusManager() {
        this.statusMap = new ConcurrentHashMap<>();
        this.actionTables = new ConcurrentHashMap<>();
    }

    @Override
    public Coast.StatusType code() {
        return Coast.StatusType.jvm;
    }

    @Override
    public void setStatus(String flow, Serializable flowId, Pair<FlowStatus, ?> status, Integer timeout) throws StatusException {
        try {
            Logs.status.debug("状态变迁到{}", status.getValue());
            statusMap.put(String.format(keyTemplate, flow, flowId), status);
        } catch (Exception e) {
            throw new StatusException(e);
        }
    }


    @Override
    public Pair<FlowStatus, ?> getStatus(String flow, Serializable flowId) throws StatusException {
        try {
            return statusMap.get(String.format(keyTemplate, flow, flowId));
        } catch (Exception e) {
            throw new StatusException(e);
        }
    }

    @Override
    public void idempotent(String key) throws IdempotentException {
        try {
            if (actionTables.containsKey(key)) {
                throw new IdempotentException(String.format("交易已发生:" + key));
            }
        } catch (Exception e) {
            throw new IdempotentException(String.format("交易已发生:" + key), e);
        }
    }

    @Override
    public void unidempotent(String key) throws IdempotentException {
        actionTables.remove(key);
    }
}
