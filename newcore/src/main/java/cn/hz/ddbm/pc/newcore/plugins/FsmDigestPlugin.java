package cn.hz.ddbm.pc.newcore.plugins;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;
import cn.hz.ddbm.pc.newcore.log.Logs;

import java.io.Serializable;

public class FsmDigestPlugin<S extends Enum<S>> extends Plugin<FsmFlow<S>, FsmState<S>, FsmWorker<S>> {
    @Override
    public String code() {
        return "digest";
    }

    @Override
    public void preAction(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) {

    }

    @Override
    public void postAction(FsmState<S> preState, FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) {
        String                   flow         = ctx.getFlow().getName();
        Serializable             id           = ctx.getId();
        Pair<S, FsmState.Offset> from         = preState.code();
        String                   action       = ctx.getAction().code();
        Object                   actionResult = ctx.getActionResult();
        Pair<S, FsmState.Offset> targetStatus = ctx.getState().code();

        Logs.digest.info("{},{},{}_{}.{}[{}  ]==>{}_{}", flow, id, from.getKey(),from.getValue(), action, actionResult, targetStatus.getKey(),targetStatus.getValue());
    }

    @Override
    public void errorAction(FsmState<S> preState, Exception e, FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) {

    }

    @Override
    public void finallyAction(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) {

    }


}
