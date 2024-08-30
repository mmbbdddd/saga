package cn.hz.ddbm.pc.profile;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.exception.*;
import cn.hz.ddbm.pc.core.exception.StatusException;
import cn.hz.ddbm.pc.core.log.Logs;
import cn.hz.ddbm.pc.core.utils.ExceptionUtils;
import cn.hz.ddbm.pc.core.utils.InfraUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseService {
    Map<String, Fsm> flows = new HashMap<>();


    public   void batchExecute(String flowName, List<FsmPayload> payloads, String event) throws StatusException, SessionException {
        Assert.notNull(flowName, "flowName is null");
        Assert.notNull(payloads, "FlowPayload is null");
        event = StrUtil.isBlank(event) ? Coasts.EVENT_FORWARD : event;
        Fsm  flow = flows.get(flowName);

        for (FsmPayload payload : payloads) {
            FsmContext  ctx = new FsmContext(flow, payload, event, flow.getProfile());
            execute(ctx);
        }
    }


    public   void execute(String flowName, FsmPayload payload, String event) throws StatusException, SessionException {
        Assert.notNull(flowName, "flowName is null");
        Assert.notNull(payload, "FlowPayload is null");
        event = StrUtil.isBlank(event) ? Coasts.EVENT_FORWARD : event;
        Fsm            flow = flows.get(flowName);
        FsmContext  ctx  = new FsmContext(flow, payload, event, flow.getProfile());
        execute(ctx);
    }

    public   void execute(FsmContext  ctx) throws StatusException, SessionException {
        if (Boolean.FALSE.equals(tryLock(ctx))) {
            return;
        }
        try {
            Boolean fluent = ctx.getFluent();
            ctx.getFlow().execute(ctx);
            if (fluent && isCanContinue(ctx)) {
                ctx.setEvent(getFluentEvent(ctx));
                ctx.getFlow().execute(ctx);
            }
        } catch (StatusException e) {
            //todo
            //即PauseFlowException
            Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), e.getRaw());
            try {
                flush(ctx);
            } catch (StatusException | SessionException e2) {
                Logs.status.error("", e2);
            }
        } catch (Throwable e) {
            try {
                if (isInterrupted(e, ctx)) { //中断异常，暂停执行，等下一次事件触发
                    Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    flush(ctx);
                } else if (isPaused(e, ctx)) { //暂停异常，状态设置为暂停，等人工修复
                    Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    ctx.setFlowStatus(FlowStatus.PAUSE);
                    flush(ctx);
                } else if (isStoped(e, ctx)) {//流程结束或者取消
                    Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    FlowStatus status = ctx.getFlow().getEnds().contains(ctx.getState()) ? FlowStatus.FINISH : ctx.getFlowStatus();
                    ctx.setFlowStatus(status);
                    flush(ctx);
                } else {
                    //可重试异常
                    Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    flush(ctx);
//                    ctx.getFlow().execute(ctx);
                }
                e.printStackTrace();
            } catch (StatusException | SessionException e2) {
                Logs.status.error("", e2);
            }
        } finally {
            releaseLock(ctx);
        }

    }

    private String getFluentEvent(FsmContext ctx) {
        return ctx.getProcessor().getFluentEvent(ctx);
    }

    private boolean isRetryable(Throwable e, FsmContext ctx) {
        if (e instanceof WrapedException) {
            e = ((WrapedException) e).getRaw();
        }
        Boolean isRetryException = false;
        isRetryException |= e instanceof IOException;
        return isRetryException;
    }

    private boolean isInterrupted(Throwable e, FsmContext ctx) {
        if (e instanceof WrapedException) {
            e = ((WrapedException) e).getRaw();
        }
        Boolean isInterruptedException = false;
        isInterruptedException |= e instanceof LockException;
        return isInterruptedException;
    }

    private boolean isPaused(Throwable e, FsmContext ctx) {
        if (e instanceof WrapedException) {
            e = ((WrapedException) e).getRaw();
        }
        Boolean isPausedException = false;
        isPausedException |= e instanceof IllegalArgumentException;
        isPausedException |= e instanceof FlowConfigException;
        isPausedException |= e instanceof FlowSystemException;
        return isPausedException;
    }

    private boolean isStoped(Throwable e, FsmContext ctx) {
        return e instanceof FsmEndException || FlowStatus.isEnd(ctx.getFlowStatus());
    }

    public void addFlow(Fsm flow) {
        this.flows.put(flow.getName(), flow);
    }

    protected Fsm getFlow(String flowName) {
        return this.flows.get(flowName);
    }

    /**
     * 可继续运行
     * 1，流程状态是Runable状态
     * 2，节点状态类型是非end的
     * 3，运行时限制为false（执行次数限制等）
     *
     * @param ctx
     * @return
     */
    public   boolean isCanContinue(FsmContext  ctx) {

        String     flowName = ctx.getFlow().getName();
        FlowStatus status   = ctx.getFlowStatus();
        if (FlowStatus.isEnd(status)) {
            Logs.flow.debug("流程不可运行：{},{},{},{}", flowName, ctx.getId(), ctx.getStatus(), ctx.getState());
            return false;
        }
        State state = ctx.getState();
        if (!ctx.getFlow().isRunnableState(state)) {
            Logs.flow.debug("流程不可运行：{},{},{},{}", flowName, ctx.getId(), ctx.getStatus(), ctx.getState());
            return false;
        }

        Long    exeRetry  = InfraUtils.getMetricsTemplate().get(ctx.getFlow().getName(), ctx.getId(), ctx.getState(), Coasts.EXECUTE_COUNT);
        Integer nodeRetry = ctx.getProfile().getStateRetry(state);

        if (exeRetry > nodeRetry) {
            Logs.flow.warn("流程已限流：{},{},{},{}>{}", flowName, ctx.getId(), ctx.getState(), exeRetry, nodeRetry);
            return false;
        }
        return true;
    }

    /**
     * 刷新状态到基础设施
     */
    private void flush(FsmContext ctx) throws SessionException, StatusException {
        ctx.syncPayLoad();
        InfraUtils.getSessionManager(ctx.getProfile().getSessionManager()).flush(ctx);
        InfraUtils.getStatusManager(ctx.getProfile().getStatusManager()).flush(ctx);
    }


    private void releaseLock(FsmContext ctx) {
        String key = String.format("%s:%s:%s", InfraUtils.getDomain(), ctx.getFlow().getName(), ctx.getId());
        try {
            InfraUtils.getLocker().releaseLock(key);
        } catch (Exception e) {
            //todo
            Logs.error.error("", e);
        }
    }

    private Boolean tryLock(FsmContext ctx) {
        String key = String.format("%s:%s:%s", InfraUtils.getDomain(), ctx.getFlow().getName(), ctx.getId());
        try {
            InfraUtils.getLocker().tryLock(key, ctx.getProfile().getLockTimeout());
            return true;
        } catch (Exception e) {
            Logs.error.error("", e);
            return false;
        }
    }

}
