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

    public void doSaga(SagaContext ctx) {
        action.doSaga(ctx);
    }

    public SagaWorker.Offset querySaga(SagaContext ctx) {
        return action.querySaga(ctx);
    }

    public void doSagaRollback(SagaContext ctx) {
        action.doSagaRollback(ctx);
    }

    public SagaWorker.Offset querySagaRollback(SagaContext ctx) {
        return action.querySagaRollback(ctx);
    }
}
