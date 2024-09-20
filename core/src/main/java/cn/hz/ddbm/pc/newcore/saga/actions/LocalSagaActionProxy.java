package cn.hz.ddbm.pc.newcore.saga.actions;


import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaState;

public class LocalSagaActionProxy {
    LocalSagaAction action;
    public LocalSagaActionProxy(Class<? extends SagaAction> actionType) {
        this.action = (LocalSagaAction) SpringUtil.getBean(actionType);
    }

    public void doLocalSaga(FlowContext<SagaState> ctx) {
        this.action.doLocalSaga(ctx);
    }

    public void doLocalSagaRollback(FlowContext<SagaState> ctx) {
        this.action.doLocalSagaRollback(ctx);
    }
}
