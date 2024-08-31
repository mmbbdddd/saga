package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.FlowProcessorService;
import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;

import java.io.Serializable;
import java.util.Objects;

public class SagaFsmWorker<S extends Serializable> extends FsmWorker<S> implements Action {
    FsmState<S>           from;
    FsmState<S>           failover;
    String                action;
    FsmSagaActionProxy<S> sagaAction;


    public SagaFsmWorker(S from, String sagaAction, S failover) {
        this.from       = new FsmState<>(from);
        this.failover   = new FsmState<>(failover);
        this.action     = sagaAction;
        this.sagaAction = new FsmSagaActionProxy(this.action);
    }

    @Override
    public void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException {
        FlowProcessorService processor = ctx.getProcessor();
        FsmState<S>          lastState = ctx.getState();
        ctx.setAction(this.sagaAction.getOrInitAction());
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
            processor.idempotent(ctx.getFlow().getName(), ctx.getId(), lastState, ctx.getEvent());
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
                ctx.metricsNode(ctx);
            }
        } else if (Objects.equals(lastState, failover)) {
            try {
                S queryResult = sagaAction.executeQuery(ctx);
                //如果业务未发送成功，取消冥等，设置为任务可执行状态
                if (queryResult == null) {
                    processor.unidempotent(ctx.getFlow().getName(), ctx.getId(), lastState, ctx.getEvent());
                    ctx.setState(from);
                }
                //业务有返回
                ctx.setState(new FsmState<>(queryResult));

                processor.plugin().post(lastState, ctx);
            } catch (NoSuchRecordException e) {
                processor.unidempotent(ctx.getFlow().getName(), ctx.getId(), lastState, ctx.getEvent());
                ctx.setState(from);
                processor.plugin().error(lastState, e, ctx);
            } catch (ActionException e) {
                ctx.setState(failover);
                processor.plugin().error(lastState, e, ctx);
                throw e;
            } finally {
                processor.plugin()._finally(ctx);
                ctx.metricsNode(ctx);
            }
        }

    }


    @Override
    public String code() {
        return sagaAction.code();
    }

}