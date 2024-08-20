package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FlowContext;

public class NoneAction implements Action, Action.QueryAction, Action.SagaAction {
    String actionDsl;

    public NoneAction(String actionDsl) {
        this.actionDsl = actionDsl;
    }

    @Override
    public String beanName() {
        return actionDsl;
    }

    @Override
    public void execute(FlowContext ctx) throws Exception {

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
