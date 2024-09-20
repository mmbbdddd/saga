package cn.hz.ddbm.pc.newcore.saga.actions;


import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;

public interface RemoteSagaAction extends SagaAction {
    void doSaga(SagaContext ctx);

    SagaWorker.Offset querySaga(SagaContext ctx);

    void doSagaRollback(SagaContext ctx);

    SagaWorker.Offset querySagaRollback(SagaContext ctx);
}
