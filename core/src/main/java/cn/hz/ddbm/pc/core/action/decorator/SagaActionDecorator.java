package cn.hz.ddbm.pc.core.action.decorator;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.SagaAction;

public class SagaActionDecorator implements SagaAction {
    String     beanName;
    Enum       failover;
    SagaAction sagaAction;

    public SagaActionDecorator(String beanName, Enum failover, SagaAction sagaAction) {
        this.beanName   = beanName;
        this.failover   = failover;
        this.sagaAction = sagaAction;
    }

    @Override
    public String beanName() {
        return beanName;
    }

    @Override
    public void execute(FsmContext ctx) throws Exception {
        sagaAction.execute(ctx);
    }

    @Override
    public Enum query(FsmContext ctx) throws Exception {
        return sagaAction.query(ctx);
    }

}
