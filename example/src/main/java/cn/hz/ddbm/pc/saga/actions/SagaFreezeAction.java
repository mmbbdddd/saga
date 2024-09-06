package cn.hz.ddbm.pc.saga.actions;

import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public class SagaFreezeAction implements RemoteSagaAction {
    @Override
    public String code() {
        return "sagaFreezeAction";
    }

    @Override
    public void execute(SagaContext<?> ctx) throws Exception {

    }

    @Override
    public Boolean executeQuery(SagaContext<?> ctx) throws Exception {
        return null;
    }

    @Override
    public void rollback(SagaContext<?> ctx) throws Exception {

    }

    @Override
    public Boolean rollbackQuery(SagaContext<?> ctx) throws Exception {
        return null;
    }
}
