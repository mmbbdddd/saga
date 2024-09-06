package cn.hz.ddbm.pc.chaos.actions;

import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import org.springframework.stereotype.Component;

@Component
public class TestSagaAction implements RemoteSagaAction {
    @Override
    public String code() {
        return "sagaAction";
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
