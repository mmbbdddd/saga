package cn.hz.ddbm.pc.newcore.infra;


import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.StatusException;

import java.io.IOException;
import java.io.Serializable;

/**
 * @Description 状态接口
 * @Author wanglin
 * @Date 2024/8/7 21:29
 * @Version 1.0.0
 **/


public interface StatusManager {
    Coast.StatusType code();

    void setStatus(String flow, Serializable flowId, FlowContext ctx, Integer timeout) throws IOException;

    Triple<FlowStatus, ?, String> getStatus(String flow, Serializable flowId) throws IOException;

    default void flush(FlowContext ctx) throws StatusException {
        try {
            setStatus(ctx.getFlow().getName(), ctx.getId(), ctx, ctx.getProfile()
                    .getStatusTimeout());
        } catch (IOException e) {
            throw new StatusException(e);
        }
    }


}
