package cn.hz.ddbm.pc.saga.actions;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaAction;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;
import cn.hz.ddbm.pc.saga.PayTest;

public class SagaPayAction implements LocalSagaAction {
    @Override
    public String code() {
        return "sagaPayAction";
    }

    @Override
    public void localSaga(FlowContext ctx) throws Exception {
        PayTest.freezed.decrementAndGet();
        PayTest.bank.incrementAndGet();

    }

    @Override
    public void localSagaRollback(FlowContext ctx) throws Exception {
        PayTest.freezed.incrementAndGet();
        PayTest.bank.decrementAndGet();
    }
}
