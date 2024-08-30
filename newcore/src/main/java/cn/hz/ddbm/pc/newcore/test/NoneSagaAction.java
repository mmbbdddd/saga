package cn.hz.ddbm.pc.newcore.test;

import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public class NoneSagaAction implements SagaAction {
    @Override
    public void execute(SagaContext<?> ctx) {
        Logs.flow.info("{}", ctx.getWorker());
    }

    @Override
    public Boolean executeQuery(SagaContext<?> ctx) throws NoSuchRecordException {
        return true;
    }

    @Override
    public void rollback(SagaContext<?> ctx) {

    }

    @Override
    public Boolean rollbackQuery(SagaContext<?> ctx) throws NoSuchRecordException {
        return true;
    }

    @Override
    public String code() {
        return "none";
    }
}
