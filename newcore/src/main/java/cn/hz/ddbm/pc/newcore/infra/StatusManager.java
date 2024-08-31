package cn.hz.ddbm.pc.newcore.infra;


import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;

import java.io.Serializable;

/**
 * @Description 状态接口
 * @Author wanglin
 * @Date 2024/8/7 21:29
 * @Version 1.0.0
 **/


public interface StatusManager {
    Coast.StatusType code();

    void setStatus(String flow, Serializable flowId, Pair<FlowStatus, ?> status, Integer timeout) throws StatusException;

    Pair<FlowStatus, ?> getStatus(String flow, Serializable flowId) throws StatusException;

    void idempotent(String key) throws IdempotentException;

    void unidempotent(String key) throws IdempotentException;


    default void flush(FlowContext ctx) throws StatusException {
        setStatus(ctx.getFlow().getName(), ctx.getId(), Pair.of(ctx.getStatus(), ctx.getState()), ctx.getProfile().getStatusTimeoutMicros());
    }

}
