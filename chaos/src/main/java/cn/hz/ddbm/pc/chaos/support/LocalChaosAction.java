package cn.hz.ddbm.pc.chaos.support;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaAction;

import javax.annotation.Resource;

public class LocalChaosAction<S extends Enum<S>> implements LocalFsmAction<S>, LocalSagaAction<S> {
    @Resource
    ChaosHandler chaosHandler;

    @Override
    public String code() {
        return "localChaosAction";
    }


    @Override
    public Object localFsm(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws Exception {
        return null;
    }

    @Override
    public void localSaga(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws Exception {

    }

    @Override
    public void localSagaRollback(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws Exception {

    }
}
