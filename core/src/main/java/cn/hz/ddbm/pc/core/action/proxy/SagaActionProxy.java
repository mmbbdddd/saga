package cn.hz.ddbm.pc.core.action.proxy;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.SagaAction;

public class SagaActionProxy implements SagaAction {
    SagaAction sagaAction;

    public SagaActionProxy(SagaAction sagaAction) {
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
    public Enum queryState(FsmContext ctx) throws Exception {
        return sagaAction.queryState(ctx);
    }

    @Override
    public Boolean executeWhen(FsmContext ctx) throws Exception {
        return sagaAction.executeWhen(ctx);
    }
}
