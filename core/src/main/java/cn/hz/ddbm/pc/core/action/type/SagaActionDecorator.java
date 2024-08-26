package cn.hz.ddbm.pc.core.action.type;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.SagaAction;

public class SagaActionDecorator implements SagaAction {
    SagaAction sagaAction;

    public SagaActionDecorator(   SagaAction sagaAction) {
        this.sagaAction = sagaAction;
    }

    @Override
    public String beanName() {
        return sagaAction.beanName();
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
