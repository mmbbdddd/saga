package cn.hz.ddbm.pc.newcore.fsm.worker;

import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmProcessor;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmActionProxy;
import cn.hz.ddbm.pc.newcore.fsm.router.LocalRouter;

import java.util.Objects;

public class FsmLocalWorker<S extends Enum<S>> extends FsmWorker<S> {
    LocalFsmActionProxy<S> action;
    LocalRouter<S>         router;

    public FsmLocalWorker( Class<? extends LocalFsmAction> action, LocalRouter<S> router) {
        this.action = new LocalFsmActionProxy<>(action);
        this.router = router;
    }

    public void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException, LockException, PauseException, FlowEndException, InterruptedException, ProcessingException, NoSuchRecordException {
        ctx.setAction(action);
        //如果任务可执行
        //如果任务可执行
        FsmState<S>     lastSate = ctx.getState();
        FsmState.Offset offset   = ctx.getState().getOffset();
        switch (offset) {
            case task:
            case taskRetry:
                doFsmActionWithTranscational(lastSate, ctx);
                break;
            case failover:
                break;
        }
    }

    private void doFsmActionWithTranscational(FsmState<S> lastSate, FsmContext<S> ctx) throws ActionException, IdempotentException {
        FsmProcessor<S> processor = (FsmProcessor<S>) ctx.getProcessor();
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
