package cn.hz.ddbm.pc.newcore.saga.action;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public interface LocalSagaAction<S extends Enum<S>> extends Action {
    void execute(SagaContext<S> ctx) throws Exception;

    void rollback(SagaContext<S> ctx)throws Exception;


}
