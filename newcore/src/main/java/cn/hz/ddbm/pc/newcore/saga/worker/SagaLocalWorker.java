package cn.hz.ddbm.pc.newcore.saga.worker;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.FlowEndException;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.LockException;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaActionProxy;

import java.util.Objects;

public class SagaLocalWorker<S extends Enum<S>> extends SagaWorker<S> {
    SagaState<S>            pre;
    SagaState<S>            next;
    SagaState<S>            rollback;
    FlowStatus              manual;
    LocalSagaActionProxy<S> action;


    public SagaLocalWorker(Integer index, Pair<S, Class<? extends SagaAction>> node, Integer total) {
        super(index, node.getKey());
        this.next     = Objects.equals(total, index+1) ? null : new SagaState<>(index + 1, SagaState.Offset.task, FlowStatus.RUNNABLE);
        this.rollback = new SagaState<>(index, SagaState.Offset.rollback, FlowStatus.RUNNABLE);
        this.pre      = index == 0 ? null : new SagaState<>(index - 1, SagaState.Offset.task, FlowStatus.RUNNABLE);
        this.manual   = FlowStatus.MANUAL;
        this.action   = new LocalSagaActionProxy<>(node.getValue());
    }

    public void execute(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws IdempotentException, ActionException, LockException, FlowEndException {
        ctx.setAction(action);
        SagaState<S>     lastState = ctx.getState();
        SagaState.Offset offset    = lastState.getOffset();
        if (null == offset) {
            return;
        }
        switch (offset) {
            case task:
            case taskRetry:
                doActionWithTranscational(lastState, ctx);
                break;
            case failover:
            case rollback:
            case rollbackRetry:
                rollbackActionWithTranscational(lastState, ctx);
                break;
            case rollbackFailover:
        }
    }


    private void doActionWithTranscational(SagaState<S> lastState, FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws IdempotentException {
        ProcesorService processor        = ctx.getProcessor();
        Integer         retryTimes       = ctx.getFlow().getRetry(ctx.getState());
        Long            executeTimeState = processor.getExecuteTimes(ctx, ctx.getState());
        if (executeTimeState > retryTimes) {
            ctx.setState(rollback);
        }
        try {
            processor.plugin().pre(ctx);
            processor.idempotent(ctx);
            action.localSaga(ctx);
            if (null == next) {
                ctx.getState().setStatus(FlowStatus.SU);
            } else {
                ctx.setState(next);
            }
            processor.plugin().post(lastState, ctx);
        } catch (Exception e) {
            processor.unidempotent(ctx);
            processor.plugin().error(lastState, e, ctx);
        }

    }

    private void rollbackActionWithTranscational(SagaState<S> lastState, FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws IdempotentException {
        ProcesorService processor        = ctx.getProcessor();
        Integer         retryTimes       = ctx.getFlow().getRetry(ctx.getState());
        Long            executeTimeState = processor.getExecuteTimes(ctx, ctx.getState());
        if (executeTimeState > retryTimes) {
            ctx.getState().setStatus(manual);
        }
        try {
            processor.plugin().pre(ctx);
            processor.idempotent(ctx);
            action.localSagaRollback(ctx);
            if (null == pre) {
                ctx.getState().setStatus(FlowStatus.FAIL);
            } else {
                ctx.setState(pre);
            }
            processor.plugin().post(lastState, ctx);
        } catch (Exception e) {
            processor.unidempotent(ctx);
            processor.plugin().error(lastState, e, ctx);
        }
    }
}
