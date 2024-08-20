package cn.hz.ddbm.pc.core.support;


import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.exception.SessionException;

/**
 * @Description TODO
 * @Author wanglin
 * @Date 2024/8/7 21:29
 * @Version 1.0.0
 **/


public interface SessionManager {
    Type code();

    void set(String flowName, String flowId, String key, Object value);

    Object get(String flowName, String flowId, String key);

    default void flush(FsmContext<?, ?> ctx) throws SessionException {
        //todo
    }

    enum Type {
        memory, redis
    }
}
