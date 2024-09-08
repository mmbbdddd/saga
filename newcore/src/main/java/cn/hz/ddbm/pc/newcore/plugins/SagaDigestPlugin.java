package cn.hz.ddbm.pc.newcore.plugins;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.saga.SagaState;

import java.io.Serializable;

public class SagaDigestPlugin<S extends Enum<S>> extends Plugin<SagaState<S>> {
    @Override
    public String code() {
        return "digest";
    }



    @Override
    public void preAction(FlowContext<?, SagaState<S>, ?> ctx) {

    }

    @Override
    public void postAction(SagaState<S> lastNode, FlowContext<?, SagaState<S>, ?> ctx) {

        String       flow         = ctx.getFlow().getName();
        Serializable id           = ctx.getId();
        Serializable from         = lastNode.code();
        String       action       = ctx.getAction().code();
        Serializable targetStatus = ctx.getState().code();

        Logs.digest.info(" {},{}, {},{},{}",  flow, id, from, action,   targetStatus);

    }

    @Override
    public void errorAction(SagaState<S> preNode, Exception e, FlowContext<?, SagaState<S>, ?> ctx) {

    }

    @Override
    public void finallyAction(FlowContext<?, SagaState<S>, ?> ctx) {
//        log.info("{},{},{},{}", ctx.getFlow().getName(), ctx.getId(),  ctx.getAction().code(), ctx.getActionResult());
//        log.info("{},{},{},{}", ctx.getFlow().getName(), ctx.getId(),  ctx.getStatus(), ctx.getState());
    }


}
