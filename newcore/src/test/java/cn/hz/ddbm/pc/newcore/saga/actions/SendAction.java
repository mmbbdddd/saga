package cn.hz.ddbm.pc.newcore.saga.actions;

import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public class SendAction  implements RemoteSagaAction {
    @Override
    public String code() {
        return null;
    }

    @Override
    public void execute(SagaContext<?> ctx) throws ActionException {

    }

    @Override
    public Boolean executeQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException {
        return null;
    }

    @Override
    public void rollback(SagaContext<?> ctx) throws ActionException {

    }

    @Override
    public Boolean rollbackQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException {
        return null;
    }
}
