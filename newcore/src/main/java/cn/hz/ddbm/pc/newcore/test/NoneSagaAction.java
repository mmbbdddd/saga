package cn.hz.ddbm.pc.newcore.test;

import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public class NoneSagaAction implements RemoteSagaAction {


    @Override
    public String code() {
        return "none";
    }

    @Override
    public void execute(SagaContext ctx) throws Exception {

    }

    @Override
    public Boolean executeQuery(SagaContext ctx) throws Exception {
        return null;
    }

    @Override
    public void rollback(SagaContext ctx) throws Exception {

    }

    @Override
    public Boolean rollbackQuery(SagaContext ctx) throws Exception {
        return null;
    }
}
