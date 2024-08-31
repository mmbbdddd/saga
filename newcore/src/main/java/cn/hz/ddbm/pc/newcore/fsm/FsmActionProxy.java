package cn.hz.ddbm.pc.newcore.fsm;


import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.test.NoneFsmAction;

import java.io.Serializable;

public class FsmActionProxy<S extends Serializable> implements FsmCommandAction<S>, FsmSagaAction<S> {
    String              action;
    FsmCommandAction<S> commandAction;
    FsmSagaAction<S>    sagaAction;
    FsmWorker<S>        fsmWorker;

    public FsmActionProxy(FsmWorker<S> fsmWorker, String action) {
        this.fsmWorker = fsmWorker;
        this.action    = action;
    }


    @Override
    public String code() {
        return getOrInitAction().code();
    }

    @Override
    public void command(FsmContext<S> ctx) throws ActionException {
        try {
            commandAction.command(ctx);
        } catch (Exception e) {
            throw new ActionException(e);
        }
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
        if(this.fsmWorker instanceof ToFsmWorker){
            if (null == commandAction) {
                synchronized (this) {
//                this.sagaAction = getBean(action, SagaAction.class);
                    this.commandAction = new NoneFsmAction();
                }
            }
        }else{
            if (null == sagaAction) {
                synchronized (this) {
//                this.sagaAction = getBean(action, SagaAction.class);
                    this.sagaAction = new NoneFsmAction();
                }
            }
        }
        return this;
    }
}
