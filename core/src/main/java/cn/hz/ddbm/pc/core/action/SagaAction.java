package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.FsmContext;

public interface SagaAction extends Action {
    void exec(FsmContext ctx) throws Exception;

    Boolean executeQuery(FsmContext ctx) throws NoSuchRecordException;

    void rollback(FsmContext ctx);

    Boolean rollbackQuery(FsmContext ctx) throws NoSuchRecordException;


}