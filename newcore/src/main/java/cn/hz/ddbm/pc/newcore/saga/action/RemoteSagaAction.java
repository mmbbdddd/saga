package cn.hz.ddbm.pc.newcore.saga.action;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public interface RemoteSagaAction<S extends Enum<S>> extends SagaAction {
    void execute(SagaContext<S> ctx) throws Exception;

    Boolean executeQuery(SagaContext<S> ctx) throws Exception;

    void rollback(SagaContext<S> ctx) throws Exception;

    Boolean rollbackQuery(SagaContext<S> ctx) throws Exception;


}
