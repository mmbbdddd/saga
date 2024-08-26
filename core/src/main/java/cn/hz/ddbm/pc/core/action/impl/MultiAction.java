package cn.hz.ddbm.pc.core.action.impl;


import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.QueryAction;
import cn.hz.ddbm.pc.core.action.SagaAction;

import java.util.List;

/**
 * 支持aAction，bActioin，cAction.....组合成一个Action运行的写法。
 **/


public abstract class MultiAction implements QueryAction, SagaAction {
    String            actionNames;
    List<QueryAction> queryActions;
    List<SagaAction>  commandActions;
    Enum              failover;

    public MultiAction(String actionNames, Enum failover, List<Action> actions) {
        this.actionNames    = actionNames;
        this.queryActions   = null;
        this.commandActions = null;
        this.failover       = failover;
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
