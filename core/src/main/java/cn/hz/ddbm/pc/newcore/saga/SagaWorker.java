package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Worker;
import cn.hz.ddbm.pc.newcore.exception.*;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaAction;
import cn.hz.ddbm.pc.newcore.saga.worker.SagaLocalWorker;
import cn.hz.ddbm.pc.newcore.saga.worker.SagaRemoteWorker;
import lombok.Getter;

@Getter
public abstract class SagaWorker<S extends Enum<S>> extends Worker<SagaAction,
        FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>>> {
    protected Integer     index;
    protected S           state;

    public SagaWorker(Integer index, S state) {
        this.index = index;
        this.state = state;
    }

    public static <S extends Enum<S>> SagaWorker<S> of(Integer index, Pair<S, Class<? extends SagaAction>> pair, Integer total) {
        if (LocalSagaAction.class.isAssignableFrom(pair.getValue())) {
            return local(index, pair, total);
        } else {
            return remote(index, pair, total);
        }
    }

    public static <S extends Enum<S>> SagaLocalWorker<S> local(Integer index, Pair<S, Class<? extends SagaAction>> node, Integer total) {
        return new SagaLocalWorker<>(index, node, total);
    }

    public static <S extends Enum<S>> SagaRemoteWorker<S> remote(Integer index, Pair<S, Class<? extends SagaAction>> node,Integer total) {
        return new SagaRemoteWorker<>(index, node,total);
    }

    public static <S extends Enum<S>> Pair<S, SagaWorker<S>> failWorker() {
        return null;
    }

    public static <S extends Enum<S>> Pair<S, SagaWorker<S>> successWorker() {
        return null;
    }


    @Override
    public abstract void execute(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws IdempotentException, ActionException, LockException, FlowEndException, NoSuchRecordException;


}






