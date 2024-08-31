package cn.hz.ddbm.pc.newcore.fsm;


import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.exception.ActionException;

import java.io.Serializable;

public class FsmCommandActionProxy<S extends Serializable> implements FsmCommandAction<S> {
    String              action;
    FsmCommandAction<S> commandAction;

    public FsmCommandActionProxy(String action) {
        this.action = action;
    }


    @Override
    public String code() {
        return action;
    }

    @Override
    public void execute(FsmContext<S> ctx) throws ActionException {
        try {
            commandAction.execute(ctx);
        } catch (Exception e) {
            throw new ActionException(e);
        }
    }

    public Action getOrInitAction() {
        if (null == commandAction) {
            synchronized (this) {
//                this.sagaAction = getBean(action, SagaAction.class);
                this.commandAction = new NoneFsmAction();
            }
        }
        return this;
    }
}
