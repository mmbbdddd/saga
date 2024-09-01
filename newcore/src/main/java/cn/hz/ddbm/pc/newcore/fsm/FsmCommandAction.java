package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.exception.ActionException;

public interface FsmCommandAction<S extends Enum<S>> extends Action {
    void command(FsmContext<S> ctx) throws ActionException;
}
