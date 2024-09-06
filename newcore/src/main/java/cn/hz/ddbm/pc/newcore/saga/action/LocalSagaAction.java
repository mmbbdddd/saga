package cn.hz.ddbm.pc.newcore.saga.action;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public interface LocalSagaAction extends Action {
    void execute(SagaContext<?> ctx) throws Exception;

    void rollback(SagaContext<?> ctx);

}
