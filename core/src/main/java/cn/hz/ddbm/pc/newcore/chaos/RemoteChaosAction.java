package cn.hz.ddbm.pc.newcore.chaos;

import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.actions.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;
import cn.hz.ddbm.pc.newcore.saga.actions.LocalSagaAction;
import cn.hz.ddbm.pc.newcore.saga.actions.RemoteSagaAction;

public class RemoteChaosAction<S extends Enum<S>> implements RemoteFsmAction<S>, RemoteSagaAction {


    @Override
    public void doRemoteSaga(SagaContext ctx) {

    }

    @Override
    public SagaWorker.Offset remoteSagaQuery(SagaContext ctx) {
        return null;
    }

    @Override
    public void doRemoteSagaRollback(SagaContext ctx) {

    }

    @Override
    public SagaWorker.Offset remoteSagaRollbackQuery(SagaContext ctx) {
        return null;
    }


    @Override
    public void remoteFsm(FsmContext<S> ctx) throws Exception {

    }

    @Override
    public Object remoteFsmQuery(FsmContext<S> ctx) throws Exception {
        return null;
    }
}
