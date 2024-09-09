package cn.hz.ddbm.pc.newcore.fsm.worker;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.fsm.*;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmActionProxy;

public class FsmLocalWorker<S extends Enum<S>> extends FsmWorker<S> {
    LocalFsmActionProxy<S> action;
    Router<S>         router;

    public FsmLocalWorker(Class<? extends LocalFsmAction> action, Router<S> router) {
        this.action = new LocalFsmActionProxy<>(action);
        this.router = router;
    }

    public void execute(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws StatusException, IdempotentException, ActionException, LockException, PauseException, FlowEndException, InterruptedException, ProcessingException, NoSuchRecordException {
        ctx.setAction(action);
        //如果任务可执行
        //如果任务可执行
        FsmState<S>     lastSate = ctx.getState();
        FsmState.Offset offset   = ctx.getState().getOffset();
        switch (offset) {
            case task:
            case taskRetry:
                localFsm(lastSate, ctx);
                break;
            case failover:
                break;
        }
    }

    private void localFsm(FsmState<S> lastSate, FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws ActionException, IdempotentException {
        FsmProcessor<S> processor = (FsmProcessor<S>) ctx.getProcessor();
        //执行业务
        try {
            processor.plugin().pre(ctx);
            Object result = action.localFsm(ctx);
            S      state  = router.router(ctx, result);
            ctx.setState(new FsmState<>(state, FsmState.Offset.task));
            processor.updateStatus(ctx);
            processor.plugin().post(lastSate,ctx);
        } catch (NoSuchRecordException e) {
            processor.plugin().error(lastSate,e,ctx);
        }catch (ActionException e) {
            processor.plugin().error(lastSate,e,ctx);
            throw e;
        } catch (ProcessingException e) {
            processor.plugin().error(lastSate,e,ctx);
        } finally {
            processor.metricsNode(ctx);
        }
    }

}
