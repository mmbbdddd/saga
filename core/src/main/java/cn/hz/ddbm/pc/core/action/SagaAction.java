package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.FsmContext;

public interface SagaAction<S extends Enum<S>> extends CommandAction<S>, QueryAction<S> {
    default Boolean executeCondition(FsmContext<S, ?> ctx) throws Exception {
        return executeWhen(queryState(ctx));
    }

    Boolean executeWhen(S queryResult) throws Exception;
}