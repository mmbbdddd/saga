package cn.hz.ddbm.pc.newcore.infra.proxy;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;
import cn.hz.ddbm.pc.newcore.infra.StatusManager;

import java.io.Serializable;

public class StatusManagerProxy implements StatusManager {
    StatusManager statusManager;

    public StatusManagerProxy(StatusManager t) {
        this.statusManager = t;
    }

    @Override
    public Coast.StatusType code() {
        return statusManager.code();
    }

    @Override
    public void setStatus(String flow, Serializable flowId, Pair<FlowStatus, ?> status, Integer timeout) throws StatusException {
        try {
            statusManager.setStatus(flow, flowId, status, timeout);
        } catch (Exception e) {
            throw new StatusException(e);
        }
    }

    @Override
    public Pair<FlowStatus, ?> getStatus(String flow, Serializable flowId) throws StatusException {
        try {
            return statusManager.getStatus(flow, flowId);
        } catch (Exception e) {
            throw new StatusException(e);
        }
    }

    @Override
    public void idempotent(String key) throws IdempotentException {
        try {
            statusManager.idempotent(key);
        } catch (Exception e) {
            throw new IdempotentException(e);
        }
    }

    @Override
    public void unidempotent(String key) throws IdempotentException {
        try {
            statusManager.unidempotent(key);
        } catch (Exception e) {
            throw new IdempotentException(e);
        }
    }
}
