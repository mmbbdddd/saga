package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.FsmContext;

public interface QueryAction<S extends Enum<S>> extends Action<S> {
    S queryState(FsmContext<S, ?> ctx) throws Exception;


}