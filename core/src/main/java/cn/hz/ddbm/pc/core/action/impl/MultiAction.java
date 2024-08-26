package cn.hz.ddbm.pc.core.action.impl;


import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.CommandAction;
import cn.hz.ddbm.pc.core.action.QueryAction;
import cn.hz.ddbm.pc.core.action.SagaAction;

import java.util.List;

/**
 * 支持aAction，bActioin，cAction.....组合成一个Action运行的写法。
 **/


public abstract class MultiAction implements SagaAction {
    String actionNames;
    Enum   failover;

    public MultiAction(String actionNames, Enum failover) {
        this.actionNames = actionNames;
        this.failover    = failover;
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
