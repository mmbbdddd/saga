package cn.hz.ddbm.pc.newcore.saga.actions;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;

public class PayFreezedAction implements RemoteSagaAction {
    @Override
    public String code() {
        return null;
    }

    @Override
    public void remoteSaga(FlowContext ctx) throws Exception {

    }

    @Override
    public Boolean remoteSagaQuery(FlowContext ctx) throws Exception {
        return null;
    }

    @Override
    public void remoteSagaRollback(FlowContext ctx) throws Exception {

    }

    @Override
    public Boolean remoteSagaRollbackFailover(FlowContext ctx) throws Exception {
        return null;
    }
}
