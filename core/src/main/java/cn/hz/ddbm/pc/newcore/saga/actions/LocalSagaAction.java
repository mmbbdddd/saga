package cn.hz.ddbm.pc.newcore.saga.actions;


import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public interface LocalSagaAction extends SagaAction {
    void doSagaRollback(SagaContext ctx);

    void doSaga(SagaContext ctx);
}
