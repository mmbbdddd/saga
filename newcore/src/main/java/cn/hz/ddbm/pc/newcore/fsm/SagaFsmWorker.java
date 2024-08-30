package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaActionProxy;

import java.io.Serializable;

public class SagaFsmWorker<S extends Serializable> extends FsmWorker<S> implements Action {
    S               from;
    S               failover;
    String          action;
    Action sagaAction;


    public SagaFsmWorker(S from, String sagaAction, S failover) {
        this.from       = from;
        this.failover   = failover;
        this.action     = sagaAction;
        this.sagaAction = new SagaActionProxy(this.action);
    }

    @Override
    public void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException {

    }


    public Action getAction() {
        return sagaAction;
    }

    @Override
    public String code() {
        return null;
    }

}