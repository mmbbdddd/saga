package cn.hz.ddbm.pc.core.support;


import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.exception.StatusException;

import java.io.IOException;
import java.io.Serializable;

/**
 * @Description 状态接口
 * @Author wanglin
 * @Date 2024/8/7 21:29
 * @Version 1.0.0
 **/


public interface StatusManager {
    Type code();

    void setStatus(String flow, Serializable flowId, Triple<FlowStatus, ?, String> triple, Integer timeout, FsmContext ctx) throws IOException;

    Triple<FlowStatus, ?, String> getStatus(String flow, Serializable flowId) throws IOException;

    default void flush(FsmContext ctx) throws StatusException {
        try {
            setStatus(ctx.getFlow().getName(), ctx.getId(), ctx.getStatus(), ctx.getProfile()
                    .getStatusTimeout(), ctx);
        } catch (IOException e) {
            throw new StatusException(e);
        }
    }

    enum Type {
        memory, redis, dao
    }
}
