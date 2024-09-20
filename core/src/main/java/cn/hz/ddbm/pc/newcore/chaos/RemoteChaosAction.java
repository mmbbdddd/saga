package cn.hz.ddbm.pc.newcore.chaos;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.actions.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;
import cn.hz.ddbm.pc.newcore.saga.actions.LocalSagaAction;
import cn.hz.ddbm.pc.newcore.saga.actions.RemoteSagaAction;

public class RemoteChaosAction  implements RemoteFsmAction , RemoteSagaAction {


    @Override
    public void doRemoteSaga(FlowContext<SagaState> ctx) {

    }

    @Override
    public SagaWorker.Offset remoteSagaQuery(FlowContext<SagaState> ctx) {
        return null;
    }

    @Override
    public void doRemoteSagaRollback(FlowContext<SagaState> ctx) {

    }

    @Override
    public SagaWorker.Offset remoteSagaRollbackQuery(FlowContext<SagaState> ctx) {
        return null;
    }


    @Override
    public void remoteFsm(FlowContext<FsmState > ctx) throws Exception {

    }

    @Override
    public Object remoteFsmQuery(FlowContext<FsmState > ctx) throws Exception {
        return null;
    }
}
