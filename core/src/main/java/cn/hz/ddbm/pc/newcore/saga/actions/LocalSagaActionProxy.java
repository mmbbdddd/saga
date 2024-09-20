package cn.hz.ddbm.pc.newcore.saga.actions;


import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public class LocalSagaActionProxy {
    LocalSagaAction action;
    public LocalSagaActionProxy(Class<? extends SagaAction> actionType) {
        this.action = (LocalSagaAction) SpringUtil.getBean(actionType);
    }

    public void doLocalSaga(SagaContext ctx) {
        this.action.doLocalSaga(ctx);
    }

    public void doLocalSagaRollback(SagaContext ctx) {
        this.action.doLocalSagaRollback(ctx);
    }
}
