package cn.hz.ddbm.pc.newcore.chaos;

import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.actions.LocalSagaAction;

public class LocalChaosAction implements LocalFsmAction, LocalSagaAction {
    @Override
    public Object doLocalFsm(FsmContext ctx) throws Exception {
        return null;
    }

    @Override
    public void doLocalSagaRollback(SagaContext ctx) {

    }

    @Override
    public void doLocalSaga(SagaContext ctx) {

    }
}
