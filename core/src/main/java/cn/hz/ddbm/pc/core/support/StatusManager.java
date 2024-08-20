package cn.hz.ddbm.pc.core.support;


import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.State;
import cn.hz.ddbm.pc.core.exception.wrap.StatusException;

import java.io.IOException;
import java.io.Serializable;

/**
 * @Description TODO
 * @Author wanglin
 * @Date 2024/8/7 21:29
 * @Version 1.0.0
 **/


public interface StatusManager {
    Type code();

    void setStatus(String flow, Serializable flowId, State<?> status, Integer timeout, FsmContext<?, ?> ctx) throws IOException;

    State<?> getStatus(String flow, Serializable flowId) throws IOException;

    default void flush(FsmContext<?, ?> ctx) throws StatusException {
        try {
            setStatus(ctx.getFlow().getName(), ctx.getId(), ctx.getStatus(), ctx.getProfile().getStatusTimeout(), ctx);
        } catch (IOException e) {
            throw new StatusException(e);
        }
    }

    enum Type {
        memory, redis, dao
    }
}
