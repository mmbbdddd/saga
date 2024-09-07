package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Worker;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.LockException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaAction;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaActionProxy;
import cn.hz.ddbm.pc.newcore.saga.worker.SagaLocalWorker;
import cn.hz.ddbm.pc.newcore.saga.worker.SagaRemoteWorker;
import lombok.Data;
import lombok.Getter;

import java.util.Objects;

@Getter
public abstract class SagaWorker<S extends Enum<S>> extends Worker<SagaContext<S>> {
    Integer index;
    S       state;

    public SagaWorker(Integer index, S state) {
        this.index = index;
        this.state = state;
    }


    public static <S extends Enum<S>> SagaLocalWorker<S> local(int i, S pre, S current, S next, Class action) {
        return new SagaLocalWorker<>(i, pre, current, next, action);
    }

    public static <S extends Enum<S>> SagaRemoteWorker<S> remote(int i, S pre, S current, S next, Class action) {
        return new SagaRemoteWorker<>(i, pre, current, next, action);
    }


    @Override
    public abstract void execute(SagaContext<S> ctx) throws IdempotentException, ActionException, LockException;

}






