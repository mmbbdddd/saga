package cn.hz.ddbm.pc.newcore.saga.actions;


import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;

public interface RemoteSagaAction extends SagaAction {
    void doRemoteSaga(SagaContext ctx);

    SagaWorker.Offset remoteSagaQuery(SagaContext ctx);

    void doRemoteSagaRollback(SagaContext ctx);

    SagaWorker.Offset remoteSagaRollbackQuery(SagaContext ctx);
}
