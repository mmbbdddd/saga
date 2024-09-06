package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.newcore.Worker;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.fsm.action.FsmAction;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.router.LocalToRouter;
import cn.hz.ddbm.pc.newcore.fsm.router.RemoteRouter;
import lombok.Data;

import java.util.Objects;

@Data
public abstract class FsmWorker<S extends Enum<S>> extends Worker<FsmContext<S>> {
    public static <S extends Enum<S>> FsmWorker<S> local(S from, Class<? extends LocalFsmAction> action, LocalToRouter<S> router) {

        return new ToWorker<>(from, action, router);

    }

    public static <S extends Enum<S>> FsmWorker<S> remote(S from, Class<? extends FsmAction> action, RemoteRouter<S> router) {
        return new SagaWorker<>(from, action, router);
    }

    public abstract void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException, LockException, PauseException, FlowEndException, InterruptedException, ProcessingException, NoSuchRecordException;
}

class ToWorker<S extends Enum<S>> extends FsmWorker<S> {
    FsmState<S>       from;
    FsmActionProxy<S> action;
    LocalToRouter<S>  router;

    public ToWorker(S from, Class<? extends LocalFsmAction> action, LocalToRouter<S> router) {
        this.from   = FsmState.of(from);
        this.action = new FsmActionProxy<>(action);
        this.router = router;
    }

    public void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException, LockException, PauseException, FlowEndException, InterruptedException, ProcessingException, NoSuchRecordException {
        FsmProcessor<S> processor = (FsmProcessor<S>) ctx.getProcessor();
        ctx.setAction(action);
        //如果任务可执行
        FsmState.Offset offset = ctx.getState().offset;

        if (Objects.equals(offset, FsmState.Offset.task)) {
            processor.plugin().pre(ctx);
            //todo jdbc transition
            //执行业务
            try {
                action.execute(ctx);
                S nextState = router.router(ctx, null);
                ctx.setState(FsmState.of(nextState));
                processor.plugin().post(from, ctx);
            } catch (ActionException e) {
                processor.plugin().error(from, e, ctx);
                throw e;
            } finally {
                //end transition
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        }
    }

}

class SagaWorker<S extends Enum<S>> extends FsmWorker<S> {
    FsmState<S>       from;
    FsmActionProxy<S> action;
    RemoteRouter<S>   router;

    public SagaWorker(S from, Class<? extends FsmAction> action, RemoteRouter<S> router) {
        this.from   = FsmState.of(from);
        this.action = new FsmActionProxy<>(action);
        this.router = router;
    }

    public void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException, LockException, PauseException, FlowEndException, InterruptedException, ProcessingException, NoSuchRecordException {
        FsmProcessor<S> processor = (FsmProcessor<S>) ctx.getProcessor();
        ctx.setAction(action);
        //如果任务可执行
        FsmState<S>     lastSate = ctx.getState();
        FsmState.Offset offset   = ctx.getState().offset;

        FsmState<S> failover = new FsmState<>(from.state, FsmState.Offset.failover);
        if (Objects.equals(offset, FsmState.Offset.task)) {
            //加锁
            processor.tryLock(ctx);

            processor.plugin().pre(ctx);
            //设置容错
            ctx.setState(failover);
            processor.updateStatus(ctx);
            //冥等
            processor.idempotent( ctx);
            //执行业务
            try {
                action.execute(ctx);
                processor.plugin().post(from, ctx);
            } catch (ActionException e) {
                processor.plugin().error(from, e, ctx);
                throw e;
            } finally {
                processor.unLock(ctx);
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        } else if (Objects.equals(offset, FsmState.Offset.failover)) {
            try {
                Object queryResult = action.executeQuery(ctx);
                Assert.notNull(queryResult, "queryResult is null");
                S nextState = router.router(ctx, queryResult);
                //如果业务未发送成功，取消冥等，设置为任务可执行状态
                //业务有返回
                if (!ctx.getFlow().isRightState(FsmState.of(nextState))) {
                    throw new IllegalArgumentException("queryResult[" + queryResult + "] not a right state code");
                }
                ctx.setState(FsmState.of(nextState));
                processor.plugin().post(lastSate, ctx);
            } catch (NoSuchRecordException e) {
                ctx.setState(failover);
                processor.plugin().error(lastSate, e, ctx);
                throw e;
            } catch (ProcessingException e) {
                ctx.setState(failover);
                processor.plugin().error(lastSate, e, ctx);
                throw e;
            } catch (ActionException e) {
                ctx.setState(failover);
                processor.plugin().error(lastSate, e, ctx);
                throw e;
            } finally {
                processor.plugin()._finally(ctx);
                processor.metricsNode(ctx);
            }
        }
    }
}
