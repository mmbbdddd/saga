package cn.hz.ddbm.pc.newcore.infra.impl;

import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;
import cn.hz.ddbm.pc.newcore.infra.StatusManager;
import cn.hz.ddbm.pc.newcore.log.Logs;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JvmStatusManager implements StatusManager {
    ConcurrentMap<String, State>   statusMap;
    ConcurrentMap<String, Boolean> idempotentRecords;
    String                         keyTemplate = "%s:%s";

    public JvmStatusManager() {
        this.statusMap         = new ConcurrentHashMap<>();
        this.idempotentRecords = new ConcurrentHashMap<>();
    }

    @Override
    public Coast.StatusType code() {
        return Coast.StatusType.jvm;
    }

    @Override
    public void setStatus(String flow, Serializable flowId, State status, Integer timeout) throws StatusException {
        try {
            Logs.status.debug("状态变迁到{}", status);
            statusMap.put(String.format(keyTemplate, flow, flowId), status);
        } catch (Exception e) {
            throw new StatusException(e);
        }
    }


    @Override
    public State getStatus(String flow, Serializable flowId) throws StatusException {
        try {
            return statusMap.get(String.format(keyTemplate, flow, flowId));
        } catch (Exception e) {
            throw new StatusException(e);
        }
    }

    @Override
    public void idempotent(String key) throws IdempotentException {
        if (idempotentRecords.containsKey(key)) {
            throw new IdempotentException(String.format("交易幂等:" + key));
        } else {
            idempotentRecords.put(key, true);
        }
    }

    @Override
    public void unidempotent(String key)   {
        idempotentRecords.remove(key);
    }
}
