package cn.hz.ddbm.pc.newcore.plugins;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.log.Logs;

import java.io.Serializable;

public class FsmDigestPlugin<S extends Enum<S>> extends Plugin<FsmState<S>> {
    @Override
    public String code() {
        return "digest";
    }

    @Override
    public void preAction(FlowContext<?, FsmState<S>, ?> ctx) {

    }

    @Override
    public void postAction(FsmState<S> preState, FlowContext<?, FsmState<S>, ?> ctx) {
        String       flow   = ctx.getFlow().getName();
        Serializable id     = ctx.getId();
        S            from   = preState.code();
        String       action = ctx.getAction().code();
        S            target = ctx.getState().code();

        Logs.digest.info("{},{},{}.{}==>{}", flow, id, from, action, target);
    }

    @Override
    public void errorAction(FsmState<S> preState, Exception e, FlowContext<?, FsmState<S>, ?> ctx) {

    }

    @Override
    public void finallyAction(FlowContext<?, FsmState<S>, ?> ctx) {

    }


}
