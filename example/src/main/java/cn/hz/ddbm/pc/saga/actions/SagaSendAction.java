package cn.hz.ddbm.pc.saga.actions;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;

public class SagaSendAction implements RemoteSagaAction {
    @Override
    public String code() {
        return "sagaSendAction";
    }

    @Override
    public void remoteSaga(FlowContext ctx) throws Exception {

    }

    @Override
    public Boolean remoteSagaQuery(FlowContext ctx) throws Exception {
        Boolean result =  Math.random()>0.5;


        return result;
    }

    @Override
    public void remoteSagaRollback(FlowContext ctx) throws Exception {

    }

    @Override
    public Boolean remoteSagaRollbackFailover(FlowContext ctx) throws Exception {
        Boolean result =  Math.random()>0.5;

        return result;
    }
}
