package cn.hz.ddbm.pc.newcore.saga.action;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;

public interface RemoteSagaAction<S extends Enum<S>> extends SagaAction {
    void remoteSaga(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws Exception;

    Boolean remoteSagaQuery(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws Exception;

    void remoteSagaRollback(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws Exception;

    Boolean remoteSagaRollbackFailover(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws Exception;


}
