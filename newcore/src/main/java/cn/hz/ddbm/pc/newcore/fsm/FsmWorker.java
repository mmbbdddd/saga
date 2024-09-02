package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.FlowProcessorService;
import cn.hz.ddbm.pc.newcore.Worker;
import cn.hz.ddbm.pc.newcore.exception.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public abstract class FsmWorker<S extends Enum<S>> extends Worker<FsmContext<S>> {

    @Override
    public abstract void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException, LockException;

}

class SagaFsmWorker<S extends Enum<S>> extends FsmWorker<S> {
    FsmState<S> from;
    FsmState<S> failover;
    Class<? extends FsmRouterAction> action;


    public SagaFsmWorker(S from, Class<? extends FsmRouterAction> action, S failover) {
        this.from     = new FsmState<>(from);
        this.failover = new FsmState<>(failover);
        this.action   = action;
    }

    @Override
    public void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException, LockException {
        FlowProcessorService processor = ctx.getProcessor();
        FsmState<S>          lastState = ctx.getState();
        ctx.setAction((FsmRouterAction) processor.getAction(action));
        FsmRouterAction<S> sagaAction = (FsmRouterAction) ctx.getAction();
        //如果任务可执行
        if (Objects.equals(lastState, from)) {
            //加锁
            processor.tryLock(ctx);

            processor.plugin().pre(ctx);
            //设置容错
            ctx.setState(failover);
            processor.updateStatus(ctx);
            //冥等
            processor.idempotent(ctx.getAction().code(), ctx);
            //执行业务
            try {
                sagaAction.execute(ctx);
                processor.plugin().post(lastState, ctx);
            } catch (ActionException e) {
                processor.plugin().error(lastState, e, ctx);
                throw e;
            } finally {
                processor.unLock(ctx);
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        } else if (Objects.equals(lastState, failover)) {
            try {
                S queryResult = sagaAction.executeQuery(ctx);
                //如果业务未发送成功，取消冥等，设置为任务可执行状态
                if (queryResult == null) {
                    processor.unidempotent(ctx.getAction().code(), ctx);
                    ctx.setState(from);
                } else {
                    //业务有返回
                    if (!ctx.getFlow().isState(queryResult)) {
                        throw new IllegalArgumentException("queryResult[" + queryResult + "] not a right state code");
                    }
                    ctx.setState(new FsmState<>(queryResult));
                }

                processor.plugin().post(lastState, ctx);
            } catch (NoSuchRecordException e) {
                processor.unidempotent(ctx.getAction().code(), ctx);
                ctx.setState(from);
                processor.plugin().error(lastState, e, ctx);
            } catch (ActionException e) {
                ctx.setState(failover);
                processor.plugin().error(lastState, e, ctx);
                throw e;
            } finally {
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        }

    }
}


class ToFsmWorker<S extends Enum<S>> extends FsmWorker<S> {

    FsmState<S>             from;
    FsmState<S>             to;
    Class<? extends FsmCommandAction> action;

    public ToFsmWorker(S from, Class<? extends FsmCommandAction> action, S to) {
        this.from   = new FsmState<>(from);
        this.action = action;
        this.to     = new FsmState<>(to);
    }

    @Override
    public void execute(FsmContext<S> ctx) throws StatusException, ActionException {
        FlowProcessorService processor = ctx.getProcessor();
        FsmFlow<S>           flow      = ctx.getFlow();
        Serializable         id        = ctx.getId();
        FsmState<S>          lastNode  = ctx.getState();
        ctx.setAction((FsmCommandAction) processor.getAction(action));
        FsmCommandAction<S> commandAction = (FsmCommandAction) ctx.getAction();
        try {
            processor.plugin().pre(ctx);
            commandAction.command(ctx);
            ctx.setState(to);
            processor.plugin().post(lastNode, ctx);
        } catch (ActionException e) {
            processor.plugin().error(lastNode, e, ctx);
            throw e;
        } finally {
            processor.plugin()._finally(ctx);
            processor.metricsNode(ctx);
        }
    }

}
