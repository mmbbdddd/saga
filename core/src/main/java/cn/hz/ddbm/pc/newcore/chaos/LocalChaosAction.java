package cn.hz.ddbm.pc.newcore.chaos;

import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.actions.LocalSagaAction;

public class LocalChaosAction<S extends Enum<S>> implements LocalFsmAction<S>, LocalSagaAction {

    @Override
    public Object doLocalFsm(FsmContext<S> ctx) throws Exception {
        return null;
    }

    @Override
    public void doLocalSagaRollback(SagaContext ctx) {

    }

    @Override
    public void doLocalSaga(SagaContext ctx) {

    }
}
