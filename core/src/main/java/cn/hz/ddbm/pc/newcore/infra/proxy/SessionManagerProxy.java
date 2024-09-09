package cn.hz.ddbm.pc.newcore.infra.proxy;

import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.SessionException;
import cn.hz.ddbm.pc.newcore.infra.SessionManager;

import java.io.Serializable;
import java.util.Map;

public class SessionManagerProxy implements SessionManager {
    SessionManager sessionManager;

    public SessionManagerProxy(SessionManager t) {
        this.sessionManager = t;
    }

    @Override
    public Coast.SessionType code() {
        return sessionManager.code();
    }

    @Override
    public void set(String flowName, Serializable flowId, Map<String, Object> session) throws SessionException {
        try {
//            SpringUtil.getBean(ChaosHandler.class).session();
            sessionManager.set(flowName, flowId, session);
        } catch (SessionException e) {
            throw e;
        } catch (Exception e) {
            throw new SessionException(e);
        }
    }

    @Override
    public Map<String, Object> get(String flowName, Serializable flowId) throws SessionException {
        try {
//            SpringUtil.getBean(ChaosHandler.class).session();
            return sessionManager.get(flowName, flowId);
        } catch (SessionException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SessionException(e);
        }
    }
}
