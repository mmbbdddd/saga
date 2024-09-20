package cn.hz.ddbm.pc.newcore.chaos;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.actions.LocalSagaAction;

public class LocalChaosAction implements LocalFsmAction, LocalSagaAction {

    @Override
    public   Object doLocalFsm(FlowContext<FsmState > ctx) throws Exception {
        return null;
    }

    @Override
    public void doLocalSagaRollback(FlowContext<SagaState> ctx) {

    }

    @Override
    public void doLocalSaga(FlowContext<SagaState> ctx) {

    }
}
