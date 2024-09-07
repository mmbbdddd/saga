package cn.hz.ddbm.pc.newcore.saga.worker;

import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.OffsetState;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.LockException;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaActionProxy;

public class SagaLocalWorker<S extends Enum<S>> extends SagaWorker<S> {
    SagaState<S>         state;
    SagaState<S>         pre;
    SagaState<S>         next;
    SagaState<S>         rollback;
    SagaState<S>         manual;
    LocalSagaActionProxy action;


    public SagaLocalWorker(int index, S pre, S current, S next, Class action) {
        super(index, current);
        this.next     = null == next ? null : new SagaState<>(next, OffsetState.task, SagaState.Direction.forward);
        this.rollback = new SagaState<>(current, OffsetState.task, SagaState.Direction.backoff);
        this.pre      = null == pre ? null : new SagaState<>(pre, OffsetState.task, SagaState.Direction.backoff);
        this.manual   = new SagaState<>(current, OffsetState.manual, SagaState.Direction.backoff);
        this.action   = new LocalSagaActionProxy(action);
    }

    public void execute(SagaContext<S> ctx) throws IdempotentException, ActionException, LockException {
        ProcesorService processor = ctx.getProcessor();
        ctx.setAction(action);
        if (ctx.getState().getDirection().isForward()) {
            Integer retryTimes       = ctx.getFlow().getRetry(ctx.getState());
            Long    executeTimeState = processor.getExecuteTimes(ctx, ctx.getState());
            if (executeTimeState > retryTimes) {
                Logs.flow.debug(">>>>重试次数超限,状态变化{}>>>>{}", ctx.getState(), rollback);
                ctx.setState(rollback);
            }
            try {
                processor.idempotent(ctx);
                action.execute(ctx);
                Logs.flow.debug(">>>>状态变迁{}", ctx.getState().getState());
                if (null == next) {
                    ctx.getState().offset(OffsetState.end);
                } else {
                    ctx.setState(next);
                }
            } catch (Exception e) {
                processor.unidempotent(ctx);
            }
        } else {
            Integer retryTimes       = ctx.getFlow().getRetry(ctx.getState());
            Long    executeTimeState = processor.getExecuteTimes(ctx, ctx.getState());
            if (executeTimeState > retryTimes) {
                Logs.flow.debug("<<<<重试次数超限，状态变化{}>>>>{}", ctx.getState(), manual);
                ctx.setState(manual);
            }
            try {
                processor.idempotent(ctx);
                action.execute(ctx);
                if (null == pre) {
                    ctx.getState().offset(OffsetState.end);
                } else {
                    Logs.flow.debug("<<<<状态变迁{}>>>>{}", ctx.getState().getState(), pre);
                    ctx.setState(pre);
                }
            } catch (Exception e) {
                processor.unidempotent(ctx);
            }
        }
    }
}
