package cn.hz.ddbm.pc.newcore.saga.actions;


import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public class LocalSagaActionProxy {
    LocalSagaAction action;
    public LocalSagaActionProxy(Class<? extends SagaAction> actionType) {
        this.action = (LocalSagaAction) SpringUtil.getBean(actionType);
    }

    public void doSaga(SagaContext ctx) {
        this.action.doSaga(ctx);
    }

    public void doSagaRollback(SagaContext ctx) {
        this.action.doSagaRollback(ctx);
    }
}
