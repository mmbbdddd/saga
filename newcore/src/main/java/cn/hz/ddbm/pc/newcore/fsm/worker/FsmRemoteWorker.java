package cn.hz.ddbm.pc.newcore.fsm.worker;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmProcessor;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmActionProxy;
import cn.hz.ddbm.pc.newcore.fsm.router.RemoteRouter;
import cn.hz.ddbm.pc.newcore.log.Logs;

public class FsmRemoteWorker<S extends Enum<S>> extends FsmWorker<S> {
    RemoteFsmActionProxy<S> action;
    RemoteRouter<S>         router;

    public FsmRemoteWorker(Class<? extends RemoteFsmAction> action, RemoteRouter<S> router) {
        this.action = new RemoteFsmActionProxy<>(action);
        this.router = router;
    }

    public void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException, LockException, PauseException, FlowEndException, InterruptedException, ProcessingException, NoSuchRecordException {
        ctx.setAction(action);
        //如果任务可执行
        FsmState<S>     lastSate = ctx.getState();
        FsmState.Offset offset   = ctx.getState().getOffset();
        switch (offset) {
            case task:
            case taskRetry:
                doFsmAction(lastSate, ctx);
                break;
            case failover:
                doFsmActionFailover(lastSate, ctx);
                break;
        }
    }

    private void doFsmActionFailover(FsmState<S> lastSate, FsmContext<S> ctx) throws InterruptedException, ActionException {
        FsmProcessor<S> processor = (FsmProcessor<S>) ctx.getProcessor();
        try {
            Object actionResult = action.executeQuery(ctx);
            Assert.notNull(actionResult, "queryResult is null");
            S nextState = router.router(ctx, actionResult);
            ctx.setState(new FsmState<>(nextState));
        } catch (NoSuchRecordException e) {
            try {
                processor.unidempotent(ctx);
            } catch (IdempotentException ex) {
                Logs.error.error("", e);
            }
        } catch (ProcessingException e) {
            Logs.flow.info("", e);
            throw new InterruptedException(e);
        } catch (ActionException e) {
            throw e;
        } finally {
            processor.metricsNode(ctx);
        }

    }

    private void doFsmAction(FsmState<S> lastSate, FsmContext<S> ctx) throws ActionException, IdempotentException {
        FsmProcessor<S> processor = (FsmProcessor<S>) ctx.getProcessor();
        //设置容错
        ctx.getState().setOffset(FsmState.Offset.failover);
        processor.updateStatus(ctx);
        //冥等
        processor.idempotent(ctx);
        //执行业务
        try {
            action.execute(ctx);
        } finally {
            processor.metricsNode(ctx);
        }
    }
}
