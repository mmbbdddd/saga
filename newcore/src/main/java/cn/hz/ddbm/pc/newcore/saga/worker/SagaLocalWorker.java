package cn.hz.ddbm.pc.newcore.saga.worker;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.FlowEndException;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.LockException;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.saga.*;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaAction;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaActionProxy;

import java.util.Objects;

public class SagaLocalWorker<S extends Enum<S>> extends SagaWorker<S> {
    SagaState<S>         pre;
    SagaState<S>         next;
    SagaState<S>         rollback;
    FlowStatus           manual;
    LocalSagaActionProxy action;


    public SagaLocalWorker(Integer index, Pair<S, Class<? extends SagaAction>> node, SagaFlow<S> flow) {
        super(index, node.getKey(), flow);
        this.next     = new SagaState<>(index + 1, SagaState.Offset.task, flow);
        this.rollback = new SagaState<>(index, SagaState.Offset.rollback, flow);
        this.pre      = new SagaState<>(index - 1, SagaState.Offset.task, flow);
        this.manual   = FlowStatus.MANUAL;
        this.action   = new LocalSagaActionProxy((Class<? extends LocalSagaAction>) node.getValue());
    }

    public void execute(SagaContext<S> ctx) throws IdempotentException, ActionException, LockException, FlowEndException {
        ctx.setAction(action);
        SagaState        lastState = ctx.getState();
        SagaState.Offset offset    = lastState.getOffset();
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


    private void doActionWithTranscational(SagaState lastState, SagaContext<S> ctx) throws IdempotentException {
        ProcesorService processor        = ctx.getProcessor();
        Integer         retryTimes       = ctx.getFlow().getRetry(ctx.getState());
        Long            executeTimeState = processor.getExecuteTimes(ctx, ctx.getState());
        if (executeTimeState > retryTimes) {
            ctx.setState(rollback);
        }
        try {
            processor.plugin().pre(ctx);
            processor.idempotent(ctx);
            action.execute(ctx);
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

    private void rollbackActionWithTranscational(SagaState lastState, SagaContext<S> ctx) throws IdempotentException {
        ProcesorService processor        = ctx.getProcessor();
        Integer         retryTimes       = ctx.getFlow().getRetry(ctx.getState());
        Long            executeTimeState = processor.getExecuteTimes(ctx, ctx.getState());
        if (executeTimeState > retryTimes) {
            ctx.getState().setStatus(manual);
        }
        try {
            processor.plugin().pre(ctx);
            processor.idempotent(ctx);
            action.execute(ctx);
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
