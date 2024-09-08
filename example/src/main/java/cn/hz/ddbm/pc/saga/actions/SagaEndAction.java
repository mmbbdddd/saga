package cn.hz.ddbm.pc.saga.actions;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaAction;

public class SagaEndAction implements LocalSagaAction {
    @Override
    public String code() {
        return "sagaEndAction";
    }

    @Override
    public void localSaga(FlowContext ctx) throws Exception {

    }

    @Override
    public void localSagaRollback(FlowContext ctx) throws Exception {

    }
}
