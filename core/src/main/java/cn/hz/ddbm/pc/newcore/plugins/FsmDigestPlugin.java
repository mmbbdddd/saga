//package cn.hz.ddbm.pc.newcore.plugins;
//
//import cn.hz.ddbm.pc.common.lang.Triple;
//import cn.hz.ddbm.pc.newcore.FlowContext;
//import cn.hz.ddbm.pc.newcore.FlowStatus;
//import cn.hz.ddbm.pc.newcore.Plugin;
//import cn.hz.ddbm.pc.newcore.State;
//import cn.hz.ddbm.pc.newcore.fsm.FsmState;
//import cn.hz.ddbm.pc.newcore.log.Logs;
//
//import java.io.Serializable;
//
//public class FsmDigestPlugin<S extends Enum<S>> extends Plugin<FsmState<S>> {
//    @Override
//    public String code() {
//        return "digest";
//    }
//
//    @Override
//    public void preAction(FlowContext<?, FsmState<S>, ?> ctx) {
//    }
//
//    @Override
//    public void postAction(FsmState<S> preState, FlowContext<?, FsmState<S>, ?> ctx) {
//        String                                 flow   = ctx.getFlow().getName();
//        Serializable                           id     = ctx.getId();
//        Triple<FlowStatus, S, FsmState.Offset> from   = preState.code();
//        String                                 action = ctx.getAction().code();
//        Triple<FlowStatus, S, FsmState.Offset> target = ctx.getState().code();
//
//        Logs.digest.info("{},{},{}.{}==>{}", flow, id, from, action, target);
//    }
//
//    @Override
//    public void errorAction(FsmState<S> preState, Exception e, FlowContext<?, FsmState<S>, ?> ctx) {
//        Logs.error.warn("",e);
//    }
//
//    @Override
//    public void finallyAction(State preNode, FlowContext<?, FsmState<S>, ?> ctx) {
//
//    }
//
//
//}
