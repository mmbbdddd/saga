package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;

public interface SagaAction extends Action {
    void execute(SagaContext<?> ctx);

    Boolean executeQuery(SagaContext<?> ctx) throws NoSuchRecordException;

    void rollback(SagaContext<?> ctx);

    Boolean rollbackQuery(SagaContext<?> ctx) throws NoSuchRecordException;
}
