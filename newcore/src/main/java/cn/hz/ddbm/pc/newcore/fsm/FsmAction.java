package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;

public interface FsmAction<S extends Enum<S>> extends Action {
    void execute(FsmContext<S> ctx) throws Exception;

    Object executeQuery(FsmContext<S> ctx) throws Exception;

}
