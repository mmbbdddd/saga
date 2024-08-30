package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;

public interface SagaAction extends Action {
    void execute(SagaContext<?> ctx) throws ActionException;

    Boolean executeQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException;

    void rollback(SagaContext<?> ctx) throws ActionException;

    Boolean rollbackQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException;
}
