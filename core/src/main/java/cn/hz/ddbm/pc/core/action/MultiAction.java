package cn.hz.ddbm.pc.core.action;


import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FsmContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 支持aAction，bActioin，cAction.....组合成一个Action运行的写法。
 **/


public abstract class MultiAction implements Action, Action.QueryAction, Action.SagaAction {
    String            actionNames;
    List<QueryAction> queryActions;
    List<Action>      dealActions;
    Enum              failover;

    public MultiAction(String actionNames, Enum failover, List<Action> actions) {
        this.actionNames  = actionNames;
        this.queryActions = null;
        this.dealActions  = null;
        this.failover     = failover;
    }

    @Override
    public String beanName() {
        return actionNames;
    }

    @Override
    public Enum failover() {
        return failover;
    }
}
