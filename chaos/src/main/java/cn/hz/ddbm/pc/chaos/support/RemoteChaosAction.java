package cn.hz.ddbm.pc.chaos.support;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import cn.hz.ddbm.pc.newcore.saga.SagaWorker;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;

import javax.annotation.Resource;

public class RemoteChaosAction<S extends Enum<S>> implements RemoteSagaAction<S>, RemoteFsmAction<S> {
    @Resource
    ChaosHandler chaosHandler;

    @Override
    public String code() {
        return "remoteChaosAction";
    }


    @Override
    public void remoteFsm(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws Exception {

    }

    @Override
    public Object remoteFsmQuery(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws Exception {
        return null;
    }

    @Override
    public void remoteSaga(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws Exception {

    }

    @Override
    public Boolean remoteSagaQuery(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws Exception {
        return chaosHandler.sagaRouter();
    }

    @Override
    public void remoteSagaRollback(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws Exception {

    }

    @Override
    public Boolean remoteSagaRollbackFailover(FlowContext<SagaFlow<S>, SagaState<S>, SagaWorker<S>> ctx) throws Exception {
        return chaosHandler.sagaRouter();
    }
}
