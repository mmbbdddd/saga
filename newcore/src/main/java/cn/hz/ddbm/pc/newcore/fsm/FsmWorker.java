package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.FlowProcessorService;
import cn.hz.ddbm.pc.newcore.Worker;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public abstract class FsmWorker<S extends Serializable> extends Worker<FsmContext<S>> {

    @Override
    public abstract void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException;

}

class SagaFsmWorker<S extends Serializable> extends FsmWorker<S> {
    FsmState<S> from;
    FsmState<S> failover;
    String      action;


    public SagaFsmWorker(S from, String sagaAction, S failover) {
        this.from       = new FsmState<>(from);
        this.failover   = new FsmState<>(failover);
        this.action = sagaAction;
    }

    @Override
    public void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException {
        FlowProcessorService processor = ctx.getProcessor();
        FsmState<S>          lastState = ctx.getState();
        ctx.setAction((FsmRouterAction) processor.getAction(action, FsmRouterAction.class));
        FsmRouterAction<S> sagaAction = (FsmRouterAction) ctx.getAction();
        //如果任务可执行
        if (Objects.equals(lastState, from)) {
            //加锁
            if (!processor.tryLock(ctx)) {
                return;
            }
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


class ToFsmWorker<S extends Serializable> extends FsmWorker<S> {

    FsmState<S>       from;
    FsmState<S>       to;
    String            action;

    public ToFsmWorker(S from, String commandAction, S to) {
        this.from          = new FsmState<>(from);
        this.action        = commandAction;
        this.to            = new FsmState<>(to);
    }

    @Override
    public void execute(FsmContext<S> ctx) throws StatusException, ActionException {
        FlowProcessorService processor = ctx.getProcessor();
        FsmFlow<S>           flow      = ctx.getFlow();
        Serializable         id        = ctx.getId();
        FsmState<S>          lastNode  = ctx.getState();
        ctx.setAction((FsmCommandAction) processor.getAction(action, FsmCommandAction.class));
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
