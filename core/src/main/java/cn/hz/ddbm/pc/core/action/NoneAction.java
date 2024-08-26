package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.QueryAction;
import cn.hz.ddbm.pc.core.action.SagaAction;

public class NoneAction implements QueryAction, SagaAction {
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
        //todo
        return null;
    }

    @Override
    public Enum failover() {
        //todo
        return null;
    }
}
