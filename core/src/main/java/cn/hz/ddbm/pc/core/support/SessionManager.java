package cn.hz.ddbm.pc.core.support;


import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.exception.SessionException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * @Description 会话接口
 * @Author wanglin
 * @Date 2024/8/7 21:29
 * @Version 1.0.0
 **/
public interface SessionManager {
    Type code();

    void set(String flowName, Serializable flowId, Map<String, Object> session) throws IOException;

    Map<String, Object> get(String flowName, Serializable flowId) throws IOException;

    default void flush(FsmContext  ctx) throws SessionException {
        try {
            set(ctx.getFlow().getName(), ctx.getId(), ctx.getSession());
        } catch (IOException e) {
            throw new SessionException(e);
        }
    }

    enum Type {
        memory, redis
    }
}
