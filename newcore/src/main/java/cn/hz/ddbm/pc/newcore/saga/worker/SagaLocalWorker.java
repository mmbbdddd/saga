package cn.hz.ddbm.pc.newcore.saga.worker;

import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.LockException;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaActionProxy;

public class SagaLocalWorker<S extends Enum<S>> extends SagaWorker<S> {
    SagaState<S>         pre;
    SagaState<S>         next;
    SagaState<S>         rollback;
    FlowStatus           manual;
    LocalSagaActionProxy action;


    public SagaLocalWorker(int index, S pre, S current, S next, Class action) {
        super(index, current);
        this.pre      = new SagaState<>(pre, SagaState.Offset.task, false);
        this.next     = new SagaState<>(next, SagaState.Offset.task, true);
        this.rollback = new SagaState<>(current, SagaState.Offset.task, false);
        this.manual   = FlowStatus.MANUAL;
        this.action   = new LocalSagaActionProxy(action);
    }

    public void execute(SagaContext<S> ctx) throws IdempotentException, ActionException, LockException {
        ProcesorService processor = ctx.getProcessor();
        ctx.setAction(action);
        if (ctx.getState().getIsForward()) {
            Integer retryTimes       = ctx.getFlow().getRetry(ctx.getState());
            Long    executeTimeState = processor.getExecuteTimes(ctx, ctx.getState());
            if (executeTimeState > retryTimes) {
                Logs.flow.debug(">>>>重试次数超限,状态变化{}>>>>{}", ctx.getState(), rollback);
                ctx.setState(rollback);
            }
            processor.idempotent(ctx);
            action.execute(ctx);
            Logs.flow.debug(">>>>状态变迁{}", ctx.getState().getMaster());
            ctx.setState(next);
        } else {
            Integer retryTimes       = ctx.getFlow().getRetry(ctx.getState());
            Long    executeTimeState = processor.getExecuteTimes(ctx, ctx.getState());
            if (executeTimeState > retryTimes) {
                Logs.flow.debug("<<<<重试次数超限，状态变化{}>>>>{}", ctx.getState(), manual);
                ctx.setStatus(manual);
            }
            processor.idempotent(ctx);
            action.execute(ctx);
            Logs.flow.debug("<<<<状态变迁{}>>>>{}", ctx.getState().getMaster(), pre);
            ctx.setState(pre);
        }
    }
}
