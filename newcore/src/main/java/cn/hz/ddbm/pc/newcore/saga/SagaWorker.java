package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.Worker;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaAction;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaActionProxy;
import cn.hz.ddbm.pc.newcore.saga.worker.SagaLocalWorker;
import cn.hz.ddbm.pc.newcore.saga.worker.SagaRemoteWorker;
import lombok.Getter;

@Getter
public abstract class SagaWorker<S extends Enum<S>> extends Worker<SagaAction, SagaContext<S>> {
    protected Integer     index;
    protected S           state;
    protected SagaFlow<S> flow;

    public SagaWorker(Integer index,S state, SagaFlow<S> flow) {
        this.index = index;
        this.flow  = flow;
        this.state  = state;
    }

    public static <S extends Enum<S>> SagaWorker<S> of(Integer index, Pair<S, Class<? extends SagaAction>> pair, SagaFlow<S> flow) {
        if (LocalSagaAction.class.isAssignableFrom(pair.getValue())) {
            return local(index, pair, flow);
        } else {
            return remote(index, pair, flow);
        }
    }

    public static <S extends Enum<S>> SagaLocalWorker<S> local(Integer index, Pair<S, Class<? extends SagaAction>> node, SagaFlow<S> flow) {
        return new SagaLocalWorker<>(index, node, flow);
    }

    public static <S extends Enum<S>> SagaRemoteWorker<S> remote(Integer index, Pair<S, Class<? extends SagaAction>> node, SagaFlow<S> flow) {
        return new SagaRemoteWorker<>(index, node, flow);
    }


    @Override
    public abstract void execute(SagaContext<S> ctx) throws IdempotentException, ActionException, LockException, FlowEndException, NoSuchRecordException;


}






