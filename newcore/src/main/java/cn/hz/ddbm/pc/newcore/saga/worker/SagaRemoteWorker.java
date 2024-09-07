package cn.hz.ddbm.pc.newcore.saga.worker;

import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaActionProxy;

public class SagaRemoteWorker<S extends Enum<S>> extends SagaWorker<S> {
    RemoteForward<S>      forward;
    RemoteBackoff<S>      backoff;
    RemoteSagaActionProxy action;

    public SagaRemoteWorker(int index, S pre, S current, S next, Class action) {
        super(index, current);
        this.forward = new RemoteForward<>(pre, current, next);
        this.backoff = new RemoteBackoff<>(pre, current);
        this.action  = new RemoteSagaActionProxy(action);
    }


    public void execute(SagaContext<S> ctx) throws IdempotentException, ActionException, LockException, FlowEndException, NoSuchRecordException {
        ctx.setAction(action);
        if (ctx.getState().getDirection().isForward()) {
            forward.execute(ctx);
        } else {
            backoff.execute(ctx);
        }
    }
}
