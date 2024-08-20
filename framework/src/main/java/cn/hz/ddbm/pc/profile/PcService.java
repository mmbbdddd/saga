package cn.hz.ddbm.pc.profile;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.exception.InterruptedFlowException;
import cn.hz.ddbm.pc.core.exception.SessionException;
import cn.hz.ddbm.pc.core.exception.wrap.ActionException;
import cn.hz.ddbm.pc.core.exception.wrap.PauseFlowException;
import cn.hz.ddbm.pc.core.exception.wrap.StatusException;
import cn.hz.ddbm.pc.core.log.Logs;
import cn.hz.ddbm.pc.core.utils.InfraUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PcService {
    Map<String, Fsm> flows = new HashMap<>();


    public <S extends Enum<S>, T extends FsmPayload<S>> void batchExecute(String flowName, List<T> payloads, String event) throws StatusException, SessionException {
        Assert.notNull(flowName, "flowName is null");
        Assert.notNull(payloads, "FlowPayload is null");
        event = StrUtil.isBlank(event) ? Coasts.EVENT_DEFAULT : event;
        Fsm<S> flow = flows.get(flowName);

        for (T payload : payloads) {
            FsmContext<S, T> ctx = new FsmContext<>(flow, payload, event,flow.getProfile());
            execute(ctx);
        }
    }


    public <S extends Enum<S>, T extends FsmPayload<S>> void execute(String flowName, T payload, String event) throws StatusException, SessionException {
        Assert.notNull(flowName, "flowName is null");
        Assert.notNull(payload, "FlowPayload is null");
        event = StrUtil.isBlank(event) ? Coasts.EVENT_DEFAULT : event;
        Fsm<S>           flow = flows.get(flowName);
        FsmContext<?, ?> ctx  = new FsmContext<>(flow, payload, event, flow.getProfile());
        execute(ctx);
    }

    public <S extends Enum<S>, T extends FsmPayload<S>> void execute(FsmContext<S, T> ctx) throws StatusException, SessionException {
        if (Boolean.FALSE.equals(tryLock(ctx))) {
            return;
        }
        try {
            Boolean fluent = ctx.getFluent();
            ctx.getFlow().execute(ctx);
            if (fluent && isCanContinue(ctx)) {
                ctx.setEvent(Coasts.EVENT_DEFAULT);
                ctx.getFlow().execute(ctx);
            }
        } catch (FsmEndException e) {
            //即PauseFlowException
            Logs.error.error("{},{},{}", ctx.getFlow().getName(), ctx.getId(), e.getMessage());
            flush(ctx);
        } catch (ActionException e) {
            //io异常 = 可重试
            if (e.getRaw() instanceof IOException) {
                Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), e);
                flush(ctx);
                execute(ctx);
            }
            //中断流程除（内部错误：不可重复执行，执行次数受限……）再次调度可触发：
            if (e.getRaw() instanceof InterruptedFlowException) {
                Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), e);
                flush(ctx);
            }
            //中断流程（内部程序错误：配置错误，代码错误）再次调度不响应：
            if (e.getRaw() instanceof PauseFlowException) {
                Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), e);
                flush(ctx);
            }
        } catch (StatusException e) {
            //todo
            //即PauseFlowException
            Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), e.getRaw());
            flush(ctx);
        } finally {
            releaseLock(ctx);
        }

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
    public <S extends Enum<S>, T extends FsmPayload<S>> boolean isCanContinue(FsmContext<S, T> ctx) {
        State<S> state    = ctx.getStatus();
        String   flowName = ctx.getFlow().getName();
        if (ctx.getFlow().isRouter(state.getState())) {
            return true;
        }
        if (!state.isRunnable()) {
            Logs.flow.info("流程不可运行：{},{},{}", flowName, ctx.getId(), state.getState());
            return false;
        }

        Long    exeRetry  = InfraUtils.getMetricsTemplate().get(ctx.getFlow().getName(), ctx.getId(), ctx.getStatus().getState(), Coasts.EXECUTE_COUNT);
        Integer nodeRetry = ctx.getFlow().getNode(state.getState()).getRetry();

        if (exeRetry > nodeRetry) {
            Logs.flow.info("流程已限流：{},{},{},{}>{}", flowName, ctx.getId(), state.getState(), exeRetry, nodeRetry);
            return false;
        }
        return true;
    }

    /**
     * 刷新状态到基础设施
     */
    private void flush(FsmContext<?, ?> ctx) throws SessionException, StatusException {
        ctx.syncPayLoad();
        InfraUtils.getSessionManager(ctx.getProfile().getSessionManager()).flush(ctx);
        InfraUtils.getStatusManager(ctx.getProfile().getStatusManager()).flush(ctx);
    }


    private void releaseLock(FsmContext<?, ?> ctx) {
        String key = String.format("%s:%s:%s", InfraUtils.getDomain(), ctx.getFlow().getName(), ctx.getId());
        try {
            InfraUtils.getLocker().releaseLock(key);
        } catch (Exception e) {
            //todo
            Logs.error.error("", e);
        }
    }

    private Boolean tryLock(FsmContext<?, ?> ctx) {
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
