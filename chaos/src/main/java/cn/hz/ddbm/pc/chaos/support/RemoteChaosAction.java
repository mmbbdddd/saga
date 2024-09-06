package cn.hz.ddbm.pc.chaos.support;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;

import javax.annotation.Resource;

public class RemoteChaosAction implements RemoteSagaAction, RemoteFsmAction {
    @Resource
    ChaosHandlerImpl chaosHandler;
    @Override
    public String code() {
        return "remoteChaosAction";
    }

    @Override
    public void execute(FsmContext ctx) throws Exception {

    }

    @Override
    public Object executeQuery(FsmContext ctx) throws Exception {
        return chaosHandler.getActionResult(ctx);
    }

    @Override
    public void execute(SagaContext ctx) throws Exception {

    }

    @Override
    public Boolean executeQuery(SagaContext ctx) throws Exception {
        return chaosHandler.getActionResult(ctx);
    }

    @Override
    public void rollback(SagaContext ctx) throws Exception {

    }

    @Override
    public Boolean rollbackQuery(SagaContext ctx) throws Exception {
        return chaosHandler.getActionResult(ctx);
    }


}
