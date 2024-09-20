package cn.hz.ddbm.pc.newcore.infra.proxy;

import cn.hz.ddbm.pc.newcore.State;
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
    public <T> void setStatus(String flow, Serializable flowId, T status, Integer timeout) throws StatusException {
        try {
            statusManager.setStatus(flow, flowId, status, timeout);
        } catch (Exception e) {
            throw new StatusException(e);
        }

    }

    @Override
    public <T> T getStatus(String flow, Serializable flowId, Class<T> type) throws StatusException {
        try {
            return statusManager.getStatus(flow, flowId,type);
        } catch (Exception e) {
            throw new StatusException(e);
        }
    }



    @Override
    public void idempotent(String key) throws IdempotentException {
        try {
//            SpringUtil.getBean(ChaosHandler.class).status();
            statusManager.idempotent(key);
        } catch (IdempotentException e) {
            throw e;
        } catch (Exception e) {
            throw new IdempotentException(e);
        }
    }

    @Override
    public void unidempotent(String key) {
        try {
//            SpringUtil.getBean(ChaosHandler.class).status();
            statusManager.unidempotent(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
