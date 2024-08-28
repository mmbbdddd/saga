package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.FsmContext;

public interface SagaAction<S extends Enum<S>> extends CommandAction<S>, QueryAction<S> {
    Boolean condition(FsmContext<S,?> ctx) throws Exception;
}