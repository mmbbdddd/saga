package cn.hz.ddbm.pc.newcore.saga.actions;


import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;

public class RemoteSagaActionProxy {
    RemoteSagaAction action;

    public RemoteSagaActionProxy(Class<? extends SagaAction> actionType) {
        this.action = (RemoteSagaAction) SpringUtil.getBean(actionType);
    }

    public void doRemoteSaga(SagaContext ctx) {
        action.doRemoteSaga(ctx);
    }

    public SagaWorker.Offset remoteSagaQuery(SagaContext ctx) {
        return action.remoteSagaQuery(ctx);
    }

    public void doRemoteSagaRollback(SagaContext ctx) {
        action.doRemoteSagaRollback(ctx);
    }

    public SagaWorker.Offset remoteSagaRollbackQuery(SagaContext ctx) {
        return action.remoteSagaRollbackQuery(ctx);
    }
}
