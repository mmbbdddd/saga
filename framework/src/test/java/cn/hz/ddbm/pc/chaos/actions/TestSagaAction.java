package cn.hz.ddbm.pc.chaos.actions;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;
import org.springframework.stereotype.Component;

@Component
public class TestSagaAction implements RemoteSagaAction {
    @Override
    public String code() {
        return "sagaAction";
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
