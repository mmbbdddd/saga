package cn.hz.ddbm.pc.newcore.fsm.worker;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.fsm.*;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmActionProxy;
import cn.hz.ddbm.pc.newcore.log.Logs;

public class FsmRemoteWorker<S extends Enum<S>> extends FsmWorker<S> {
    RemoteFsmActionProxy<S> action;
    Router<S>         router;

    public FsmRemoteWorker(Class<? extends RemoteFsmAction> action, Router<S> router) {
        this.action = new RemoteFsmActionProxy<>(action);
        this.router = router;
    }

    public void execute(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws StatusException, IdempotentException, ActionException, LockException, PauseException, FlowEndException, InterruptedException, ProcessingException, NoSuchRecordException {
        ctx.setAction(action);
        //如果任务可执行
        FsmState<S>     lastSate = ctx.getState();
        FsmState.Offset offset   = ctx.getState().getOffset();
        switch (offset) {
            case task:
            case taskRetry:
                remoteFsm(lastSate, ctx);
                break;
            case failover:
                remoteFsmFailover(lastSate, ctx);
                break;
        }
    }

    private void remoteFsmFailover(FsmState<S> lastSate, FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws InterruptedException, ActionException {
        FsmProcessor<S> processor = (FsmProcessor<S>) ctx.getProcessor();
        try {
            processor.plugin().pre(ctx);
            Object actionResult = action.remoteFsmQuery(ctx);
//            Assert.notNull(actionResult, "queryResult is null");
            S nextState = router.router(ctx, actionResult);
            ctx.setState(new FsmState<>(nextState, FsmState.Offset.task));
            processor.plugin().post(lastSate,   ctx);
        } catch (NoSuchRecordException e) {
            processor.plugin().error(lastSate, e, ctx);
            try {
                processor.unidempotent(ctx);
            } catch (IdempotentException ex) {
                Logs.error.error("", e);
            }
        } catch (ProcessingException e) {
            processor.plugin().error(lastSate, e, ctx);
            throw new InterruptedException(e);
        } catch (ActionException e) {
            processor.plugin().error(lastSate, e, ctx);
            throw e;
        } catch (Exception e) {
            processor.plugin().error(lastSate, e, ctx);
            throw e;
        } finally {
            processor.plugin()._finally(lastSate, ctx);
            processor.metricsNode(ctx);
        }

    }

    private void remoteFsm(FsmState<S> lastSate, FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws ActionException, IdempotentException {
        FsmProcessor<S> processor = (FsmProcessor<S>) ctx.getProcessor();
        processor.plugin().pre(ctx);
        //设置容错
        ctx.getState().setOffset(FsmState.Offset.failover);
        processor.updateStatus(ctx);
        //冥等
//        processor.idempotent(ctx);
        //执行业务
        try {
            action.remoteFsm(ctx);
            processor.plugin().post(lastSate, ctx);
        } catch (Exception e) {
            processor.plugin().error(lastSate, e, ctx);
            throw e;
        } finally {
            processor.plugin()._finally(lastSate, ctx);
            processor.metricsNode(ctx);
        }
    }
}
