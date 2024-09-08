package cn.hz.ddbm.pc.newcore.saga.action;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;

public interface LocalSagaAction<S extends Enum<S>> extends SagaAction {
    void localSaga(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws Exception;

    void localSagaRollback(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws Exception;

}
