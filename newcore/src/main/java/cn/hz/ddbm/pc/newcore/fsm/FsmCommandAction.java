package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.exception.ActionException;

import java.io.Serializable;

public interface FsmCommandAction<S extends Serializable> extends Action {
    void command(FsmContext<S> ctx) throws ActionException;
}
