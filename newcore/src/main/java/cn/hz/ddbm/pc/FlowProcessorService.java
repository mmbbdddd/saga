package cn.hz.ddbm.pc;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.FlowProcessor;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;
import cn.hz.ddbm.pc.newcore.saga.SagaState;

import java.io.Serializable;

public abstract class FlowProcessorService<C extends FlowContext> implements FlowProcessor<C> {
    //todo

    public PluginService plugin() {
        return new PluginService();
    }

    public Integer getStateExecuteTimes(FlowContext ctx, String flow, State state) {
        return ctx.getExecuteTimes().get();
    }

    public boolean tryLock(FlowContext ctx) {
        return true;
    }

    public void updateStatus(FlowContext ctx) throws StatusException {

    }

    public void idempotent(String name, Serializable id, SagaState state, String event) throws IdempotentException {

    }


    public void unidempotent(String name, Serializable id, State state, String event) throws IdempotentException {

    }

    public void unLock(FlowContext ctx) {

    }


}


