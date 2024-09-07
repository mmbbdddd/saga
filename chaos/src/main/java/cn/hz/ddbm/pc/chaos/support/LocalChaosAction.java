package cn.hz.ddbm.pc.chaos.support;

import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaAction;

import javax.annotation.Resource;

public class LocalChaosAction implements LocalFsmAction, LocalSagaAction {
    @Resource
    ChaosHandler chaosHandler;

    @Override
    public String code() {
        return "localChaosAction";
    }

    @Override
    public Object execute(FsmContext ctx) throws Exception {
        return null;
    }



    @Override
    public void execute(SagaContext ctx) throws Exception {

    }

    @Override
    public void rollback(SagaContext ctx) throws Exception {

    }
}
