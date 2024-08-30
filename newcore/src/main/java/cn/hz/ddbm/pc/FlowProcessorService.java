package cn.hz.ddbm.pc;

import cn.hutool.core.util.ReflectUtil;
import cn.hz.ddbm.pc.newcore.*;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.infra.InfraUtils;
import cn.hz.ddbm.pc.newcore.infra.SessionManager;
import cn.hz.ddbm.pc.newcore.saga.SagaModel;
import cn.hz.ddbm.pc.newcore.saga.SagaState;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

public abstract class FlowProcessorService<C extends FlowContext> implements FlowProcessor<C> {
    //todo
    public FlowModel getFlow(String flowName) {
        return null;
    }
    public PluginService plugin() {
        return new PluginService();
    }

    public Integer getStateExecuteTimes(FlowContext ctx, String flow, State state) {
        return ctx.getExecuteTimes().get();
    }

    public boolean tryLock(FlowContext ctx) {
        return true;
    }

    public void updateStatus(FlowContext ctx) throws StatusException {

    }

    public void idempotent(String name, Serializable id, SagaState state, String event) throws IdempotentException {

    }


    public void unidempotent(String name, Serializable id, State state, String event) throws IdempotentException {

    }

    public void unLock(FlowContext ctx) {

    }

    /**
     * 刷新状态到基础设施
     */
    public void flush(FlowContext ctx) throws SessionException, StatusException {
        ctx.syncpayload();
        InfraUtils.getSessionManager(ctx.getProfile().getSession()).flush(ctx);
        InfraUtils.getStatusManager(ctx.getProfile().getStatus()).flush(ctx);
    }

    public boolean isRetryable(Throwable e, FlowContext ctx) {
        if (ReflectUtil.getFieldValue(e, "raw") != null && ReflectUtil.getFieldValue(e, "raw") instanceof Exception) {
            e = (Exception) ReflectUtil.getFieldValue(e, "raw");
        }
        Boolean isRetryException = false;
        isRetryException |= e instanceof IOException;
        return isRetryException;
    }

    public boolean isInterrupted(Throwable e, FlowContext ctx) {
        if (ReflectUtil.getFieldValue(e, "raw") != null && ReflectUtil.getFieldValue(e, "raw") instanceof Exception) {
            e = (Exception) ReflectUtil.getFieldValue(e, "raw");
        }
        Boolean isInterruptedException = false;
        isInterruptedException |= e instanceof LockException;
        return isInterruptedException;
    }

    public boolean isPaused(Throwable e, FlowContext ctx) {
        if (ReflectUtil.getFieldValue(e, "raw") != null && ReflectUtil.getFieldValue(e, "raw") instanceof Exception) {
            e = (Exception) ReflectUtil.getFieldValue(e, "raw");
        }
        Boolean isPausedException = false;
        isPausedException |= e instanceof IllegalArgumentException;
        return isPausedException;
    }

    public boolean isStoped(Throwable e, FlowContext ctx) {
        return e instanceof FlowEndException || FlowStatus.isEnd(ctx.getStatus());
    }


}


