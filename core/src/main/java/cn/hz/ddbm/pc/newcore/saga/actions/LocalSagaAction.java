package cn.hz.ddbm.pc.newcore.saga.actions;


import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaState;

public interface LocalSagaAction extends SagaAction {
    void doLocalSagaRollback(FlowContext<SagaState> ctx);

    void doLocalSaga(FlowContext<SagaState> ctx);
}
