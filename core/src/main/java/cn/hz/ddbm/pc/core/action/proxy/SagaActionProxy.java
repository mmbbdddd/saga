package cn.hz.ddbm.pc.core.action.proxy;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.NoSuchRecordException;
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
    public void exec(FsmContext ctx) throws Exception {

    }

    @Override
    public Boolean executeQuery(FsmContext ctx) throws NoSuchRecordException {
        return null;
    }

    @Override
    public void rollback(FsmContext ctx) {

    }

    @Override
    public Boolean rollbackQuery(FsmContext ctx) throws NoSuchRecordException {
        return null;
    }
}
