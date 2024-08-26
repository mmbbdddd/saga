package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.FsmContext;

public interface CommandAction<S extends Enum<S>> extends Action<S> {

    void execute(FsmContext<S, ?> ctx) throws Exception;

}