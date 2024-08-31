package cn.hz.ddbm.pc.newcore.fsm;


import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;

import java.io.Serializable;

public class FsmSagaActionProxy<S extends Serializable> implements FsmSagaAction<S> {

    String           action;
    FsmSagaAction<S> sagaAction;

    public FsmSagaActionProxy(String action) {
        this.action = action;
    }

    @Override
    public String code() {
        return sagaAction.code();
    }

    @Override
    public void execute(FsmContext<S> ctx) throws ActionException {
        try {
            sagaAction.execute(ctx);
        } catch (Exception e) {
            throw new ActionException(e);
        }
    }

    @Override
    public S executeQuery(FsmContext<S> ctx) throws NoSuchRecordException, ActionException {
        try {
            return sagaAction.executeQuery(ctx);
        } catch (Exception e) {
            throw new ActionException(e);
        }
    }

    public Action getOrInitAction() {
        if (null == sagaAction) {
            synchronized (this) {
//                this.sagaAction = getBean(action, SagaAction.class);
                this.sagaAction = new NoneFsmAction();
            }
        }
        return this;
    }
}
