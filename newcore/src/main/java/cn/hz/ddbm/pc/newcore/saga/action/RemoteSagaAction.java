package cn.hz.ddbm.pc.newcore.saga.action;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public interface RemoteSagaAction<S extends Enum<S>> extends Action {
    void execute(SagaContext<S> ctx) throws Exception;

    Boolean executeQuery(SagaContext<S> ctx) throws Exception;

    void rollback(SagaContext<S> ctx) throws Exception;

    Boolean rollbackQuery(SagaContext<S> ctx) throws Exception;
}
