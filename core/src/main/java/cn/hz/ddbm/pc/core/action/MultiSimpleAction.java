package cn.hz.ddbm.pc.core.action;


import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FlowContext;

import java.util.List;

/**
 * 支持aAction，bActioin，cAction.....组合成一个Action运行的写法。
 **/


public class MultiSimpleAction implements Action, Action.QueryAction, Action.SagaAction {
    String       actionNames;
    List<Action> actions;

    public MultiSimpleAction(String actionNames, List<Action> actions) {
        this.actionNames = actionNames;
        this.actions     = actions;
    }

    @Override
    public String beanName() {
        return actionNames;
    }

    @Override
    public void execute(FlowContext ctx) throws Exception {
        for (Action action : this.actions) {
            action.execute(ctx);
        }
    }


    @Override
    public Enum query(FlowContext ctx) throws Exception {
        return null;
    }

    @Override
    public Enum getExecuteResult(FlowContext ctx) {
        return null;
    }
}
