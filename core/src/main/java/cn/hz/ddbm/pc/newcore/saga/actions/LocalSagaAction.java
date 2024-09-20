package cn.hz.ddbm.pc.newcore.saga.actions;


import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public interface LocalSagaAction extends SagaAction {
    void doLocalSagaRollback(SagaContext ctx);

    void doLocalSaga(SagaContext ctx);
}
