package cn.hz.ddbm.pc.newcore.saga.actions;


import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;

public class RemoteSagaActionProxy {
    RemoteSagaAction action;

    public RemoteSagaActionProxy(Class<? extends SagaAction> actionType) {
        this.action = (RemoteSagaAction) SpringUtil.getBean(actionType);
    }

    public void doRemoteSaga(FlowContext<SagaState> ctx) {
        action.doRemoteSaga(ctx);
    }

    public SagaWorker.Offset remoteSagaQuery(FlowContext<SagaState> ctx) {
        return action.remoteSagaQuery(ctx);
    }

    public void doRemoteSagaRollback(FlowContext<SagaState> ctx) {
        action.doRemoteSagaRollback(ctx);
    }

    public SagaWorker.Offset remoteSagaRollbackQuery(FlowContext<SagaState> ctx) {
        return action.remoteSagaRollbackQuery(ctx);
    }
}
