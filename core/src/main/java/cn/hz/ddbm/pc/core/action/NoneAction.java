package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FsmContext;

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
    public void execute(FsmContext ctx) throws Exception {

    }

    @Override
    public Enum query(FsmContext ctx) throws Exception {
        return null;
    }

}
