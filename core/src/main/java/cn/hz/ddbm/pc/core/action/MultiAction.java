package cn.hz.ddbm.pc.core.action;


import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FsmContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 支持aAction，bActioin，cAction.....组合成一个Action运行的写法。
 **/


public abstract class MultiAction<S extends Enum<S>> implements Action<S>, Action.QueryAction<S>, Action.SagaAction<S> {
    String               actionNames;
    List<QueryAction<S>> queryActions;
    List<SagaAction<S>>  sagaActions;
    List<Action<S>>      otherActions;

    public MultiAction(String actionNames, List<Action<S>> actions) {
        this.actionNames  = actionNames;
        this.queryActions = null;
        this.sagaActions  = null;
        this.otherActions = null;
    }

    @Override
    public String beanName() {
        return actionNames;
    }


}
