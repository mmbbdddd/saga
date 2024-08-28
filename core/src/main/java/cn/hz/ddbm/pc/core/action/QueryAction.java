package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.FsmContext;

public interface QueryAction<S extends Enum<S>> extends Action<S> {
    S query(FsmContext<S, ?> ctx) throws Exception;


}