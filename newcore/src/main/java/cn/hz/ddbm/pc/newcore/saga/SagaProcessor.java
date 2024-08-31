package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.FlowProcessorService;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.infra.InfraUtils;
import cn.hz.ddbm.pc.newcore.infra.SessionManager;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.utils.ExceptionUtils;

import java.util.Map;

public class SagaProcessor<S> extends FlowProcessorService<SagaContext<S>> {


    public void workerProcess(String flowName, SagaPayload<S> payload, Profile profile) throws FlowEndException, InterruptedException, PauseException, SessionException {
        SagaModel<S>        flow           = (SagaModel<S>) getFlow(flowName);
        SessionManager      sessionManager = InfraUtils.getSessionManager(profile.getSession());
        Map<String, Object> session        = sessionManager.get(flowName, payload.getId());

        SagaContext<S> ctx = new SagaContext<>(flow, payload, profile, session);
        workerProcess(ctx);
    }


    public void workerProcess(SagaContext<S> ctx) throws FlowEndException, InterruptedException, PauseException {
        Assert.notNull(ctx, "ctx is null");
        ctx.setProcessor(this);
        SagaModel<S> flow       = ctx.getFlow();
        FlowStatus   status     = ctx.getStatus();
        SagaState<S> state      = ctx.getState();
        Integer      stateRetry = flow.getRetry(state);
        //状态不可执行
        if (FlowStatus.isEnd(status)) {
            throw new FlowEndException();
        }
        if (FlowStatus.PAUSE.equals(status)) {
            throw new PauseException();
        }
        //工作流结束
        if (flow.isEnd(state)) {
            throw new FlowEndException();
        }
        //工作流结束
        Integer stateExecuteTimes = getStateExecuteTimes(ctx, flow.getName(), state);
        if (stateExecuteTimes > stateRetry) {
            throw new InterruptedException(String.format("节点%s执行次数超限制{}>{}", state.code(), stateExecuteTimes, stateRetry));
        }

        SagaWorker<S> worker = flow.getWorker(ctx.getState().getState());
        try {
            ctx.setWorker(worker);
            worker.execute(ctx);
        } catch (Throwable e) {
            try {
                if (isInterrupted(e, ctx)) { //中断异常，暂停执行，等下一次事件触发
                    Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    flush(ctx);
                } else if (isPaused(e, ctx)) { //暂停异常，状态设置为暂停，等人工修复
                    Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    ctx.setStatus(FlowStatus.PAUSE);
                    flush(ctx);
                } else if (isStoped(e, ctx)) {//流程结束或者取消
                    Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    status = ctx.getFlow().getEnds().contains(ctx.getState()) ? FlowStatus.FINISH : ctx.getStatus();
                    ctx.setStatus(status);
                    flush(ctx);
                } else {
                    //可重试异常
                    Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), ExceptionUtils.unwrap(e));
                    flush(ctx);
//                    ctx.getFlow().execute(ctx);
                }
            } catch (StatusException | SessionException e2) {
                Logs.status.error("", e2);
            }
        }
    }


}
