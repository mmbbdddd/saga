package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.Worker;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;
import cn.hz.ddbm.pc.newcore.saga.SagaActionProxy;
import lombok.Data;

import java.io.Serializable;
@Data
public abstract class FsmWorker<S extends Serializable> extends Worker<FsmContext<S>> {

    @Override
    public abstract void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException ;

}

