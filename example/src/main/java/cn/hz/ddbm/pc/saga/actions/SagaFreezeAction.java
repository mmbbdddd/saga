package cn.hz.ddbm.pc.saga.actions;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaAction;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;
import cn.hz.ddbm.pc.saga.PayTest;

public class SagaFreezeAction implements LocalSagaAction {
    @Override
    public String code() {
        return "sagaFreezeAction";
    }

    @Override
    public void localSaga(FlowContext ctx) throws Exception {
        PayTest.account.decrementAndGet();
        PayTest.freezed.incrementAndGet();
    }

    @Override
    public void localSagaRollback(FlowContext ctx) throws Exception {
        PayTest.account.incrementAndGet();
        PayTest.freezed.decrementAndGet();
    }
}
