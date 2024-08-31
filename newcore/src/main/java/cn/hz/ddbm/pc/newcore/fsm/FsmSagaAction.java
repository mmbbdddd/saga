package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;

import java.io.Serializable;

public interface FsmSagaAction<S extends Serializable> extends Action {
    void execute(FsmContext<S> ctx) throws ActionException;

    S executeQuery(FsmContext<S> ctx) throws NoSuchRecordException, ActionException;
}
