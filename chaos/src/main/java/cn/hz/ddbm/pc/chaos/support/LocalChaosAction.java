package cn.hz.ddbm.pc.chaos.support;

import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.actions.LocalSagaAction;

import javax.annotation.Resource;

public class LocalChaosAction<S extends Enum<S>> implements LocalFsmAction<S>, LocalSagaAction {
    @Resource
    ChaosHandler chaosHandler;


    @Override
    public Object localFsm(FsmContext<S> ctx) throws Exception {
        return null;
    }

    @Override
    public void doSagaRollback(SagaContext ctx) {

    }

    @Override
    public void doSaga(SagaContext ctx) {

    }
}
